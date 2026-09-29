package hr.gearmory.app.feature.inventory

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

internal data class InventoryEntry(
    val id: String,
    val type: String,
    val code: String,
    val condition: String,
    val quantity: String,
    val note: String = "",
    val thickness: String = "",
    val size: String = "",
)

internal class InventoryViewModel(app: Application) : AndroidViewModel(app) {
    val entries = mutableStateListOf<InventoryEntry>()
    var notice by mutableStateOf<String?>(null)
    private val store = InventoryFileStore(app)
    private var blocked = false

    fun reload() {
        store.ensure()
        when (val load = store.read()) {
            is InventoryLoad.Ready -> {
                blocked = false
                entries.clear()
                entries.addAll(load.entries)
                notice = if (load.dropped > 0 && store.write(load.entries)) {
                    skippedRowsNotice(load.dropped)
                } else if (load.dropped > 0) {
                    CsvWriteError
                } else {
                    null
                }
            }
            InventoryLoad.Corrupt -> {
                blocked = true
                notice = CsvReadError
                entries.clear()
            }
            InventoryLoad.Unavailable -> {
                blocked = true
                notice = CsvWriteError
                entries.clear()
            }
        }
    }

    fun addPiece(type: String, code: String, condition: String, thickness: String, size: String) {
        if (blocked || !pieceCodeOk(type, code) || !thicknessOk(type, thickness) || !sizeOk(type, size)) return
        if (entries.any { it.type == type && it.code == code }) return
        val row = InventoryEntry(freshId(), type, code, condition, "", thickness = thickness, size = size)
        commit(listOf(row) + entries)
    }

    fun setOstalo(values: Map<String, String>) {
        if (blocked) return
        val updates = quantityTypes.mapNotNull { type ->
            val count = normalizeQuantity(values[type].orEmpty()) ?: return@mapNotNull null
            type to count
        }
        if (updates.isEmpty()) return
        var next = entries.toList()
        val usedIds = next.mapNotNull { it.id.toIntOrNull() }.toMutableSet()
        val rows = updates.map { (type, count) ->
            val previous = next.firstOrNull { it.type == type && it.isQuantity() }
            val id = previous?.id ?: run {
                val fresh = ((usedIds.maxOrNull() ?: 0) + 1).toString()
                usedIds += fresh.toInt()
                fresh
            }
            InventoryEntry(id, type, "", "", count, "")
        }
        val ids = rows.map { it.id }.toSet()
        commit(rows + next.filterNot { it.id in ids })
    }

    fun setQuantity(type: String, raw: String, note: String = "") {
        if (blocked) return
        val name = when {
            type in quantityTypes -> type
            else -> raznoNameOk(type) ?: return
        }
        val count = normalizeQuantity(raw) ?: return
        val storedNote = if (name in quantityTypes) "" else napomenaOk(note)
        val previous = entries.firstOrNull { it.type == name && it.isQuantity() }
        val row = InventoryEntry(previous?.id ?: freshId(), name, "", "", count, storedNote)
        val rest = entries.filterNot { it.id == row.id }
        commit(listOf(row) + rest)
    }

    fun remove(id: String) {
        if (blocked || entries.none { it.id == id }) return
        commit(entries.filterNot { it.id == id })
    }

    fun clearAll() {
        if (blocked || entries.isEmpty()) return
        commit(emptyList())
    }

    fun updatePiece(id: String, code: String, condition: String, thickness: String, size: String): Boolean {
        if (blocked) return false
        val index = entries.indexOfFirst { it.id == id }
        if (index < 0) return false
        val current = entries[index]
        if (current.isQuantity() || !pieceCodeOk(current.type, code)) return false
        if (condition !in hr.gearmory.app.feature.equipment.pieceConditions) return false
        if (!thicknessOk(current.type, thickness) || !sizeOk(current.type, size)) return false
        if (entries.any { it.id != id && it.type == current.type && it.code == code }) return false
        val next = entries.toMutableList()
        next[index] = current.copy(
            code = code,
            condition = condition,
            quantity = "",
            thickness = thickness,
            size = size,
        )
        return commit(next)
    }

    fun updateQuantity(id: String, type: String, raw: String, note: String = ""): Boolean {
        if (blocked) return false
        val index = entries.indexOfFirst { it.id == id }
        if (index < 0) return false
        val current = entries[index]
        if (!current.isQuantity()) return false
        val name = if (current.type in quantityTypes) {
            current.type
        } else {
            raznoNameOk(type) ?: return false
        }
        val count = normalizeQuantity(raw) ?: return false
        val storedNote = if (name in quantityTypes) "" else napomenaOk(note)
        if (entries.any { it.id != id && it.type == name && it.isQuantity() }) return false
        val next = entries.toMutableList()
        next[index] = current.copy(type = name, code = "", condition = "", quantity = count, note = storedNote)
        return commit(next)
    }

    fun quantityOf(type: String): String =
        entries.firstOrNull { it.type == type && it.isQuantity() }?.quantity.orEmpty()

    private fun commit(next: List<InventoryEntry>): Boolean {
        if (!store.write(next)) {
            notice = CsvWriteError
            return false
        }
        notice = null
        entries.clear()
        entries.addAll(next)
        return true
    }

    private fun freshId(): String {
        val max = entries.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
        return (max + 1).toString()
    }
}
