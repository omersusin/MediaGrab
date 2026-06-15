package media.grab.os.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.data.repository.DownloadRepository
import javax.inject.Inject

data class DownloadsFilter(val query: String = "", val platform: Platform? = null, val type: MediaType? = null, val status: DownloadStatus? = null)

data class DownloadsUiState(val items: List<Download> = emptyList(), val filter: DownloadsFilter = DownloadsFilter())

@HiltViewModel
class DownloadsViewModel @Inject constructor(private val repo: DownloadRepository) : ViewModel() {
    private val filter = MutableStateFlow(DownloadsFilter())
    val filterState: StateFlow<DownloadsFilter> = filter.asStateFlow()
    val state: StateFlow<DownloadsUiState> = combine(filter.flatMapLatestF(), filter) { items, f -> DownloadsUiState(items, f) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DownloadsUiState())

    private fun MutableStateFlow<DownloadsFilter>.flatMapLatestF() = kotlinx.coroutines.flow.flatMapLatest(this) { f -> repo.search(f.query, f.platform, f.type, f.status) }

    fun setQuery(q: String) { filter.value = filter.value.copy(query = q) }
    fun setPlatform(p: Platform?) { filter.value = filter.value.copy(platform = p) }
    fun setType(t: MediaType?) { filter.value = filter.value.copy(type = t) }
    fun delete(id: Long) { viewModelScope.launch { repo.delete(id) } }
    fun clearAll() { viewModelScope.launch { repo.deleteAll() } }
}