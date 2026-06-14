package media.grab.os

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object YtDlpFallback {
    suspend fun resolve(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf("yt-dlp", "--get-url", "--no-playlist", url)
            )
            val output = process.inputStream.bufferedReader().readText().trim()
            if (output.isNotEmpty() && output.startsWith("http")) output else null
        } catch (e: Exception) {
            null
        }
    }
}
