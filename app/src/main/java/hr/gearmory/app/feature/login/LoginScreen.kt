package hr.gearmory.app.feature.login

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.gearmory.app.R

@Composable
internal fun LoginScreen(onLogin: () -> Unit) {
    val portrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        BubbleField(Modifier.fillMaxSize())
        if (portrait) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 28.dp, vertical = 36.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LoginBranding()
                Spacer(Modifier.height(36.dp))
                LoginCard(
                    onLogin = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp),
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LoginBranding(modifier = Modifier.weight(1.15f))
                Spacer(Modifier.width(48.dp))
                LoginCard(
                    onLogin = onLogin,
                    modifier = Modifier
                        .weight(0.85f)
                        .widthIn(max = 420.dp),
                )
            }
        }
    }
}

@Composable
private fun LoginBranding(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.logo_blue),
            contentDescription = "Ronilački klub Geronimo",
            modifier = Modifier.size(120.dp),
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
            modifier = Modifier.widthIn(max = 360.dp),
        )
    }
}

@Composable
private fun LoginCard(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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

private data class Bubble(
    val x: Float,
    val y: Float,
    val radiusDp: Float,
    val alpha: Float,
    val stroked: Boolean,
)

private val bubbles = listOf(
    Bubble(0.06f, 0.08f, 18f, 0.45f, true),
    Bubble(0.18f, 0.22f, 42f, 0.22f, false),
    Bubble(0.08f, 0.72f, 26f, 0.28f, false),
    Bubble(0.22f, 0.88f, 14f, 0.55f, true),
    Bubble(0.34f, 0.12f, 12f, 0.4f, true),
    Bubble(0.42f, 0.78f, 34f, 0.16f, false),
    Bubble(0.58f, 0.08f, 22f, 0.2f, false),
    Bubble(0.72f, 0.18f, 10f, 0.5f, true),
    Bubble(0.86f, 0.1f, 30f, 0.18f, false),
    Bubble(0.92f, 0.42f, 16f, 0.35f, true),
    Bubble(0.8f, 0.9f, 46f, 0.14f, false),
    Bubble(0.94f, 0.78f, 12f, 0.45f, true),
    Bubble(0.5f, 0.42f, 8f, 0.35f, true),
    Bubble(0.14f, 0.48f, 9f, 0.4f, true),
)

@Composable
private fun BubbleField(modifier: Modifier = Modifier) {
    val fill = Color.White
    val line = MaterialTheme.colorScheme.primary
    Canvas(modifier) {
        bubbles.forEach { bubble ->
            val center = Offset(bubble.x * size.width, bubble.y * size.height)
            val radius = bubble.radiusDp.dp.toPx()
            if (bubble.stroked) {
                drawCircle(
                    color = line.copy(alpha = bubble.alpha),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx()),
                )
            } else {
                drawCircle(
                    color = fill.copy(alpha = bubble.alpha),
                    radius = radius,
                    center = center,
                )
            }
        }
    }
}
