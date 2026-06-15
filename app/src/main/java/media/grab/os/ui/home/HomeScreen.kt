package media.grab.os.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import media.grab.os.R
import media.grab.os.ds.components.EmptyState
import media.grab.os.navigation.Destinations
import media.grab.os.ui.home.components.AccessStatusCard
import media.grab.os.ui.home.components.ActiveDownloadCard
import media.grab.os.ui.home.components.PasteUrlCard
import media.grab.os.ui.home.components.RecentDownloadsCard

@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (state.active.isEmpty() && state.recent.isEmpty()) {
            EmptyState(icon = Icons.Rounded.RocketLaunch, title = stringResource(R.string.home_title), message = stringResource(R.string.home_subtitle))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { AccessStatusCard() }
                item { PasteUrlCard(onClick = { navController.navigate(Destinations.Paste.route) }) }
                if (state.active.isNotEmpty()) {
                    item { Text(stringResource(R.string.home_card_active), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(state.active, key = { it.id }) { download -> ActiveDownloadCard(download) }
                }
                if (state.recent.isNotEmpty()) {
                    item { Text(stringResource(R.string.home_card_recent), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(state.recent, key = { it.id }) { download -> RecentDownloadsCard(download) }
                }
            }
        }
    }
}