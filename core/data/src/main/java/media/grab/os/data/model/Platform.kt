package media.grab.os.data.model

enum class Platform(val displayName: String) {
    INSTAGRAM("Instagram"),
    TIKTOK("TikTok"),
    TWITTER("X / Twitter"),
    FACEBOOK("Facebook"),
    YOUTUBE("YouTube"),
    PINTEREST("Pinterest"),
    REDDIT("Reddit"),
    TELEGRAM("Telegram"),
    OTHER("Other");

    companion object {
        fun fromUrl(url: String): Platform {
            val u = url.lowercase()
            return when {
                "instagram.com" in u -> INSTAGRAM
                "tiktok.com" in u -> TIKTOK
                "twitter.com" in u || "x.com" in u -> TWITTER
                "facebook.com" in u || "fb.com" in u -> FACEBOOK
                "youtube.com" in u || "youtu.be" in u -> YOUTUBE
                "pinterest.com" in u -> PINTEREST
                "reddit.com" in u -> REDDIT
                "t.me" in u || "telegram" in u -> TELEGRAM
                else -> OTHER
            }
        }
    }
}
