package hr.gearmory.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import hr.gearmory.app.feature.inventory.InventoryFileStore
import hr.gearmory.app.feature.login.LoginScreen
import hr.gearmory.app.ui.shell.AppShell
import hr.gearmory.app.ui.theme.GeArmoryTheme

class MainActivity : ComponentActivity() {
    private val storagePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) InventoryFileStore(this).ensure()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 29 &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            storagePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            InventoryFileStore(this).ensure()
        }
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
            AppShell(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onLogout = { isLoggedIn = false },
            )
        } else {
            LoginScreen(onLogin = { isLoggedIn = true })
        }
    }
}
