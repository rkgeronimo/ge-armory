package hr.gearmory.app.feature.returns

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PersonSearch
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReturnsScreen() {
    var memberQuery by rememberSaveable { mutableStateOf("") }
    var selectedMember by rememberSaveable { mutableStateOf<String?>(null) }
    var memberMenuExpanded by remember { mutableStateOf(false) }
    val returnsViewModel: ReturnsViewModel = viewModel()
    val focusManager = LocalFocusManager.current
    val memberListScrollState = rememberScrollState()
    val holders = returnsViewModel.holdings.filter { it.pieces.isNotEmpty() }
    val filteredMembers = holders.filter {
        it.member.contains(memberQuery, ignoreCase = true)
    }
    val selectedHoldings = holders.firstOrNull { it.member == selectedMember }
    val checkedIds = returnsViewModel.checkedIds
    val hasMarkedPiece = selectedHoldings?.pieces?.any { it.id in checkedIds } == true

    fun selectMember(member: String?) {
        if (member != selectedMember) returnsViewModel.clearChecks()
        selectedMember = member
        if (member != null) memberQuery = member
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
                            onValueChange = { query ->
                                memberQuery = query
                                val match = holders.firstOrNull { holding ->
                                    holding.member.equals(query, ignoreCase = true)
                                }?.member
                                selectMember(match)
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
                            Column(modifier = Modifier.verticalScroll(memberListScrollState)) {
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
                                    filteredMembers.forEachIndexed { index, holding ->
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .clickable {
                                                    selectMember(holding.member)
                                                    memberMenuExpanded = false
                                                    focusManager.clearFocus()
                                                },
                                            color = if (holding.member == selectedMember) {
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
                                                    text = holding.member,
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
                    onClick = {
                        returnsViewModel.clearChecks()
                        memberQuery = ""
                        selectedMember = null
                        memberMenuExpanded = false
                        focusManager.clearFocus()
                    },
                    enabled = selectedMember != null ||
                        memberQuery.isNotBlank() ||
                        checkedIds.isNotEmpty(),
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

        if (selectedHoldings == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
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
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HeldPiecesCard(
                    pieces = selectedHoldings.pieces,
                    checkedIds = checkedIds,
                    onToggle = returnsViewModel::toggle,
                )
                Spacer(Modifier.height(16.dp))
                ReturnButton(
                    enabled = hasMarkedPiece,
                    onReturn = {
                        returnsViewModel.returnMarked(selectedHoldings.member)
                        memberQuery = ""
                        selectedMember = null
                        memberMenuExpanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun HeldPiecesCard(
    pieces: List<HeldPiece>,
    checkedIds: List<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
        pieces.forEach { piece ->
            val checked = piece.id in checkedIds
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .height(56.dp)
                    .clickable { onToggle(piece.id) },
                color = if (checked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                contentColor = if (checked) {
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
                    Text(
                        text = piece.type,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Medium,
                    )
                    Text(
                        text = piece.code,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (checked) {
                                    Color.White.copy(alpha = 0.18f)
                                } else {
                                    Color.Transparent
                                },
                                shape = MaterialTheme.shapes.extraLarge,
                            )
                            .border(
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (checked) {
                                        Color.White.copy(alpha = 0.75f)
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
                                contentDescription = "Vraćeno",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ReturnButton(
    enabled: Boolean,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onReturn,
        enabled = enabled,
        modifier = modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
            text = "Vraćeno",
            fontWeight = FontWeight.SemiBold,
        )
    }
}
