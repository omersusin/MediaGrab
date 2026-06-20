package media.grab.os.extractor

import android.content.Context
import kotlinx.coroutines.Dispatchers
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
        val notifId = download.id.hashCode()

        try {
            NotificationHelper.showDownloading(context, notifId, mediaInfo.fileName, 0)
            val response = HttpClient.getAsync(mediaInfo.url)
            if (!response.isSuccessful) error("HTTP ${response.code}")

            val bytes = response.body?.bytes() ?: error("Empty body")
            val mime = response.body?.contentType()?.toString()?.split(";")?.firstOrNull()?.trim()
                ?: "image/jpeg"

            val saved = FileSaver.save(context, bytes, mediaInfo.fileName, mime)
                ?: error("Save failed")

            repository.updateStatus(download.id, DownloadStatus.COMPLETED)
            NotificationHelper.showCompleted(context, notifId, mediaInfo.fileName)
            saved
        } catch (e: Exception) {
            repository.updateStatus(download.id, DownloadStatus.FAILED, e.message ?: "Unknown error")
            NotificationHelper.showFailed(context, notifId, mediaInfo.fileName, e.message ?: "Unknown")
            throw e
        }
    }
}
