package media.grab.os.extractor

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform

data class MediaInfo(
    val url: String,
    val title: String,
    val fileName: String,
    val mediaType: MediaType,
    val platform: Platform,
    val thumbnailUrl: String? = null,
    val author: String? = null
)
