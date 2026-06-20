package media.grab.os.extractor.extractors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.network.HttpClient

class GenericExtractor : Extractor {
    override val platform = Platform.OTHER
    override fun canHandle(url: String) = true

    override suspend fun extract(url: String): Result<MediaInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val response = HttpClient.getAsync(url)
            if (!response.isSuccessful) error("HTTP ${'$'}{response.code}: Sayfaya erişilemedi")
            val html = response.body?.string().orEmpty()
            if (html.isBlank()) error("Boş yanıt")

            // Try og:video, og:image, twitter:image, in that order
            val ogVideo = findMeta(html, listOf("og:video", "og:video:url", "twitter:player:stream"))
            val ogImage = findMeta(html, listOf("og:image", "twitter:image"))
            val ogTitle = findMeta(html, listOf("og:title", "twitter:title"))
                ?: Regex("""<title>([^<]+)</title>""").find(html)?.groupValues?.get(1)
                ?: "media"

            val mediaUrl = ogVideo ?: ogImage ?: error("Sayfada video/resim bulunamadı. Site giriş gerektirebilir.")

            val type = if (ogVideo != null) MediaType.VIDEO
                else if (ogImage != null) MediaType.IMAGE
                else MediaType.UNKNOWN

            val ext = when {
                mediaUrl.contains(".mp4") -> "mp4"
                mediaUrl.contains(".webm") -> "webm"
                mediaUrl.contains(".png") -> "png"
                mediaUrl.contains(".gif") -> "gif"
                mediaUrl.contains(".webp") -> "webp"
                ogVideo != null -> "mp4"
                ogImage != null -> "jpg"
                else -> "bin"
            }

            val safeTitle = ogTitle.replace(Regex("[^\\w\\s.-]"), "_").take(80)

            MediaInfo(
                url = mediaUrl,
                title = safeTitle,
                fileName = "${'$'}safeTitle.${'$'}ext",
                mediaType = type,
                platform = Platform.fromUrl(url),
                thumbnailUrl = ogImage
            )
        }
    }

    private fun findMeta(html: String, keys: List<String>): String? {
        for (key in keys) {
            val pattern = Regex(
                """<meta[^>]+(?:property|name)=["']${'$'}key["'][^>]+content=["']([^"']+)["']"""
            )
            val match = pattern.find(html)?.groupValues?.get(1)
            if (!match.isNullOrBlank()) return match

            // Try reversed attribute order
            val pattern2 = Regex(
                """<meta[^>]+content=["']([^"']+)["'][^>]+(?:property|name)=["']${'$'}key["']"""
            )
            val match2 = pattern2.find(html)?.groupValues?.get(1)
            if (!match2.isNullOrBlank()) return match2
        }
        return null
    }
}
