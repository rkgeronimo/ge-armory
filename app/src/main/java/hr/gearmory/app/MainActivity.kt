package hr.gearmory.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import hr.gearmory.app.feature.login.LoginScreen
import hr.gearmory.app.ui.shell.AppShell
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
