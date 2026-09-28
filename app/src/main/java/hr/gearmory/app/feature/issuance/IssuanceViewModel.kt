package hr.gearmory.app.feature.issuance

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

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

internal val loanEquipmentTypes = listOf(
    "Odijelo",
    "Maska",
    "Čizmice",
    "Peraje",
    "Kompenzator",
    "Regulator",
)

internal class IssuanceViewModel : ViewModel() {
    val equipmentValues = mutableStateMapOf<String, String>().apply {
        loanEquipmentTypes.forEach { put(it, "") }
    }
    val equipmentSizes = mutableStateMapOf<String, String>()

    fun pressKey(equipment: String, key: String) {
        val current = equipmentValues[equipment].orEmpty()
        val candidate = when (key) {
            "⌫" -> current.dropLast(1)
            else -> current + key
        }
        equipmentValues[equipment] = candidate
            .uppercase()
            .filter { it.isDigit() || it == 'X' }
            .take(12)
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
