package hr.gearmory.app.feature.equipment

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

internal data class Piece(
    val id: String,
    val type: String,
    val code: String,
    val condition: String,
    val status: String,
    val holder: String? = null,
    val issuedOn: String? = null,
    val size: String = "",
)

internal val equipmentTypes = listOf(
    "Odijelo",
    "Čizmice",
    "Peraje",
    "Kompenzator",
    "Rukavice",
    "Regulator",
)

internal val pieceConditions = listOf(
    "Novo",
    "Dobro",
    "Za otpis",
    "Neispravno",
)

internal const val StatusInStockLabel = "Na stanju"
internal const val StatusIssuedLabel = "Izdano"

internal fun typedNumberLimit(type: String): Int = when (type) {
    "Odijelo", "Čizmice" -> 4
    else -> 2
}

internal fun codeLetter(type: String): String = when (type) {
    "Čizmice" -> "B"
    "Kompenzator" -> "J"
    "Rukavice" -> "G"
    else -> ""
}

internal fun pieceSize(type: String, code: String): String {
    val letter = codeLetter(type)
    val body = if (letter.isEmpty() || code.startsWith(letter)) code.removePrefix(letter) else return ""
    val prefixes = when (type) {
        "Peraje" -> listOf("XL", "S", "R")
        "Rukavice" -> listOf("XL", "S", "M", "L")
        "Odijelo", "Čizmice" -> return digitSize(body)
        else -> return ""
    }
    return prefixes.firstOrNull { body.startsWith(it) }.orEmpty()
}

private fun digitSize(body: String): String {
    if (body.length >= 2 && body[0].isDigit() && body[1].isDigit()) {
        return body.take(2).trimStart('0').ifEmpty { "0" }
    }
    return ""
}

internal class EquipmentViewModel : ViewModel() {
    val pieces = mutableStateListOf(
        Piece("1", "Odijelo", "0512", "Dobro", StatusIssuedLabel, "Ana Kovač", "12. 9. 2026."),
        Piece("2", "Odijelo", "0603", "Novo", StatusInStockLabel),
        Piece("3", "Čizmice", "B0811", "Dobro", StatusIssuedLabel, "Marko Marić", "12. 9. 2026."),
        Piece("4", "Čizmice", "B1204", "Za otpis", StatusInStockLabel),
        Piece("5", "Peraje", "R12", "Dobro", StatusInStockLabel),
        Piece("6", "Peraje", "XL3", "Novo", StatusIssuedLabel, "Petra Babić", "20. 9. 2026."),
        Piece("7", "Kompenzator", "J12", "Dobro", StatusInStockLabel, size = "M"),
        Piece("8", "Kompenzator", "J04", "Neispravno", StatusInStockLabel, size = "XS"),
        Piece("9", "Rukavice", "GM8", "Dobro", StatusIssuedLabel, "Ana Kovač", "1. 9. 2026."),
        Piece("10", "Rukavice", "GL2", "Novo", StatusInStockLabel),
        Piece("11", "Regulator", "12", "Dobro", StatusInStockLabel),
        Piece("12", "Regulator", "7", "Za otpis", StatusIssuedLabel, "Marko Marić", "5. 9. 2026."),
    )

    val checkedIds = mutableStateListOf<String>()

    fun toggleChecked(id: String) {
        if (id in checkedIds) checkedIds.remove(id) else checkedIds.add(id)
    }

    fun deleteChecked() {
        pieces.removeAll { it.id in checkedIds }
        checkedIds.clear()
    }

    fun update(id: String, type: String, code: String, condition: String) {
        val index = pieces.indexOfFirst { it.id == id }
        if (index < 0) return
        val current = pieces[index]
        pieces[index] = current.copy(type = type, code = code, condition = condition)
    }
}
