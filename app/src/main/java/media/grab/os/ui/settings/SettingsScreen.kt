package media.grab.os.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import media.grab.os.data.preferences.AccessMode
import media.grab.os.data.preferences.ThemeMode
import media.grab.os.privileged.RootHelper
import media.grab.os.privileged.ShizukuHelper
import media.grab.os.ui.AppViewModel

@Composable
fun SettingsScreen(vm: AppViewModel) {
    val context = LocalContext.current
    val settings by vm.settings.collectAsState()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        SettingsCard("Appearance") {
            Text("Theme", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = settings.themeMode == mode,
                        onClick = { vm.setTheme(mode) },
                        label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }

        SettingsCard("Access mode") {
            Text(
                "Download/ writes need no root. These modes enable grabbing from protected locations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccessMode.entries.forEach { mode ->
                    FilterChip(
                        selected = settings.accessMode == mode,
                        onClick = { vm.setAccessMode(mode) },
                        label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            HorizontalDivider()
            Text("Shizuku: ${ShizukuHelper.statusText()}", style = MaterialTheme.typography.bodySmall)
            Text("Root: ${RootHelper.statusText()}", style = MaterialTheme.typography.bodySmall)
        }

        SettingsCard("Permissions") {
            OutlinedButton(onClick = { openAppNotificationSettings(context) }, modifier = Modifier.fillMaxWidth()) {
                Text("Notification settings")
            }
            OutlinedButton(onClick = { openAccessibilitySettings(context) }, modifier = Modifier.fillMaxWidth()) {
                Text("Accessibility (floating button)")
            }
            OutlinedButton(onClick = { openOverlaySettings(context) }, modifier = Modifier.fillMaxWidth()) {
                Text("Display over other apps")
            }
        }

        SettingsCard("About") {
            Text("MediaGrab v1.0.0", style = MaterialTheme.typography.bodyMedium)
            Text("Free & open-source · GPL-3.0 · No ads, no telemetry.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = { openUrl(context, "https://github.com/omersusin/MediaGrab") },
                modifier = Modifier.fillMaxWidth()) { Text("Source code") }
            OutlinedButton(onClick = { openUrl(context, "https://www.gnu.org/licenses/gpl-3.0.html") },
                modifier = Modifier.fillMaxWidth()) { Text("License") }
        }
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

private fun openUrl(context: Context, url: String) {
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}

private fun openAppNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    runCatching { context.startActivity(intent) }
}

private fun openAccessibilitySettings(context: Context) {
    runCatching { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
}

private fun openOverlaySettings(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
    runCatching { context.startActivity(intent) }
}
