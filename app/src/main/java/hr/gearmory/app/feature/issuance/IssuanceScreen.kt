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
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IssuanceScreen() {
    var memberQuery by rememberSaveable { mutableStateOf("") }
    var selectedMember by rememberSaveable { mutableStateOf<String?>(null) }
    var memberMenuExpanded by remember { mutableStateOf(false) }
    var selectedEquipment by rememberSaveable { mutableStateOf(loanEquipmentTypes.first()) }
    var showConfirmation by rememberSaveable { mutableStateOf(false) }
    val issuanceViewModel: IssuanceViewModel = viewModel()
    val focusManager = LocalFocusManager.current
    val memberListScrollState = rememberScrollState()
    val equipmentValues = issuanceViewModel.equipmentValues
    val equipmentSizes = issuanceViewModel.equipmentSizes
    val filteredMembers = dummyMembers.filter {
        it.contains(memberQuery, ignoreCase = true)
    }
    val cancelIssuance = {
        issuanceViewModel.clearAll()
        memberQuery = ""
        selectedMember = null
        selectedEquipment = loanEquipmentTypes.first()
        memberMenuExpanded = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 612.dp)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp,
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        OutlinedTextField(
                            value = memberQuery,
                            onValueChange = {
                                memberQuery = it
                                selectedMember = dummyMembers.firstOrNull { member ->
                                    member.equals(it, ignoreCase = true)
                                }
                                memberMenuExpanded = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("Odaberi ili pretraži člana") },
                            leadingIcon = {
                                Icon(Icons.Rounded.PersonSearch, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { memberMenuExpanded = !memberMenuExpanded },
                                ) {
                                    Icon(
                                        Icons.Rounded.ArrowDropDown,
                                        contentDescription = "Otvori popis članova",
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                        )
                    }
                    if (memberMenuExpanded) {
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 224.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                            ),
                        ) {
                            Column(
                                modifier = Modifier.verticalScroll(memberListScrollState),
                            ) {
                                if (filteredMembers.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Nema pronađenih članova",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                } else {
                                    filteredMembers.forEachIndexed { index, member ->
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .clickable {
                                                    memberQuery = member
                                                    selectedMember = member
                                                    memberMenuExpanded = false
                                                    focusManager.clearFocus()
                                                },
                                            color = if (member == selectedMember) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surface
                                            },
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text = member,
                                                    fontWeight = FontWeight.Medium,
                                                )
                                            }
                                        }
                                        if (index < filteredMembers.lastIndex) {
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outlineVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = cancelIssuance,
                    enabled = selectedMember != null ||
                        equipmentValues.values.any { it.isNotBlank() },
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
            }
        }
        HorizontalDivider()

        if (selectedMember == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.PersonSearch,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Prvo odaberite člana",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        } else {
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
