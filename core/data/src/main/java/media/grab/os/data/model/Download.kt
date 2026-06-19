package media.grab.os.data.model

import java.util.UUID

data class Download(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val fileName: String,
    val platform: Platform,
    val mediaType: MediaType,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val progress: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)
