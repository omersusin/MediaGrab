package media.grab.os.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

object HttpClient {
    val instance: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    fun get(url: String, userAgent: String = DEFAULT_UA): Response {
        val req = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "*/*")
            .header("Accept-Language", "en-US,en;q=0.9,tr;q=0.8")
            .build()
        return instance.newCall(req).execute()
    }

    suspend fun getAsync(url: String, userAgent: String = DEFAULT_UA): Response =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { get(url, userAgent) }

    const val DEFAULT_UA =
        "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36"
}
