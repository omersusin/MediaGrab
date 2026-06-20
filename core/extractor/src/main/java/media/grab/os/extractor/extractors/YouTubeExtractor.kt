package media.grab.os.extractor.extractors

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo

class YouTubeExtractor : Extractor {
    override val platform = Platform.YOUTUBE
    override fun canHandle(url: String) =
        "youtube.com" in url.lowercase() || "youtu.be" in url.lowercase()

    override suspend fun extract(url: String): Result<MediaInfo> = runCatching {
        // YT requires yt-dlp or special handling; for now, return metadata only
        val videoId = Regex("(?:v=|youtu\\.be/)([\\w-]{11})").find(url)?.groupValues?.get(1)
            ?: error("Invalid YouTube URL")
        MediaInfo(
            url = "https://www.youtube.com/watch?v=${'$'}videoId",
            title = "YouTube ${'$'}videoId",
            fileName = "yt_${'$'}videoId.mp4",
            mediaType = MediaType.VIDEO,
            platform = Platform.YOUTUBE,
            thumbnailUrl = "https://i.ytimg.com/vi/${'$'}videoId/hqdefault.jpg"
        )
    }
}
