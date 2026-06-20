package media.grab.os.ui.paste

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import media.grab.os.data.model.Platform

@Composable
fun PasteUrlScreen(vm: media.grab.os.ui.AppViewModel) {
    val context = LocalContext.current
    var url by remember { mutableStateOf("") }
    var lastAction by remember { mutableStateOf("") }
    val platform = remember(url) { if (url.isBlank()) null else Platform.fromUrl(url) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Paste a link", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Works with 1000+ sites via the built-in yt-dlp engine. Pure image posts fall back to a metadata scraper.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Media URL") },
            singleLine = false
        )
        if (platform != null) {
            Text(
                "Detected: ${platform.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        OutlinedButton(onClick = { url = readClipboard(context) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.ContentPaste, contentDescription = null)
            Text("  Paste from clipboard")
        }
        Button(
            onClick = { vm.download(url); lastAction = "Queued: $url"; url = "" },
            enabled = url.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Download, contentDescription = null)
            Text("  Download")
        }
        OutlinedButton(
            onClick = { vm.download(url, audioOnly = true); lastAction = "Queued audio: $url"; url = "" },
            enabled = url.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.MusicNote, contentDescription = null)
            Text("  Download audio only")
        }
        if (lastAction.isNotBlank()) {
            Text(lastAction, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

private fun readClipboard(context: Context): String {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return cm.primaryClip?.getItemAt(0)?.text?.toString()?.trim().orEmpty()
}
