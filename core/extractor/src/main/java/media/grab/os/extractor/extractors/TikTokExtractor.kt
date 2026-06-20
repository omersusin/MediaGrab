package media.grab.os.extractor.extractors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.network.HttpClient

class TikTokExtractor : Extractor {
    override val platform = Platform.TIKTOK
    override fun canHandle(url: String) = "tiktok.com" in url.lowercase()

    override suspend fun extract(url: String): Result<MediaInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val response = HttpClient.getAsync(url)
            if (!response.isSuccessful) error("HTTP ${response.code}")
            val html = response.body?.string().orEmpty()

            val patterns = listOf(
                Regex(""""playAddr":\s*"([^"\\]+(\\.[^"\\]*)*)""""),
                Regex(""""downloadAddr":\s*"([^"\\]+(\\.[^"\\]*)*)""""),
                Regex("""<meta[^>]+property=["']og:video["'][^>]+content=["']([^"']+)["']"""),
                Regex("""<video[^>]+src=["']([^"']+\.mp4[^"']*)["']""")
            )

            var mediaUrl: String? = null
            for (p in patterns) {
                mediaUrl = p.find(html)?.groupValues?.get(1)
                    ?.replace("\\u002F", "/")
                    ?.replace("\\/", "/")
                if (!mediaUrl.isNullOrBlank()) break
            }
            mediaUrl = mediaUrl ?: error("TikTok video URL bulunamadi. Sayfa JS ile yukleniyor olabilir.")

            val title = Regex("""<meta[^>]+property=["']og:title["'][^>]+content=["']([^"']+)["']""")
                .find(html)?.groupValues?.get(1) ?: "TikTok"
            val safeTitle = title.replace(Regex("[^\\w\\s.-]"), "_").take(60)

            MediaInfo(
                url = mediaUrl,
                title = safeTitle,
                fileName = "tt_${safeTitle}.mp4",
                mediaType = MediaType.VIDEO,
                platform = Platform.TIKTOK
            )
        }
    }
}
