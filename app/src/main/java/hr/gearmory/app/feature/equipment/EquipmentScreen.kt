package hr.gearmory.app.feature.equipment

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.gearmory.app.ui.theme.StatusInStock
import hr.gearmory.app.ui.theme.StatusIssued

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun EquipmentScreen() {
    val viewModel: EquipmentViewModel = viewModel()
    var query by rememberSaveable { mutableStateOf("") }
    var statusFilter by rememberSaveable { mutableStateOf("Sve") }
    var typeFilter by rememberSaveable { mutableStateOf("Sve") }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    val editing = viewModel.pieces.firstOrNull { it.id == editingId }
    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val checkedIds = viewModel.checkedIds
    val visible = viewModel.pieces.filter { piece ->
        val statusOk = statusFilter == "Sve" || piece.status == statusFilter
        val typeOk = typeFilter == "Sve" || piece.type == typeFilter
        val text = query.trim()
        val textOk = text.isEmpty() ||
            piece.code.contains(text, ignoreCase = true) ||
            piece.type.contains(text, ignoreCase = true) ||
            piece.holder.orEmpty().contains(text, ignoreCase = true)
        statusOk && typeOk && textOk
    }

    if (editing != null) {
        EquipmentForm(
            piece = editing,
            onSave = { type, code, condition ->
                viewModel.update(editing.id, type, code, condition)
                editingId = null
            },
            onCancel = { editingId = null },
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("Traži šifru, vrstu ili ime") },
                leadingIcon = { IconSearch() },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Sve", StatusInStockLabel, StatusIssuedLabel).forEach { label ->
                    FilterChip(
                        label = label,
                        selected = statusFilter == label,
                        onClick = { statusFilter = label },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                (listOf("Sve") + equipmentTypes).forEach { label ->
                    FilterChip(
                        label = label,
                        selected = typeFilter == label,
                        onClick = { typeFilter = label },
                    )
                }
            }
            if (checkedIds.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = viewModel::deleteChecked,
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text("Briši", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        HorizontalDivider()
        if (visible.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Nema komada",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (isPortrait) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                visible.forEach { piece ->
                    PieceCard(
                        piece = piece,
                        checked = piece.id in checkedIds,
                        onToggle = { viewModel.toggleChecked(piece.id) },
                        onOpen = { editingId = piece.id },
                    )
                }
            }
        } else {
            PieceTable(
                pieces = visible,
                checkedIds = checkedIds,
                onToggle = viewModel::toggleChecked,
                onOpen = { editingId = it },
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun IconSearch() {
    androidx.compose.material3.Icon(
        Icons.Rounded.Search,
        contentDescription = null,
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
    ) {
        Text(label)
    }
}

@Composable
private fun PieceCard(
    piece: Piece,
    checked: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleCheck(checked = checked, onToggle = onToggle)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FieldLabel("Šifra")
                        Spacer(Modifier.width(6.dp))
                        FieldValue(piece.code)
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        FieldValue(piece.type)
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        StatusPill(piece.status)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FieldLabel("Veličina")
                    Spacer(Modifier.width(6.dp))
                    FieldValue(piece.size.ifBlank { pieceSize(piece.type, piece.code) }.ifBlank { "—" })
                }
                if (piece.status == StatusIssuedLabel) {
                    FieldValue(listOfNotNull(piece.holder, piece.issuedOn).joinToString("  "))
                }
            }
        }
    }
}

@Composable
private fun PieceTable(
    pieces: List<Piece>,
    checkedIds: List<String>,
    onToggle: (String) -> Unit,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            Box(Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
            TableRow(
                code = "Šifra",
                type = "Vrsta opreme",
                size = "Veličina",
                status = "Status",
                holder = "Zadužio/la",
                issuedOn = "Datum izdavanja",
                header = true,
                checked = false,
                onToggle = {},
                onOpen = {},
            )
            }
            pieces.forEach { piece ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TableRow(
                code = piece.code,
                type = piece.type,
                size = piece.size.ifBlank { pieceSize(piece.type, piece.code) }.ifBlank { "—" },
                status = piece.status,
                holder = if (piece.status == StatusIssuedLabel) piece.holder.orEmpty() else "",
                issuedOn = if (piece.status == StatusIssuedLabel) piece.issuedOn.orEmpty() else "",
                header = false,
                checked = piece.id in checkedIds,
                showPill = true,
                onToggle = { onToggle(piece.id) },
                onOpen = { onOpen(piece.id) },
            )
        }
        }
    }
}

@Composable
private fun TableRow(
    code: String,
    type: String,
    size: String,
    status: String,
    holder: String,
    issuedOn: String,
    header: Boolean,
    checked: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
    showPill: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(enabled = !header, onClick = onOpen),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (header) {
            Spacer(Modifier.width(48.dp))
        } else {
            CircleCheck(checked = checked, onToggle = onToggle)
        }
        Text(code, modifier = Modifier.weight(0.9f), fontWeight = if (header) FontWeight.SemiBold else FontWeight.Medium)
        Text(type, modifier = Modifier.weight(1.2f), fontWeight = if (header) FontWeight.SemiBold else FontWeight.Normal)
        Text(size, modifier = Modifier.weight(0.7f), fontWeight = if (header) FontWeight.SemiBold else FontWeight.Normal)
        Box(modifier = Modifier.weight(1f)) {
            if (showPill) StatusPill(status) else if (header) {
                Text("Status", fontWeight = FontWeight.SemiBold)
            }
        }
        Text(holder, modifier = Modifier.weight(1.3f), fontWeight = if (header) FontWeight.SemiBold else FontWeight.Normal)
        Text(issuedOn, modifier = Modifier.weight(1.2f), fontWeight = if (header) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun FieldValue(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun CircleCheck(
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (checked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                )
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (checked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Odabrano",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val issued = status == StatusIssuedLabel
    Surface(
        color = if (issued) StatusIssued else StatusInStock,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}
