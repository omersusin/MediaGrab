package media.grab.os.ui.paste

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import media.grab.os.R
import media.grab.os.ds.components.MGFilledButton
import media.grab.os.ds.components.MGOutlinedButton

@Composable
fun PasteUrlScreen(navController: NavController, vm: PasteUrlViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            TopAppBar(title = { Text(stringResource(R.string.paste_title)) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Rounded.ArrowBack, contentDescription = null) } })
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(value = state.url, onValueChange = vm::setUrl, modifier = Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.paste_hint)) }, singleLine = false, minLines = 3)
                MGOutlinedButton(text = "Paste from clipboard", onClick = { vm.pasteFromClipboard(clipboard.getText()?.text) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                MGFilledButton(text = stringResource(R.string.paste_download), onClick = vm::download, enabled = !state.loading && state.url.isNotBlank(), modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
            }
        }
    }
}