package media.grab.os.ui.paste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.model.MediaType
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.extractor.ExtractionRequest
import media.grab.os.extractor.ExtractorRegistry
import javax.inject.Inject

data class PasteUiState(val url: String = "", val loading: Boolean = false, val error: String? = null)

@HiltViewModel
class PasteUrlViewModel @Inject constructor(private val extractor: ExtractorRegistry, private val repo: DownloadRepository) : ViewModel() {
    private val _state = MutableStateFlow(PasteUiState())
    val state: StateFlow<PasteUiState> = _state.asStateFlow()
    fun setUrl(url: String) { _state.value = _state.value.copy(url = url) }
    fun pasteFromClipboard(clip: String?) { _state.value = _state.value.copy(url = clip.orEmpty()) }
    fun download() {
        val url = _state.value.url.trim()
        if (url.isEmpty()) return
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { extractor.extract(ExtractionRequest(url)) }.onSuccess { result ->
                val download = Download(platform = result.platform, mediaType = result.mediaType, sourceUrl = result.sourceUrl, mediaUrl = result.mediaUrl, fileName = result.fileName, mimeType = result.mimeType, status = DownloadStatus.PENDING)
                repo.add(download)
                _state.value = _state.value.copy(loading = false, url = "")
            }.onFailure { t -> _state.value = _state.value.copy(loading = false, error = t.message ?: "Unknown error") }
        }
    }
}