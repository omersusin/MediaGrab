package media.grab.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onEnableAccessibility: () -> Unit,
    accessibilityEnabled: Boolean
) {
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("MediaGrab Dashboard", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Accessibility Service")
                    Text(if (accessibilityEnabled) "Active" else "Disabled")
                    if (!accessibilityEnabled) {
                        Button(onClick = onEnableAccessibility) {
                            Text("Open Accessibility Settings")
                        }
                    }
                }
            }
        }
    }
}
