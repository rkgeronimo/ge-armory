package hr.gearmory.app.feature.issuance

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun IssuanceEditor(
    selectedEquipment: String,
    onEquipmentSelected: (String) -> Unit,
    equipmentValues: MutableMap<String, String>,
    equipmentSizes: MutableMap<String, String>,
    onKey: (String, String) -> Unit,
    onClearEquipment: (String) -> Unit,
    onIssue: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            CompactIssuanceEditor(
                selectedEquipment = selectedEquipment,
                onEquipmentSelected = onEquipmentSelected,
                equipmentValues = equipmentValues,
                equipmentSizes = equipmentSizes,
                onKey = onKey,
                onClearEquipment = onClearEquipment,
                onIssue = onIssue,
            )
        } else {
            WideIssuanceEditor(
                selectedEquipment = selectedEquipment,
                onEquipmentSelected = onEquipmentSelected,
                equipmentValues = equipmentValues,
                equipmentSizes = equipmentSizes,
                onKey = onKey,
                onClearEquipment = onClearEquipment,
                onIssue = onIssue,
            )
        }
    }
}

@Composable
private fun WideIssuanceEditor(
    selectedEquipment: String,
    onEquipmentSelected: (String) -> Unit,
    equipmentValues: MutableMap<String, String>,
    equipmentSizes: MutableMap<String, String>,
    onKey: (String, String) -> Unit,
    onClearEquipment: (String) -> Unit,
    onIssue: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .width(320.dp)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "OPREMA",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp),
                )
                loanEquipmentTypes.forEach { equipment ->
                    val isSelected = equipment == selectedEquipment
                    val value = equipmentValues[equipment].orEmpty()
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .height(56.dp)
                            .clickable { onEquipmentSelected(equipment) },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        contentColor = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        color = if (value.isNotBlank()) {
                                            if (isSelected) {
                                                Color.White.copy(alpha = 0.18f)
                                            } else {
                                                MaterialTheme.colorScheme.primaryContainer
                                            }
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = MaterialTheme.shapes.extraLarge,
                                    )
                                    .border(
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (value.isBlank()) {
                                                if (isSelected) {
                                                    Color.White.copy(alpha = 0.75f)
                                                } else {
                                                    MaterialTheme.colorScheme.outline
                                                }
                                            } else {
                                                Color.Transparent
                                            },
                                        ),
                                        shape = MaterialTheme.shapes.extraLarge,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (value.isNotBlank()) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Popunjeno",
                                        tint = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = equipment,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Medium
                                },
                            )
                            Text(
                                text = equipmentSizes[equipment].orEmpty() + value,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
            }
        }
        VerticalDivider()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(22.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(modifier = Modifier.weight(0.7f)) {
                    EquipmentCodeInput(
                        value = equipmentSizes[selectedEquipment].orEmpty() +
                            equipmentValues[selectedEquipment].orEmpty(),
                        onClear = { onClearEquipment(selectedEquipment) },
                    )
                }

                Column(
                    modifier = Modifier.weight(1.3f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EquipmentSizeSelector(
                        equipment = selectedEquipment,
                        selectedSize = equipmentSizes[selectedEquipment],
                        onSizeSelected = { equipmentSizes[selectedEquipment] = it },
                    )
                    NumericKeypad(
                        modifier = Modifier.fillMaxWidth(),
                        keyHeight = 56.dp,
                        onKey = { key -> onKey(selectedEquipment, key) },
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            IssuanceSummary(
                equipmentValues = equipmentValues,
                equipmentSizes = equipmentSizes,
            )
            Spacer(Modifier.height(10.dp))
            IssuanceActions(
                enabled = equipmentValues.values.any { it.isNotBlank() },
                onIssue = onIssue,
            )
        }
    }
}

@Composable
private fun CompactIssuanceEditor(
    selectedEquipment: String,
    onEquipmentSelected: (String) -> Unit,
    equipmentValues: MutableMap<String, String>,
    equipmentSizes: MutableMap<String, String>,
    onKey: (String, String) -> Unit,
    onClearEquipment: (String) -> Unit,
    onIssue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = "OPREMA",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        loanEquipmentTypes.chunked(3).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { equipment ->
                    val isSelected = selectedEquipment == equipment
                    val isComplete = equipmentValues[equipment].orEmpty().isNotBlank()
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable { onEquipmentSelected(equipment) },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            if (isComplete) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (isSelected) {
                                                Color.White.copy(alpha = 0.2f)
                                            } else {
                                                MaterialTheme.colorScheme.primaryContainer
                                            },
                                            shape = MaterialTheme.shapes.extraLarge,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = "Popunjeno",
                                        tint = if (isSelected) {
                                            Color.White
                                        } else {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        },
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(
                                text = equipment,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        EquipmentCodeInput(
            value = equipmentSizes[selectedEquipment].orEmpty() +
                equipmentValues[selectedEquipment].orEmpty(),
            onClear = { onClearEquipment(selectedEquipment) },
        )
        Spacer(Modifier.height(32.dp))
        EquipmentSizeSelector(
            equipment = selectedEquipment,
            selectedSize = equipmentSizes[selectedEquipment],
            onSizeSelected = { equipmentSizes[selectedEquipment] = it },
        )
        NumericKeypad(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            onKey = { key -> onKey(selectedEquipment, key) },
        )
        Spacer(Modifier.height(16.dp))
        IssuanceSummary(
            equipmentValues = equipmentValues,
            equipmentSizes = equipmentSizes,
        )
        Spacer(Modifier.height(10.dp))
        IssuanceActions(
            enabled = equipmentValues.values.any { it.isNotBlank() },
            onIssue = onIssue,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun IssuanceActions(
    enabled: Boolean,
    onIssue: () -> Unit,
) {
    Button(
        onClick = onIssue,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.outline,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    ) {
        Text(
            text = "Izdaj opremu",
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EquipmentCodeInput(
    value: String,
    onClear: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            readOnly = true,
            placeholder = { Text("Oznaka opreme") },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp,
            ),
            trailingIcon = if (value.isNotEmpty()) {
                {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(56.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Obriši oznaku",
                        )
                    }
                }
            } else {
                null
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
        )
    }
}

@Composable
private fun EquipmentSizeSelector(
    equipment: String,
    selectedSize: String?,
    onSizeSelected: (String) -> Unit,
) {
    val sizes = when (equipment) {
        "Peraje" -> listOf("S", "R", "XL")
        "Kompenzator" -> listOf("XS", "S", "M", "L", "XL", "XXL")
        else -> emptyList()
    }

    if (sizes.isEmpty()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        sizes.forEach { size ->
            val isSelected = size == selectedSize
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clickable { onSizeSelected(size) },
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = size,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun NumericKeypad(
    modifier: Modifier = Modifier,
    keyHeight: Dp = 56.dp,
    onKey: (String) -> Unit,
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("X", "0", "⌫"),
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { key ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(keyHeight)
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
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                        ),
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

@Composable
private fun IssuanceSummary(
    equipmentValues: Map<String, String>,
    equipmentSizes: Map<String, String>,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .widthIn(max = 720.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "SAŽETAK",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${equipmentValues.values.count { it.isNotBlank() }} / ${loanEquipmentTypes.size} popunjeno",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(6.dp))
                loanEquipmentTypes.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        row.forEach { equipment ->
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                ) {
                                    Text(
                                        text = equipment,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        text = listOfNotNull(
                                            equipmentSizes[equipment]
                                                ?.takeIf { it.isNotBlank() },
                                            equipmentValues[equipment]
                                                ?.takeIf { it.isNotBlank() },
                                        ).joinToString("").ifBlank { "—" },
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(
                                        alpha = 0.18f,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
