package media.grab.os.extractor

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.network.HttpClient

object DownloadEngine {

    suspend fun downloadAndSave(
        context: Context,
        mediaInfo: MediaInfo,
        repository: DownloadRepository = DownloadRepository.getInstance()
    ): Result<String> = withContext(Dispatchers.IO) {
        val download = Download(
            url = mediaInfo.url,
            title = mediaInfo.title,
            fileName = mediaInfo.fileName,
            platform = mediaInfo.platform,
            mediaType = mediaInfo.mediaType,
            status = DownloadStatus.DOWNLOADING
        )
        repository.addDownload(download)

        runCatching {
            val response = HttpClient.getAsync(mediaInfo.url)
            if (!response.isSuccessful) error("HTTP ${'$'}{response.code}")
            val bytes = response.body?.bytes() ?: error("Empty body")
            val mime = response.body?.contentType()?.toString()?.split(";")?.firstOrNull() ?: "image/jpeg"

            val saved = FileSaver.save(context, bytes, mediaInfo.fileName, mime)
                ?: error("Save failed")

            repository.updateStatus(download.id, DownloadStatus.COMPLETED)
            saved
        }.onFailure { e ->
            repository.updateStatus(download.id, DownloadStatus.FAILED, e.message)
        }
    }
}
