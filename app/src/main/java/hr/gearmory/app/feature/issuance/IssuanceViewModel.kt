package hr.gearmory.app.feature.issuance

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import hr.gearmory.app.feature.equipment.typedNumberLimit
import hr.gearmory.app.feature.inventory.regulatorTypes

internal val dummyMembers = listOf(
    "Ana Kovač",
    "Marko Marić",
    "Ivan Horvat",
    "Petra Babić",
    "Luka Jurić",
    "Ema Novak",
    "Toni Radić",
    "Mia Perić",
)

internal const val LoanMaskaDisalica = "Maska i Disalica"
internal const val LoanOlovo = "Olovo"

internal val loanEquipmentTypes = listOf(
    "Odijelo",
    "Čizmice",
    "Peraje",
    "Kompenzator",
    "Regulator",
    LoanMaskaDisalica,
    LoanOlovo,
)

internal fun loanField(type: String, size: String, number: String): String = when (type) {
    "Čizmice" -> "B$number"
    "Kompenzator" -> "J$number"
    "Peraje", "Regulator" -> size + number
    LoanMaskaDisalica -> "MD$number"
    else -> number
}

internal fun loanShown(type: String, size: String, number: String): String {
    if (type == LoanOlovo) return number
    if (number.isEmpty()) return ""
    val code = loanField(type, size, number)
    return if (type == "Kompenzator" && size.isNotEmpty()) "$code $size" else code
}

internal fun loanReady(type: String, size: String, number: String): Boolean {
    if (number.isBlank()) return false
    return when (type) {
        "Peraje", "Kompenzator" -> size.isNotBlank()
        "Regulator" -> regulatorTypes.any { it.second == size }
        else -> true
    }
}

internal class IssuanceViewModel : ViewModel() {
    val equipmentValues = mutableStateMapOf<String, String>().apply {
        loanEquipmentTypes.forEach { put(it, "") }
    }
    val equipmentSizes = mutableStateMapOf<String, String>()

    fun pressKey(equipment: String, key: String) {
        val current = equipmentValues[equipment].orEmpty()
        val candidate = when (key) {
            "⌫" -> current.dropLast(1)
            "X" -> if (equipment == LoanOlovo) current else current + key
            else -> current + key
        }
        equipmentValues[equipment] = if (equipment == LoanOlovo) {
            candidate.filter { it.isDigit() }.trimStart('0').take(9)
        } else {
            candidate
                .uppercase()
                .filter { it.isDigit() || it == 'X' }
                .take(typedNumberLimit(equipment))
        }
    }

    fun clearEquipment(equipment: String) {
        equipmentValues[equipment] = ""
        equipmentSizes.remove(equipment)
    }

    fun clearAll() {
        loanEquipmentTypes.forEach { equipmentValues[it] = "" }
        equipmentSizes.clear()
    }
}
