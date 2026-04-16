package com.media.grab.grabber

object MediaDetector {

    data class PlatformInfo(
        val name: String,
        val icon: String,
        val isSupported: Boolean,
        val requiresGrabber: Boolean
    )

    fun detectPlatform(url: String): PlatformInfo {
        return when {
            url.contains("youtube.com") || url.contains("youtu.be") ->
                PlatformInfo("YouTube", "youtube", true, false)

            url.contains("tiktok.com") ->
                PlatformInfo("TikTok", "tiktok", true, false)

            url.contains("instagram.com") ->
                PlatformInfo("Instagram", "instagram", false, true)

            url.contains("facebook.com") || url.contains("fb.watch") ->
                PlatformInfo("Facebook", "facebook", true, false)

            url.contains("twitter.com") || url.contains("x.com") ->
                PlatformInfo("Twitter", "twitter", true, false)

            url.contains("snap.com") || url.contains("snapchat.com") ->
                PlatformInfo("Snapchat", "snapchat", false, true)

            url.contains("pinterest.com") ->
                PlatformInfo("Pinterest", "pinterest", true, false)

            url.contains("reddit.com") ->
                PlatformInfo("Reddit", "reddit", true, false)

            else ->
                PlatformInfo("Unknown", "link", true, false)
        }
    }

    fun isMediaUrl(url: String): Boolean {
        val mediaPatterns = listOf(
            ".mp4", ".m3u8", ".webm", ".mkv", ".avi",
            ".mp3", ".m4a", ".wav", ".flac", ".aac",
            "video", "media", "stream", "download"
        )
        return mediaPatterns.any { url.contains(it, ignoreCase = true) }
    }

    fun extractVideoId(platform: String, url: String): String? {
        return when (platform) {
            "YouTube" -> extractYouTubeId(url)
            "TikTok" -> extractTikTokId(url)
            "Instagram" -> extractInstagramId(url)
            else -> null
        }
    }

    private fun extractYouTubeId(url: String): String? {
        val patterns = listOf(
            Regex("(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/shorts/)([a-zA-Z0-9_-]{11})"),
            Regex("youtube\\.com/embed/([a-zA-Z0-9_-]{11})")
        )
        patterns.forEach { pattern ->
            pattern.find(url)?.let { return it.groupValues[1] }
        }
        return null
    }

    private fun extractTikTokId(url: String): String? {
        Regex("/video/(\\d+)").find(url)?.let { return it.groupValues[1] }
        return null
    }

    private fun extractInstagramId(url: String): String? {
        Regex("/reel/([A-Za-z0-9_-]+)").find(url)?.let { return it.groupValues[1] }
        Regex("/reels?/([A-Za-z0-9_-]+)").find(url)?.let { return it.groupValues[1] }
        return null
    }
}
