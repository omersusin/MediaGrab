package media.grab.core.extractor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object InstagramExtractor {

    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun extractMediaUrl(postUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            // If it's a /share/ link, follow redirects to get the real URL first
            val resolvedUrl = if (postUrl.contains("/share/")) {
                resolveShareUrl(postUrl) ?: return@withContext null
            } else {
                postUrl
            }

            val shortcode = extractShortcode(resolvedUrl) ?: return@withContext null
            val docId = "10015901848480474"
            val variables = """{"shortcode":"$shortcode"}"""
            val requestBody = FormBody.Builder()
                .add("doc_id", docId)
                .add("variables", variables)
                .build()
            val request = Request.Builder()
                .url("https://www.instagram.com/api/graphql")
                .post(requestBody)
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36")
                .addHeader("X-IG-App-ID", "936619743392459")
                .build()

            val response = client.newCall(request).execute()
            val json = JSONObject(response.body?.string() ?: return@withContext null)
            val mediaData = json.getJSONObject("data").getJSONObject("xdt_shortcode_media")

            // Video
            val videoUrl = mediaData.optString("video_url", null)
            if (!videoUrl.isNullOrEmpty()) return@withContext videoUrl

            // Carousel
            val carousel = mediaData.optJSONArray("edge_sidecar_to_children")
            if (carousel != null && carousel.length() > 0) {
                val firstNode = carousel.getJSONObject(0).getJSONObject("node")
                val carouselVideo = firstNode.optString("video_url", null)
                if (!carouselVideo.isNullOrEmpty()) return@withContext carouselVideo
                return@withContext firstNode.optString("display_url", null)
            }

            // Image
            return@withContext mediaData.optString("display_url", null)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun resolveShareUrl(shareUrl: String): String? {
        return try {
            val request = Request.Builder()
                .url(shareUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()
            client.newCall(request).execute().use { response ->
                response.request.url.toString()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractShortcode(url: String): String? {
        // Supports: /p/, /reel/, /reels/, /tv/, /stories/
        val regex = Regex(
            "(?:https?://)?(?:www\\.)?instagram\\.com/" +
            "(?:stories/[a-zA-Z0-9._-]+/|p/|reel/|reels/|tv/|share/)" +
            "([a-zA-Z0-9_-]+)"
        )
        return regex.find(url)?.groupValues?.get(1)
    }
}
