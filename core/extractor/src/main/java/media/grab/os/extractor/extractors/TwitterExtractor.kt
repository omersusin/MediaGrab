package media.grab.os.extractor.extractors

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.network.HttpClient

class TwitterExtractor : Extractor {
    override val platform = Platform.TWITTER
    override fun canHandle(url: String) =
        "twitter.com" in url.lowercase() || "x.com" in url.lowercase()

    override suspend fun extract(url: String): Result<MediaInfo> = runCatching {
        val response = HttpClient.getAsync(url)
        val html = response.body?.string().orEmpty()

        val ogVideo = Regex("""<meta[^>]+property=["']og:video["'][^>]+content=["']([^"']+)["']""")
            .find(html)?.groupValues?.get(1)
        val ogImage = Regex("""<meta[^>]+property=["']og:image["'][^>]+content=["']([^"']+)["']""")
            .find(html)?.groupValues?.get(1)
        val ogTitle = Regex("""<meta[^>]+property=["']og:title["'][^>]+content=["']([^"']+)["']""")
            .find(html)?.groupValues?.get(1) ?: "Tweet"

        val mediaUrl = ogVideo ?: ogImage ?: error("No media")
        val type = if (ogVideo != null) MediaType.VIDEO else MediaType.IMAGE
        val safeTitle = ogTitle.replace(Regex("[^\\w\\s.-]"), "_").take(60)
        val ext = if (ogVideo != null) "mp4" else "jpg"
        MediaInfo(
            url = mediaUrl,
            title = safeTitle,
            fileName = "x_${'$'}safeTitle.${'$'}ext",
            mediaType = type,
            platform = Platform.TWITTER
        )
    }
}
