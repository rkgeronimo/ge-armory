package hr.gearmory.app.remote

import android.os.Build
import android.util.Base64
import hr.gearmory.app.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

internal val ReservationBase: String = if (BuildConfig.DEBUG) {
    val host = if (isEmulator()) "10.0.2.2" else "10.1.2.161"
    "http://$host:8080/wp-json/rkg/v1"
} else {
    "https://rkgeronimo.hr/wp-json/rkg/v1"
}

private fun isEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT
    return fingerprint.startsWith("generic")
        || fingerprint.contains("emulator")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for")
        || Build.HARDWARE.contains("goldfish")
        || Build.HARDWARE.contains("ranchu")
        || Build.PRODUCT.contains("sdk")
}

internal val equipmentKeys = listOf(
    "mask",
    "regulator",
    "suit",
    "boots",
    "gloves",
    "fins",
    "bcd",
    "lead",
)

internal data class StaffCredentials(
    val username: String,
    val applicationPassword: String,
) {
    fun header(): String {
        val token = "$username:${applicationPassword.replace(" ", "")}"
        val encoded = Base64.encodeToString(token.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return "Basic $encoded"
    }
}

internal data class ReservedPiece(
    val label: String,
    val size: String?,
    val code: String?,
    val needed: Boolean,
    val returned: Int?,
)

internal data class Reservation(
    val id: Long,
    val userId: Long,
    val userName: String,
    val excursionId: Long?,
    val excursion: String?,
    val state: Int,
    val status: String,
    val comment: String?,
    val equipment: Map<String, ReservedPiece>,
)

internal class ReservationException(
    val http: Int,
    val code: String?,
) : Exception(code ?: http.toString())

internal fun reservationNotice(error: Throwable): String = when (error) {
    is ReservationException -> when (error.code) {
        "rkg_forbidden" -> "Nema ovlasti."
        "rkg_invalid_return" -> "Oznaka nije valjana."
        "rkg_not_found" -> "Zahtjev nije pronađen."
        "rkg_unavailable" -> "Zahtjev nije dostupan."
        else -> "Stranica nije odgovorila."
    }
    else -> "Stranica nije dostupna."
}

internal fun Reservation.listLabel(): String {
    val trip = excursion?.takeIf { it.isNotBlank() }
    return if (trip == null) userName else "$userName  $trip"
}

internal fun Reservation.openPieces(): List<Pair<String, ReservedPiece>> =
    equipmentKeys.mapNotNull { key ->
        val piece = equipment[key] ?: return@mapNotNull null
        if (!piece.needed || piece.code.isNullOrBlank()) return@mapNotNull null
        if (piece.returned == 0 || piece.returned == 3) return@mapNotNull null
        key to piece
    }

internal fun pieceTitle(key: String, piece: ReservedPiece): String =
    if (key == "lead") "Olovo" else piece.label

internal fun pieceMark(key: String, piece: ReservedPiece): String =
    if (key == "lead") piece.size ?: piece.code.orEmpty() else piece.code.orEmpty()

internal fun pieceSize(key: String, piece: ReservedPiece): String? =
    if (key == "lead") null else piece.size?.takeIf { it.isNotBlank() }

internal object ReservationClient {
    fun listActive(credentials: StaffCredentials): List<Reservation> {
        val body = call(credentials, "GET", "/reservations?state=1", null)
        val array = JSONArray(body)
        return List(array.length()) { index -> parseReservation(array.getJSONObject(index)) }
    }

    fun updateReturned(
        credentials: StaffCredentials,
        id: Long,
        returnedByKey: Map<String, Int>,
    ): Reservation {
        val equipment = JSONObject()
        returnedByKey.forEach { (key, returned) ->
            if (key !in equipmentKeys || returned !in setOf(0, 1, 3)) return@forEach
            equipment.put(key, JSONObject().put("returned", returned))
        }
        val body = call(
            credentials,
            "POST",
            "/reservations/$id",
            JSONObject().put("equipment", equipment).toString(),
        )
        return parseReservation(JSONObject(body))
    }

    private fun call(
        credentials: StaffCredentials,
        method: String,
        path: String,
        json: String?,
    ): String {
        val url = URL(ReservationBase + path)
        val https = url.protocol.equals("https", ignoreCase = true)
        val localHttp = BuildConfig.DEBUG && url.protocol.equals("http", ignoreCase = true)
        if (!https && !localHttp) {
            throw ReservationException(0, null)
        }
        val connection = url.openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false
        connection.requestMethod = method
        connection.connectTimeout = 20_000
        connection.readTimeout = 20_000
        connection.setRequestProperty("Authorization", credentials.header())
        connection.setRequestProperty("Accept", "application/json")
        if (json != null) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { stream ->
                stream.write(json.toByteArray(Charsets.UTF_8))
            }
        }
        val http = connection.responseCode
        if (http in 300..399) {
            connection.disconnect()
            throw ReservationException(http, null)
        }
        val text = (if (http in 200..299) connection.inputStream else connection.errorStream)
            ?.bufferedReader(Charsets.UTF_8)
            ?.use { it.readText() }
            .orEmpty()
        if (http !in 200..299) {
            throw ReservationException(http, errorCode(text))
        }
        return text
    }

    private fun errorCode(text: String): String? = try {
        JSONObject(text).optNullableString("code")
    } catch (_: Exception) {
        null
    }

    private fun parseReservation(json: JSONObject): Reservation {
        val equipmentJson = json.optJSONObject("equipment") ?: JSONObject()
        val equipment = equipmentKeys.mapNotNull { key ->
            val item = equipmentJson.optJSONObject(key) ?: return@mapNotNull null
            key to ReservedPiece(
                label = item.optString("label"),
                size = item.optNullableString("size"),
                code = item.optNullableString("code"),
                needed = item.optBoolean("needed", false),
                returned = if (item.isNull("returned")) null else item.getInt("returned"),
            )
        }.toMap()
        return Reservation(
            id = json.getLong("id"),
            userId = json.getLong("user_id"),
            userName = json.optString("user_name"),
            excursionId = if (json.isNull("excursion_id")) null else json.getLong("excursion_id"),
            excursion = json.optNullableString("excursion"),
            state = json.getInt("state"),
            status = json.optString("status"),
            comment = json.optNullableString("comment"),
            equipment = equipment,
        )
    }
}

private fun JSONObject.optNullableString(name: String): String? {
    if (!has(name) || isNull(name)) return null
    return optString(name)
}
