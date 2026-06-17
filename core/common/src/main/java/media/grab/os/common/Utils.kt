package media.grab.os.common

import java.util.regex.Pattern

object Utils {
    private val URL_PATTERN: Pattern = Pattern.compile(
        """https?://[\w\-._~:/?#\[\]@!$&'()*+,;=%]+"""
    )

    fun extractUrls(text: String): List<String> {
        val matcher = URL_PATTERN.matcher(text)
        val urls = mutableListOf<String>()
        while (matcher.find()) {
            urls.add(matcher.group())
        }
        return urls
    }

    fun isValidUrl(url: String): Boolean = URL_PATTERN.matcher(url).matches()
}
