package media.grab.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

object LogStore {
    val logs = MutableStateFlow<List<String>>(emptyList())
    fun add(message: String) {
        logs.value = logs.value + message
    }
}

@Composable
fun DashboardScreen(
    onEnableAccessibility: () -> Unit,
    accessibilityEnabled: Boolean
) {
    val logEntries by LogStore.logs.collectAsState()
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
            Spacer(modifier = Modifier.height(12.dp))
            Text("Download Log", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(logEntries.reversed()) { entry ->
                    Text(entry, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
