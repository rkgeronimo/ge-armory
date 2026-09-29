package hr.gearmory.app.feature.inventory

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.gearmory.app.feature.equipment.equipmentTypes
import hr.gearmory.app.feature.equipment.pieceConditions
import hr.gearmory.app.feature.equipment.pieceSize

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InventoryScreen() {
    val viewModel: InventoryViewModel = viewModel()
    var type by rememberSaveable { mutableStateOf(equipmentTypes.first()) }
    var size by rememberSaveable { mutableStateOf("") }
    var regulator by rememberSaveable { mutableStateOf("") }
    var typed by rememberSaveable { mutableStateOf("") }
    var condition by rememberSaveable { mutableStateOf("Dobro") }
    val sizes = inventorySizes(type)
    val brands = if (type == "Regulator") regulatorTypes else emptyList()
    val prefix = if (type == "Regulator") regulator else size
    val code = prefix + typed
    val duplicate = code.isNotEmpty() && viewModel.entries.any { it.type == type && it.code == code }
    val needsChoice = sizes.isNotEmpty() || brands.isNotEmpty()
    val canEnter = typed.isNotBlank() && (!needsChoice || prefix.isNotBlank()) && !duplicate
    val visible = viewModel.entries.take(5)
    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TypeChips(
                type = type,
                onType = { option ->
                    type = option
                    if (size !in inventorySizes(option)) size = ""
                    if (option != "Regulator") regulator = ""
                },
            )
            Spacer(Modifier.height(16.dp))
            EntryForm(
                sizes = sizes,
                size = size,
                onSize = { size = it },
                brands = brands,
                regulator = regulator,
                onRegulator = { regulator = it },
                condition = condition,
                onCondition = { condition = it },
                code = code,
                duplicate = duplicate,
                canEnter = canEnter,
                onKey = { key ->
                    typed = when (key) {
                        "⌫" -> typed.dropLast(1)
                        else -> (typed + key).filter { it.isDigit() || it == 'X' }.take(12)
                    }
                },
                onEnter = {
                    viewModel.add(type, code, condition)
                    typed = ""
                    size = ""
                    regulator = ""
                    condition = "Dobro"
                },
            )
            if (visible.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                EntryList(
                    entries = visible,
                    onRemove = viewModel::remove,
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        TypeChips(
            type = type,
            onType = { option ->
                type = option
                if (size !in inventorySizes(option)) size = ""
                if (option != "Regulator") regulator = ""
            },
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .verticalScroll(rememberScrollState()),
            ) {
                EntryForm(
                    sizes = sizes,
                    size = size,
                    onSize = { size = it },
                    brands = brands,
                    regulator = regulator,
                    onRegulator = { regulator = it },
                    condition = condition,
                    onCondition = { condition = it },
                    code = code,
                    duplicate = duplicate,
                    canEnter = canEnter,
                    onKey = { key ->
                        typed = when (key) {
                            "⌫" -> typed.dropLast(1)
                            else -> (typed + key).filter { it.isDigit() || it == 'X' }.take(12)
                        }
                    },
                    onEnter = {
                        viewModel.add(type, code, condition)
                        typed = ""
                        size = ""
                        regulator = ""
                        condition = "Dobro"
                    },
                )
            }
            Spacer(Modifier.size(16.dp))
            EntryList(
                entries = visible,
                onRemove = viewModel::remove,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TypeChips(
    type: String,
    onType: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        equipmentTypes.forEach { option ->
            SelectChip(
                label = option,
                selected = option == type,
                onClick = { onType(option) },
            )
        }
    }
}

@Composable
private fun EntryList(
    entries: List<InventoryEntry>,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            EntryRow(
                type = "Vrsta opreme",
                code = "Šifra",
                size = "Veličina",
                condition = "Stanje",
                header = true,
                onRemove = {},
            )
            if (entries.isEmpty()) {
                Text(
                    text = "Nema unosa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            entries.forEach { entry ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                EntryRow(
                    type = entry.type,
                    code = entry.code,
                    size = pieceSize(entry.type, entry.code).ifBlank { "—" },
                    condition = entry.condition,
                    header = false,
                    onRemove = { onRemove(entry.id) },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.EntryForm(
    sizes: List<String>,
    size: String,
    onSize: (String) -> Unit,
    brands: List<Pair<String, String>>,
    regulator: String,
    onRegulator: (String) -> Unit,
    condition: String,
    onCondition: (String) -> Unit,
    code: String,
    duplicate: Boolean,
    canEnter: Boolean,
    onKey: (String) -> Unit,
    onEnter: () -> Unit,
) {
    Text(
        text = "Stanje",
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        pieceConditions.forEach { option ->
            SelectChip(
                label = option,
                selected = option == condition,
                onClick = { onCondition(option) },
                modifier = Modifier.weight(1f),
            )
        }
    }
    if (brands.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Tip regulatora",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            brands.forEach { (label, prefix) ->
                SelectChip(
                    label = label,
                    selected = prefix == regulator,
                    onClick = { onRegulator(prefix) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
    if (sizes.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Veličina",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sizes.forEach { option ->
                SelectChip(
                    label = option,
                    selected = option == size,
                    onClick = { onSize(option) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally),
    ) {
        OutlinedTextField(
            value = code,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Šifra") },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            singleLine = true,
        )
        Spacer(Modifier.height(12.dp))
        NumberPad(onKey = onKey)
        Spacer(Modifier.height(12.dp))
        if (duplicate) {
            Text(
                text = "Već uneseno",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = onEnter,
            enabled = canEnter,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Unesi", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun EntryRow(
    type: String,
    code: String,
    size: String,
    condition: String,
    header: Boolean,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = type,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (header) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        Text(
            text = code,
            modifier = Modifier.weight(1.1f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (header) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        Text(
            text = size,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (header) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        Text(
            text = condition,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (header) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        if (header) {
            Spacer(Modifier.size(48.dp))
        } else {
            IconButton(onClick = onRemove, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Rounded.Close, contentDescription = "Makni redak")
            }
        }
    }
}

@Composable
private fun SelectChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun NumberPad(onKey: (String) -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("X", "0", "⌫"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { key ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable { onKey(key) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (key == "X") {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (key == "X") {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shadowElevation = 1.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (key == "⌫") {
                                Icon(
                                    Icons.Rounded.Backspace,
                                    contentDescription = "Obriši zadnji znak",
                                )
                            } else {
                                Text(
                                    text = key,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
