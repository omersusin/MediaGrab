package media.grab.os.extractor.extractors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.network.HttpClient

class InstagramExtractor : Extractor {
    override val platform = Platform.INSTAGRAM
    override fun canHandle(url: String) = "instagram.com" in url.lowercase()

    override suspend fun extract(url: String): Result<MediaInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val response = HttpClient.getAsync(url)
            if (!response.isSuccessful) error("HTTP ${response.code}")
            val html = response.body?.string().orEmpty()
            if (html.isBlank()) error("Bos yanit")

            // og:video / og:image
            val ogVideo = Regex("""<meta[^>]+property=["']og:video["'][^>]+content=["']([^"']+)["']""")
                .find(html)?.groupValues?.get(1)?.replace("&amp;", "&")
            val ogImage = Regex("""<meta[^>]+property=["']og:image["'][^>]+content=["']([^"']+)["']""")
                .find(html)?.groupValues?.get(1)?.replace("&amp;", "&")

            // JSON pattern (display_url, video_url) - escaped JSON icinde
            val jsonVideo = Regex(""""video_url":\s*"([^"\\]+(\\.[^"\\]*)*)"""")
                .find(html)?.groupValues?.get(1)
                ?.replace("\\/", "/")
                ?.replace("&amp;", "&")
            val jsonImage = Regex(""""display_url":\s*"([^"\\]+(\\.[^"\\]*)*)"""")
                .find(html)?.groupValues?.get(1)
                ?.replace("\\/", "/")
                ?.replace("&amp;", "&")

            val mediaUrl = ogVideo ?: jsonVideo ?: ogImage ?: jsonImage
                ?: error("Instagram medyasi bulunamadi. Giris gerektiren icerik olabilir.")

            val type = if (ogVideo != null || jsonVideo != null) MediaType.VIDEO else MediaType.IMAGE
            val title = Regex("""<meta[^>]+property=["']og:title["'][^>]+content=["']([^"']+)["']""")
                .find(html)?.groupValues?.get(1) ?: "Instagram"
            val safeTitle = title.replace(Regex("[^\\w\\s.-]"), "_").take(60)
            val ext = if (type == MediaType.VIDEO) "mp4" else "jpg"

            MediaInfo(
                url = mediaUrl,
                title = safeTitle,
                fileName = "ig_${safeTitle}.${ext}",
                mediaType = type,
                platform = Platform.INSTAGRAM
            )
        }
    }
}
