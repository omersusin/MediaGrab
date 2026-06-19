package media.grab.os.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform

class DownloadRepository private constructor() {

    private val _downloads = MutableStateFlow<List<Download>>(emptyList())
    val downloads: StateFlow<List<Download>> = _downloads.asStateFlow()

    private val _activeCount = MutableStateFlow(0)
    val activeCount: StateFlow<Int> = _activeCount.asStateFlow()

    fun addDownload(download: Download) {
        _downloads.value = listOf(download) + _downloads.value
        updateActiveCount()
    }

    fun removeDownload(id: String) {
        _downloads.value = _downloads.value.filterNot { it.id == id }
        updateActiveCount()
    }

    fun updateStatus(id: String, status: DownloadStatus, error: String? = null) {
        _downloads.value = _downloads.value.map {
            if (it.id == id) it.copy(status = status, errorMessage = error) else it
        }
        updateActiveCount()
    }

    fun updateProgress(id: String, progress: Float) {
        _downloads.value = _downloads.value.map {
            if (it.id == id) it.copy(progress = progress) else it
        }
    }

    fun search(query: String, platform: Platform?, type: MediaType?, status: DownloadStatus?): List<Download> {
        return _downloads.value.filter { d ->
            (query.isBlank() || d.title.contains(query, ignoreCase = true) || d.url.contains(query, ignoreCase = true)) &&
            (platform == null || d.platform == platform) &&
            (type == null || d.mediaType == type) &&
            (status == null || d.status == status)
        }
    }

    fun observeActive(): List<Download> = _downloads.value.filter {
        it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED
    }

    fun observeRecent(limit: Int = 5): List<Download> =
        _downloads.value.filter { it.status == DownloadStatus.COMPLETED }.take(limit)

    fun clearAll() {
        _downloads.value = emptyList()
        updateActiveCount()
    }

    private fun updateActiveCount() {
        _activeCount.value = _downloads.value.count {
            it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED
        }
    }

    companion object {
        @Volatile private var instance: DownloadRepository? = null
        fun getInstance(): DownloadRepository {
            return instance ?: synchronized(this) {
                instance ?: DownloadRepository().also { instance = it }
            }
        }
    }
}
