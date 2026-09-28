package hr.gearmory.app.feature.inventory

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

internal data class InventoryEntry(
    val id: String,
    val type: String,
    val code: String,
    val condition: String,
)

internal fun inventorySizes(type: String): List<String> = when (type) {
    "Peraje" -> listOf("S", "R", "XL")
    "Kompenzator" -> listOf("XS", "S", "M", "L", "XL", "XXL")
    "Rukavice" -> listOf("S", "M", "L", "XL")
    else -> emptyList()
}

internal class InventoryViewModel : ViewModel() {
    val entries = mutableStateListOf<InventoryEntry>()
    private var nextId = 1

    fun add(type: String, code: String, condition: String) {
        if (code.isBlank()) return
        if (entries.any { it.type == type && it.code == code }) return
        entries.add(0, InventoryEntry(nextId++.toString(), type, code, condition))
    }

    fun remove(id: String) {
        entries.removeAll { it.id == id }
    }
}
