package media.grab.os.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DownloadItem(
    val id: String,
    val url: String,
    val title: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

class DownloadRepository {
    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    fun addDownload(item: DownloadItem) {
        _downloads.value = _downloads.value + item
    }

    fun removeDownload(id: String) {
        _downloads.value = _downloads.value.filterNot { it.id == id }
    }

    fun clearAll() {
        _downloads.value = emptyList()
    }
}
