package hr.gearmory.app.feature.inventory

import hr.gearmory.app.feature.equipment.equipmentTypes
import hr.gearmory.app.feature.equipment.pieceConditions
import hr.gearmory.app.feature.equipment.pieceSize
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction

internal const val CsvHeader = "Vrsta;Sifra;Velicina;Stanje;Kolicina;Napomena"
private const val LegacyCsvHeader = "Vrsta;Sifra;Velicina;Stanje;Kolicina"
internal const val CsvReadError = "Csv se ne da čitati."
internal const val CsvWriteError = "Csv nije zapisan."
internal const val ChipOstalo = "Ostalo"
internal const val ChipRazno = "Razno"

internal val quantityTypes = listOf(
    "Olovo 1kg",
    "Olovo 2kg",
    "Pojas",
    "Maska",
    "Disalica",
    "Kadica",
)

internal val inventoryChips = equipmentTypes + ChipOstalo + ChipRazno

internal val regulatorTypes = listOf(
    "Apeks" to "RA",
    "Mares" to "RM",
    "Scubapro" to "RS",
)

internal fun inventorySizes(type: String): List<String> = when (type) {
    "Peraje" -> listOf("S", "R", "XL")
    "Kompenzator" -> listOf("XS", "S", "M", "L", "XL", "XXL")
    "Rukavice" -> listOf("S", "M", "L", "XL")
    else -> emptyList()
}

private val blockedRaznoNames = buildSet {
    addAll(equipmentTypes)
    addAll(quantityTypes)
    add("Cizmice")
    add(ChipOstalo)
    add(ChipRazno)
}

internal fun InventoryEntry.isQuantity(): Boolean = type !in equipmentTypes

internal fun toFileType(type: String): String = when (type) {
    "Čizmice" -> "Cizmice"
    "Dišalica" -> "Disalica"
    else -> type
}

internal fun fromFileType(type: String): String = when (type) {
    "Cizmice" -> "Čizmice"
    "Dišalica" -> "Disalica"
    else -> type
}

internal fun pieceCodeOk(type: String, code: String): Boolean {
    if (code.isEmpty() || type !in equipmentTypes) return false
    val sizes = inventorySizes(type).sortedByDescending { it.length }
    return when (type) {
        "Peraje", "Kompenzator", "Rukavice" -> {
            val size = sizes.firstOrNull { code.startsWith(it) } ?: return false
            val rest = code.removePrefix(size)
            rest.isNotEmpty() && rest.length <= 12 && rest.all { it.isDigit() || it == 'X' }
        }
        "Regulator" -> {
            val prefix = regulatorTypes.firstOrNull { code.startsWith(it.second) }?.second ?: return false
            val rest = code.removePrefix(prefix)
            rest.isNotEmpty() && rest.length <= 12 && rest.all { it.isDigit() || it == 'X' }
        }
        else -> code.length <= 12 && code.all { it.isDigit() || it == 'X' }
    }
}

internal fun normalizeQuantity(raw: String): String? {
    val text = raw.trim()
    if (text.isEmpty() || text.any { !it.isDigit() }) return null
    if (text.length > 1 && text.startsWith('0')) return null
    val value = text.toLongOrNull() ?: return null
    if (value <= 0L) return null
    return value.toString()
}

internal fun raznoNameOk(raw: String): String? {
    val name = raw.trim()
    if (name.isEmpty()) return null
    if (name.any { it.code > 127 || it == ';' || it == '"' || it == '\n' || it == '\r' }) return null
    if (name in blockedRaznoNames) return null
    return name
}

internal fun napomenaOk(raw: String): String {
    val folded = raw
        .replace('č', 'c')
        .replace('ć', 'c')
        .replace('ž', 'z')
        .replace('š', 's')
        .replace('đ', 'd')
        .replace('Č', 'C')
        .replace('Ć', 'C')
        .replace('Ž', 'Z')
        .replace('Š', 'S')
        .replace('Đ', 'D')
    return folded
        .filter { it.code <= 127 && it != ';' && it != '"' && it != '\n' && it != '\r' }
        .trim()
}

internal fun popisSorted(entries: List<InventoryEntry>): List<InventoryEntry> {
    val rank = (equipmentTypes + quantityTypes).withIndex().associate { it.value to it.index }
    return entries.sortedWith(
        compareBy<InventoryEntry> { rank[it.type] ?: Int.MAX_VALUE }
            .thenBy { if (it.type in rank) "" else it.type }
            .thenBy { it.code },
    )
}

internal fun encodeInventory(entries: List<InventoryEntry>): String = buildString {
    append(CsvHeader)
    append("\r\n")
    entries.forEach { entry ->
        val size = if (entry.isQuantity()) "" else pieceSize(entry.type, entry.code)
        append(toFileType(entry.type))
        append(';')
        append(entry.code)
        append(';')
        append(size)
        append(';')
        append(entry.condition)
        append(';')
        append(entry.quantity)
        append(';')
        append(entry.note)
        append("\r\n")
    }
}

internal fun inventoryBytes(entries: List<InventoryEntry>): ByteArray {
    val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
    return bom + encodeInventory(entries).toByteArray(Charsets.UTF_8)
}

internal fun decodeInventory(bytes: ByteArray): String? {
    val start = if (
        bytes.size >= 3 &&
        bytes[0] == 0xEF.toByte() &&
        bytes[1] == 0xBB.toByte() &&
        bytes[2] == 0xBF.toByte()
    ) {
        3
    } else {
        0
    }
    return try {
        Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes, start, bytes.size - start))
            .toString()
    } catch (_: CharacterCodingException) {
        null
    }
}

internal fun parseInventory(text: String): List<InventoryEntry>? {
    val lines = text.split('\n').map { it.removeSuffix("\r") }
    var end = lines.size
    while (end > 0 && lines[end - 1].isBlank()) end--
    if (end == 0) return null
    val columns = when (lines[0]) {
        CsvHeader -> 6
        LegacyCsvHeader -> 5
        else -> return null
    }
    val parsed = mutableListOf<InventoryEntry>()
    for (index in 1 until end) {
        val line = lines[index]
        if (line.isBlank()) return null
        val fields = parseCsvRow(line) ?: return null
        if (fields.size != columns) return null
        val entry = rowToEntry(fields.map { it.trim() }, index) ?: return null
        parsed += entry
    }
    if (hasDuplicate(parsed)) return null
    return parsed
}

private fun rowToEntry(fields: List<String>, index: Int): InventoryEntry? {
    val type = fromFileType(fields[0])
    val code = fields[1]
    val condition = fields[3]
    val quantity = fields[4]
    val note = fields.getOrElse(5) { "" }
    return when {
        type in equipmentTypes -> pieceEntry(type, code, condition, quantity, note, index)
        type in quantityTypes -> quantityEntry(type, code, fields[2], condition, quantity, note, false, index)
        else -> {
            val name = raznoNameOk(type) ?: return null
            quantityEntry(name, code, fields[2], condition, quantity, note, true, index)
        }
    }
}

private fun pieceEntry(
    type: String,
    code: String,
    condition: String,
    quantity: String,
    note: String,
    index: Int,
): InventoryEntry? {
    if (!pieceCodeOk(type, code)) return null
    if (condition !in pieceConditions) return null
    if (quantity.isNotEmpty() || note.isNotEmpty()) return null
    return InventoryEntry(index.toString(), type, code, condition, "")
}

private fun quantityEntry(
    type: String,
    code: String,
    size: String,
    condition: String,
    quantity: String,
    note: String,
    allowNote: Boolean,
    index: Int,
): InventoryEntry? {
    if (code.isNotEmpty() || size.isNotEmpty() || condition.isNotEmpty()) return null
    val count = normalizeQuantity(quantity) ?: return null
    val storedNote = if (allowNote) napomenaOk(note) else note
    if (!allowNote && storedNote.isNotEmpty()) return null
    return InventoryEntry(index.toString(), type, "", "", count, storedNote)
}

private fun hasDuplicate(entries: List<InventoryEntry>): Boolean {
    val pieces = entries.filter { !it.isQuantity() }
    val quantities = entries.filter { it.isQuantity() }
    if (pieces.size != pieces.map { it.type to it.code }.toSet().size) return true
    if (quantities.size != quantities.map { it.type }.toSet().size) return true
    return false
}

private fun parseCsvRow(line: String): List<String>? {
    val fields = mutableListOf<String>()
    val current = StringBuilder()
    var quoted = false
    var index = 0
    while (index < line.length) {
        val char = line[index]
        if (quoted) {
            if (char == '"') {
                if (index + 1 < line.length && line[index + 1] == '"') {
                    current.append('"')
                    index += 2
                    continue
                }
                quoted = false
                index++
                continue
            }
            current.append(char)
            index++
            continue
        }
        when (char) {
            ';' -> {
                fields += current.toString()
                current.clear()
                index++
            }
            '"' -> {
                quoted = true
                index++
            }
            else -> {
                current.append(char)
                index++
            }
        }
    }
    if (quoted) return null
    fields += current.toString()
    return fields
}
