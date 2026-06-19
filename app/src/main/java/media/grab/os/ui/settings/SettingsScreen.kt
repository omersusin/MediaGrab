package media.grab.os.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.grab.os.ds.components.AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController? = null) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                Text("Erişim", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
            }
            item {
                AppCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Erişim Yöntemi", style = MaterialTheme.typography.bodyLarge)
                        Text("Accessibility Service / Shizuku / Root", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { }) { Text("Erişim Ver") }
                    }
                }
            }
            item {
                Text("Tema", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
            }
            item {
                AppCard {
                    ListItem(
                        headlineContent = { Text("Karanlık Tema") },
                        trailingContent = { Switch(checked = false, onCheckedChange = { }) }
                    )
                }
            }
            item {
                Text("İndirme", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
            }
            item {
                AppCard {
                    ListItem(
                        headlineContent = { Text("Konum") },
                        supportingContent = { Text("/Download/MediaGrab/") }
                    )
                }
            }
            item {
                Text("Hakkında", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
            }
            item {
                AppCard {
                    ListItem(
                        headlineContent = { Text("Sürüm") },
                        supportingContent = { Text("1.0.0") }
                    )
                }
            }
        }
    }
}
