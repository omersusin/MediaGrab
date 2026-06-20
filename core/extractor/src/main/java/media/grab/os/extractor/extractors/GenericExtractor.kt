package media.grab.os.extractor.extractors

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.network.HttpClient

class GenericExtractor : Extractor {
    override val platform = Platform.OTHER
    override fun canHandle(url: String) = true

    override suspend fun extract(url: String): Result<MediaInfo> = runCatching {
        val response = HttpClient.getAsync(url)
        if (!response.isSuccessful) error("HTTP ${'$'}{response.code}")
        val html = response.body?.string().orEmpty()

        val ogVideo = Regex("""<meta[^>]+property=["']og:video["'][^>]+content=["']([^"']+)["']""")
            .find(html)?.groupValues?.get(1)
        val ogImage = Regex("""<meta[^>]+property=["']og:image["'][^>]+content=["']([^"']+)["']""")
            .find(html)?.groupValues?.get(1)
        val ogTitle = Regex("""<meta[^>]+property=["']og:title["'][^>]+content=["']([^"']+)["']""")
            .find(html)?.groupValues?.get(1)
            ?: Regex("""<title>([^<]+)</title>""").find(html)?.groupValues?.get(1)
            ?: "media"

        val mediaUrl = ogVideo ?: ogImage ?: url
        val type = if (ogVideo != null) MediaType.VIDEO else if (ogImage != null) MediaType.IMAGE else MediaType.UNKNOWN
        val safeTitle = ogTitle?.replace(Regex("[^\\w\\s.-]"), "_")?.take(80) ?: "media"
        val ext = when {
            ogVideo != null -> "mp4"
            ogImage != null -> "jpg"
            else -> "bin"
        }

        MediaInfo(
            url = mediaUrl,
            title = safeTitle,
            fileName = "${'$'}safeTitle.${'$'}ext",
            mediaType = type,
            platform = Platform.fromUrl(url),
            thumbnailUrl = ogImage,
            author = null
        )
    }
}
