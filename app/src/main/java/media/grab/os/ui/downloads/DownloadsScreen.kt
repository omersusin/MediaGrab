package media.grab.os.ui.downloads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import media.grab.os.R
import media.grab.os.ds.components.EmptyState
import media.grab.os.ui.downloads.components.DownloadListItem
import media.grab.os.ui.downloads.components.SearchBar

@Composable
fun DownloadsScreen(navController: androidx.navigation.NavController, vm: DownloadsViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            SearchBar(query = state.filter.query, onQueryChange = vm::setQuery)
            if (state.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { EmptyState(icon = Icons.Rounded.DownloadDone, title = stringResource(R.string.downloads_empty), message = "") }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.items, key = { it.id }) { download -> DownloadListItem(download, onDelete = { vm.delete(download.id) }) }
                }
            }
        }
    }
}