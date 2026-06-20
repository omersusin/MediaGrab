package media.grab.os.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import media.grab.os.data.preferences.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController? = null) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences.getInstance(context) }
    val scope = rememberCoroutineScope()
    val theme by prefs.theme.collectAsState(initial = "system")
    val accessMode by prefs.accessMode.collectAsState(initial = "accessibility")
    val overlayEnabled by prefs.overlayEnabled.collectAsState(initial = false)

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
                Text("Tema", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
            }
            item {
                AppCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        listOf("system" to "Sistem", "light" to "Açık", "dark" to "Koyu").forEach { (value, label) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = theme == value,
                                    onClick = { scope.launch { prefs.setTheme(value) } }
                                )
                                Text(label)
                            }
                        }
                    }
                }
            }

            item {
                Text("Erişim Yöntemi", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
            }
            item {
                AppCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        listOf(
                            "accessibility" to "Accessibility Service",
                            "shizuku" to "Shizuku",
                            "root" to "Root"
                        ).forEach { (value, label) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = accessMode == value,
                                    onClick = { scope.launch { prefs.setAccessMode(value) } }
                                )
                                Text(label)
                            }
                        }
                    }
                }
            }

            item {
                Text("Bildirim", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
            }
            item {
                AppCard {
                    ListItem(
                        headlineContent = { Text("Bildirim İzni") },
                        trailingContent = {
                            TextButton(onClick = {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                context.startActivity(intent)
                            }) { Text("Ayarla") }
                        }
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

@Composable
private fun AppCard(content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        content()
    }
}
