package media.grab.os.extractor.extractors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.network.HttpClient

class TwitterExtractor : Extractor {
    override val platform = Platform.TWITTER
    override fun canHandle(url: String) =
        "twitter.com" in url.lowercase() || "x.com" in url.lowercase()

    override suspend fun extract(url: String): Result<MediaInfo> = withContext(Dispatchers.IO) {
        runCatching {
            // Try syndication API first
            val tweetId = Regex("(?:status|statuses)/(\d+)").find(url)?.groupValues?.get(1)
                ?: Regex("/(\d{10,})").find(url)?.groupValues?.get(1)
                ?: error("Tweet ID bulunamadı")

            val synUrl = "https://cdn.syndication.twimg.com/tweet-result?id=${'$'}tweetId&token=0"
            val synResp = HttpClient.getAsync(synUrl)

            if (synResp.isSuccessful) {
                val body = synResp.body?.string().orEmpty()
                if (body.isNotBlank()) {
                    // Parse JSON for video URL
                    val videoMatch = Regex(""variants":\[\{[^\]]*"url":"([^"]+\.mp4[^"]*)"")
                        .find(body)?.groupValues?.get(1)?.replace("\/", "/")
                    val imageMatch = Regex(""media_url":"(https://pbs\.twimg\.com[^"]+)"")
                        .find(body)?.groupValues?.get(1)?.replace("\/", "/")

                    val mediaUrl = videoMatch ?: imageMatch ?: error("Tweet medyası bulunamadı")
                    val title = Regex(""text":"([^"]+)"").find(body)?.groupValues?.get(1) ?: "Tweet"
                    val safeTitle = title.take(60).replace(Regex("[^\\w\\s.-]"), "_")
                    val type = if (videoMatch != null) MediaType.VIDEO else MediaType.IMAGE
                    val ext = if (videoMatch != null) "mp4" else "jpg"

                    return@withContext Result.success(MediaInfo(
                        url = mediaUrl,
                        title = safeTitle,
                        fileName = "x_${'$'}safeTitle.${'$'}ext",
                        mediaType = type,
                        platform = Platform.TWITTER
                    ))
                }
            }

            // Fallback: parse HTML
            val response = HttpClient.getAsync(url)
            val html = response.body?.string().orEmpty()
            val ogVideo = Regex("""<meta[^>]+property=["']og:video["'][^>]+content=["']([^"']+)["']""")
                .find(html)?.groupValues?.get(1)
            val ogImage = Regex("""<meta[^>]+property=["']og:image["'][^>]+content=["']([^"']+)["']""")
                .find(html)?.groupValues?.get(1)
            val mediaUrl = ogVideo ?: ogImage ?: error("Tweet medyası bulunamadı")
            val type = if (ogVideo != null) MediaType.VIDEO else MediaType.IMAGE
            val safeTitle = "tweet_${'$'}tweetId"
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
}
