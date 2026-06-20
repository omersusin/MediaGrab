package media.grab.os.extractor

import media.grab.os.extractor.extractors.*

object ExtractorRegistry {
    private val extractors: List<Extractor> = listOf(
        InstagramExtractor(),
        TikTokExtractor(),
        TwitterExtractor(),
        FacebookExtractor(),
        YouTubeExtractor(),
        PinterestExtractor(),
        RedditExtractor(),
        TelegramExtractor(),
        GenericExtractor()
    )

    fun find(url: String): Extractor =
        extractors.firstOrNull { it.canHandle(url) } ?: GenericExtractor()

    suspend fun extract(url: String): Result<MediaInfo> = find(url).extract(url)
}
