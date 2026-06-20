package media.grab.os.extractor.extractors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.network.HttpClient
import org.json.JSONObject

class RedditExtractor : Extractor {
    override val platform = Platform.REDDIT
    override fun canHandle(url: String) =
        "reddit.com" in url.lowercase() || "redd.it" in url.lowercase()

    override suspend fun extract(url: String): Result<MediaInfo> = withContext(Dispatchers.IO) {
        runCatching {
            // Normalize URL -> add .json
            val cleanUrl = url.split("?")[0].trimEnd('/')
            val jsonUrl = if (cleanUrl.endsWith(".json")) cleanUrl else "${'$'}cleanUrl.json"

            val response = HttpClient.getAsync(jsonUrl, "Mozilla/5.0 (compatible; MediaGrab/1.0)")
            if (!response.isSuccessful) error("HTTP ${'$'}{response.code}")
            val body = response.body?.string() ?: error("Empty body")

            val json = JSONObject(body)
            // Reddit returns [post, comments] - take first
            val postData = json.getJSONArray("data").getJSONObject(0)
                .getJSONObject("data")
                .getJSONObject("children")
                .getJSONArray("data")
                .getJSONObject(0)
                .getJSONObject("data")

            val title = postData.optString("title", "Reddit post").take(60)
            val videoUrl = postData.optJSONObject("media")?.optString("reddit_video", "")
                ?.let { JSONObject(it).optString("fallback_url") }
                .orEmpty()
            val imageUrl = postData.optString("url_overridden_by_dest", "")
            val isVideo = postData.optBoolean("is_video", false)

            val finalUrl = when {
                isVideo && videoUrl.isNotEmpty() -> videoUrl
                imageUrl.contains(".mp4") -> imageUrl
                imageUrl.contains(".jpg") || imageUrl.contains(".png") || imageUrl.contains(".gif") -> imageUrl
                else -> error("No media found in Reddit post")
            }

            val type = when {
                finalUrl.contains(".mp4") -> MediaType.VIDEO
                finalUrl.contains(".gif") -> MediaType.IMAGE
                else -> MediaType.IMAGE
            }
            val ext = when {
                finalUrl.contains(".mp4") -> "mp4"
                finalUrl.contains(".gif") -> "gif"
                finalUrl.contains(".png") -> "png"
                else -> "jpg"
            }

            val safeTitle = title.replace(Regex("[^\\w\\s.-]"), "_")
            MediaInfo(
                url = finalUrl,
                title = safeTitle,
                fileName = "rd_${'$'}safeTitle.${'$'}ext",
                mediaType = type,
                platform = Platform.REDDIT
            )
        }
    }
}
