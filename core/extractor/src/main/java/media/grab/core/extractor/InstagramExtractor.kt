package media.grab.core.extractor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object InstagramExtractor {

    private val client = OkHttpClient()

    suspend fun extractMediaUrl(postUrl: String): String? = withContext(Dispatchers.IO) {
        val shortcode = extractShortcode(postUrl) ?: return@withContext null
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

        try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body?.string() ?: return@withContext null)
            val mediaData = json.getJSONObject("data").getJSONObject("xdt_shortcode_media")
            // Try video first, then carousel, then image
            val videoUrl = mediaData.optString("video_url", null)
            if (!videoUrl.isNullOrEmpty()) return@withContext videoUrl

            val carousel = mediaData.optJSONArray("edge_sidecar_to_children")
            if (carousel != null && carousel.length() > 0) {
                // Return first item's media URL (simplified)
                val firstNode = carousel.getJSONObject(0).getJSONObject("node")
                val carouselVideo = firstNode.optString("video_url", null)
                if (!carouselVideo.isNullOrEmpty()) return@withContext carouselVideo
                return@withContext firstNode.optString("display_url", null)
            }

            return@withContext mediaData.optString("display_url", null)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun extractShortcode(url: String): String? {
        val regex = Regex("(?:instagram\\.com/(?:p|reel|tv)/([A-Za-z0-9_-]+))")
        return regex.find(url)?.groupValues?.get(1)
    }
}
