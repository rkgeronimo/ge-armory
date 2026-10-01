package hr.gearmory.app.feature.issuance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun IssuanceScreen() {
    var pickedMember by rememberSaveable { mutableStateOf<String?>(null) }
    var opened by rememberSaveable { mutableStateOf(false) }
    var selectedEquipment by rememberSaveable { mutableStateOf(loanEquipmentTypes.first()) }
    var showConfirmation by rememberSaveable { mutableStateOf(false) }
    val issuanceViewModel: IssuanceViewModel = viewModel()
    val equipmentValues = issuanceViewModel.equipmentValues
    val equipmentSizes = issuanceViewModel.equipmentSizes
    val selectedMember = pickedMember
    val cancelIssuance = {
        issuanceViewModel.clearAll()
        pickedMember = null
        opened = false
        selectedEquipment = loanEquipmentTypes.first()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!opened || selectedMember == null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp)
                        .size(56.dp),
                ) {
                    Icon(
                        Icons.Rounded.Refresh,
                        contentDescription = "Osvježi",
                    )
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        dummyMembers.forEachIndexed { index, member ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 64.dp)
                                    .clickable { pickedMember = member },
                                color = if (member == pickedMember) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = member,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            if (index < dummyMembers.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { opened = true },
                    enabled = selectedMember != null,
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Zaduživanje", fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Button(
                    onClick = cancelIssuance,
                    modifier = Modifier
                        .width(120.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                ) {
                    Text("Odustani")
                }
            }
            IssuanceEditor(
                selectedEquipment = selectedEquipment,
                onEquipmentSelected = { selectedEquipment = it },
                equipmentValues = equipmentValues,
                equipmentSizes = equipmentSizes,
                onKey = { equipment, key ->
                    issuanceViewModel.pressKey(equipment, key)
                },
                onClearEquipment = issuanceViewModel::clearEquipment,
                onIssue = { showConfirmation = true },
            )
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Oprema je izdana") },
            text = {
                Text(
                    "Izdavanje za člana $selectedMember evidentirano je u prototipu.",
                )
            },
            confirmButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("U redu")
                }
            },
        )
    }
}
