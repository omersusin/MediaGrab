package media.grab.core.extractor

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object InstagramExtractor {

    private const val TAG = "MediaGrab_IG"
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun extractMediaUrl(postUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            // /share/ linkini çöz
            val resolvedUrl = if (postUrl.contains("/share/")) {
                Log.d(TAG, "→ Resolving /share/ link: $postUrl")
                resolveShareUrl(postUrl) ?: run {
                    Log.e(TAG, "✗ Failed to resolve /share/ link")
                    return@withContext null
                }
            } else {
                postUrl
            }
            Log.d(TAG, "✓ Resolved URL: $resolvedUrl")

            // Kısa kodu ayıkla
            val shortcode = extractShortcode(resolvedUrl)
            if (shortcode == null) {
                Log.e(TAG, "✗ Could not extract shortcode from: $resolvedUrl")
                return@withContext null
            }
            Log.d(TAG, "✓ Shortcode: $shortcode")

            // GraphQL isteği yap
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

            Log.d(TAG, "→ Sending GraphQL request...")
            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: run {
                Log.e(TAG, "✗ Empty response body")
                return@withContext null
            }
            Log.d(TAG, "✓ Response code: ${response.code}, body length: ${bodyString.length}")

            if (!response.isSuccessful) {
                Log.e(TAG, "✗ HTTP ${response.code}: ${bodyString.take(300)}")
                return@withContext null
            }

            // JSON ayrıştır
            val json = JSONObject(bodyString)
            val mediaData = json.getJSONObject("data").getJSONObject("xdt_shortcode_media")

            // Video
            val videoUrl = mediaData.optString("video_url", null)
            if (!videoUrl.isNullOrEmpty()) {
                Log.d(TAG, "✓ Found video URL")
                return@withContext videoUrl
            }

            // Carousel
            val carousel = mediaData.optJSONArray("edge_sidecar_to_children")
            if (carousel != null && carousel.length() > 0) {
                val firstNode = carousel.getJSONObject(0).getJSONObject("node")
                val carouselVideo = firstNode.optString("video_url", null)
                if (!carouselVideo.isNullOrEmpty()) {
                    Log.d(TAG, "✓ Found carousel video URL")
                    return@withContext carouselVideo
                }
                val displayUrl = firstNode.optString("display_url", null)
                Log.d(TAG, "✓ Found carousel display URL")
                return@withContext displayUrl
            }

            // Görsel
            val displayUrl = mediaData.optString("display_url", null)
            if (!displayUrl.isNullOrEmpty()) {
                Log.d(TAG, "✓ Found display URL")
                return@withContext displayUrl
            }

            Log.e(TAG, "✗ No media URL found in response")
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "✗ Exception: ${e.message}", e)
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
        val regex = Regex(
            "(?:https?://)?(?:www\\.)?instagram\\.com/" +
            "(?:stories/[a-zA-Z0-9._-]+/|p/|reel/|reels/|tv/|share/)" +
            "([a-zA-Z0-9_-]+)"
        )
        return regex.find(url)?.groupValues?.get(1)
    }
}
