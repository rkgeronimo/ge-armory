package hr.gearmory.app.ui.shell

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.gearmory.app.feature.equipment.EquipmentScreen
import hr.gearmory.app.feature.home.HomeScreen
import hr.gearmory.app.feature.inventory.InventoryPopisScreen
import hr.gearmory.app.feature.inventory.InventoryScreen
import hr.gearmory.app.feature.issuance.IssuanceScreen
import hr.gearmory.app.feature.returns.ReturnsScreen
import kotlinx.coroutines.launch

@Composable
internal fun AppShell(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(Destination.Home) }
    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        if (isPortrait) {
            PortraitShell(
                selected = selected,
                darkTheme = darkTheme,
                onSelect = { selected = it },
                onToggleTheme = onToggleTheme,
                onLogout = onLogout,
            )
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                    NavigationPanel(
                        selected = selected,
                        darkTheme = darkTheme,
                        onSelect = { selected = it },
                        onToggleTheme = onToggleTheme,
                        onLogout = onLogout,
                        modifier = Modifier.width(220.dp),
                    )
                }
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                )
                ShellContent(
                    selected = selected,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PortraitShell(
    selected: Destination,
    darkTheme: Boolean,
    onSelect: (Destination) -> Unit,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(240.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                windowInsets = WindowInsets(0, 0, 0, 0),
            ) {
                NavigationPanel(
                    selected = selected,
                    darkTheme = darkTheme,
                    onSelect = { destination ->
                        onSelect(destination)
                        scope.launch { drawerState.close() }
                    },
                    onToggleTheme = onToggleTheme,
                    onLogout = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Menu,
                            contentDescription = "Otvori izbornik",
                        )
                    }
                    Text(
                        text = selected.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            HorizontalDivider()
            ShellContent(
                selected = selected,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ShellContent(
    selected: Destination,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (selected) {
            Destination.Home -> HomeScreen()
            Destination.Issuance -> IssuanceScreen()
            Destination.Returns -> ReturnsScreen()
            Destination.Equipment -> EquipmentScreen()
            Destination.Inventory -> InventoryScreen()
            Destination.InventoryPopis -> InventoryPopisScreen()
        }
    }
}
