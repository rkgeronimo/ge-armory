package hr.gearmory.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.gearmory.app.ui.theme.GeArmoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            GeArmoryTheme(darkTheme = darkTheme) {
                ArmoryApp(
                    darkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme },
                )
            }
        }
    }
}

@Composable
private fun ArmoryApp(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isLoggedIn) {
            HomeScreen(
                onLogout = { isLoggedIn = false },
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
            )
        } else {
            LoginScreen(onLogin = { isLoggedIn = true })
        }
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Column(modifier = Modifier.fillMaxSize()) {
                LoginBranding(modifier = Modifier.weight(0.42f))
                LoginAction(
                    onLogin = onLogin,
                    modifier = Modifier.weight(0.58f),
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                LoginBranding(modifier = Modifier.weight(0.43f))
                LoginAction(
                    onLogin = onLogin,
                    modifier = Modifier.weight(0.57f),
                )
            }
        }
    }
}

@Composable
private fun LoginBranding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(40.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_blue),
            contentDescription = "Ronilački klub Geronimo",
            modifier = Modifier.size(132.dp),
        )
        Spacer(Modifier.height(22.dp))
        Text(
            text = "GE Armory",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Jednostavno upravljanje opremom ronilačkog kluba.",
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
            fontSize = 17.sp,
            lineHeight = 25.sp,
            modifier = Modifier.widthIn(max = 420.dp),
        )
    }
}

@Composable
private fun LoginAction(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.widthIn(max = 440.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(modifier = Modifier.padding(36.dp)) {
                Text(
                    text = "Prijava",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Nastavite u sustav za upravljanje opremom.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(28.dp))
                Button(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text("Prijavi se")
                }
            }
        }
    }
}

private enum class Destination(
    val title: String,
    val description: String,
    val icon: ImageVector,
) {
    Dashboard(
        title = "Početna",
        description = "Pregled stanja opreme",
        icon = Icons.Rounded.Dashboard,
    ),
    Loans(
        title = "Izdavanje",
        description = "Izdavanje i povrat opreme",
        icon = Icons.Rounded.SwapHoriz,
    ),
    Equipment(
        title = "Oprema",
        description = "Popis sve opreme",
        icon = Icons.Rounded.Inventory2,
    ),
    Inventory(
        title = "Inventura",
        description = "Unos i pregled inventure",
        icon = Icons.Rounded.FactCheck,
    ),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    onLogout: () -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(Destination.Dashboard) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        val isPortrait =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

        if (isPortrait) {
            Column(modifier = Modifier.fillMaxSize()) {
                DestinationContent(
                    selected = selected,
                    modifier = Modifier.weight(1f),
                )
                HorizontalDivider()
                NavigationBar {
                    Destination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = destination == selected,
                            onClick = { selected = destination },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.title,
                                )
                            },
                            label = { Text(destination.title) },
                        )
                    }
                    NavigationBarItem(
                        selected = false,
                        onClick = onToggleTheme,
                        icon = {
                            Icon(
                                imageVector = if (darkTheme) {
                                    Icons.Rounded.LightMode
                                } else {
                                    Icons.Rounded.DarkMode
                                },
                                contentDescription = "Promijeni temu",
                            )
                        },
                        label = { Text("Tema") },
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onLogout,
                        icon = {
                            Icon(Icons.Rounded.Logout, contentDescription = "Odjava")
                        },
                        label = { Text("Odjava") },
                    )
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    header = {
                        Image(
                            painter = painterResource(R.drawable.logo_blue),
                            contentDescription = "Ronilački klub Geronimo",
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .size(52.dp),
                        )
                    },
                ) {
                    Destination.entries.forEach { destination ->
                        NavigationRailItem(
                            selected = destination == selected,
                            onClick = { selected = destination },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.title,
                                )
                            },
                            label = { Text(destination.title) },
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    NavigationRailItem(
                        selected = false,
                        onClick = onToggleTheme,
                        icon = {
                            Icon(
                                imageVector = if (darkTheme) {
                                    Icons.Rounded.LightMode
                                } else {
                                    Icons.Rounded.DarkMode
                                },
                                contentDescription = "Promijeni temu",
                            )
                        },
                        label = { Text("Tema") },
                    )
                    NavigationRailItem(
                        selected = false,
                        onClick = onLogout,
                        icon = {
                            Icon(Icons.Rounded.Logout, contentDescription = "Odjava")
                        },
                        label = { Text("Odjava") },
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                )
                DestinationContent(
                    selected = selected,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DestinationContent(
    selected: Destination,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (selected) {
            Destination.Dashboard -> DashboardContent()
            Destination.Loans -> EquipmentIssuanceContent()
            else -> ModulePlaceholder(selected)
        }
    }
}

@Composable
private fun DashboardContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Text(
            text = "Dobro došli",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Brzi pregled današnjeg stanja.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(28.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SummaryCard(
                value = "0",
                label = "Izdano danas",
                icon = Icons.Rounded.SwapHoriz,
                modifier = Modifier.weight(1f),
            )
            SummaryCard(
                value = "0",
                label = "Ukupno opreme",
                icon = Icons.Rounded.Inventory2,
                modifier = Modifier.weight(1f),
            )
            SummaryCard(
                value = "0",
                label = "Otvorene inventure",
                icon = Icons.Rounded.FactCheck,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.medium,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(18.dp))
            Column {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private val dummyMembers = listOf(
    "Ana Kovač",
    "Marko Marić",
    "Ivan Horvat",
    "Petra Babić",
    "Luka Jurić",
    "Ema Novak",
    "Toni Radić",
    "Mia Perić",
)

private val loanEquipmentTypes = listOf(
    "Odijelo",
    "Maska",
    "Čizmice",
    "Peraje",
    "Kompenzator",
    "Regulator",
)

class EquipmentIssuanceViewModel : ViewModel() {
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

@Composable
private fun EquipmentIssuanceContent() {
    var memberQuery by rememberSaveable { mutableStateOf("") }
    var selectedMember by rememberSaveable { mutableStateOf<String?>(null) }
    var memberMenuExpanded by remember { mutableStateOf(false) }
    var selectedEquipment by rememberSaveable { mutableStateOf(loanEquipmentTypes.first()) }
    var showConfirmation by rememberSaveable { mutableStateOf(false) }
    val issuanceViewModel: EquipmentIssuanceViewModel = viewModel()
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(modifier = Modifier.width(480.dp)) {
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
                            modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = "Pretražite članove prema imenu ili prezimenu.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable
private fun IssuanceEditor(
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
                    Text(
                        text = selectedEquipment,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))
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
        Text(
            text = selectedEquipment,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        EquipmentCodeInput(
            value = equipmentSizes[selectedEquipment].orEmpty() +
                equipmentValues[selectedEquipment].orEmpty(),
            onClear = { onClearEquipment(selectedEquipment) },
        )
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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

@Composable
private fun ModulePlaceholder(destination: Destination) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(32.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                destination.icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                destination.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Modul je spreman za sljedeću fazu.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
