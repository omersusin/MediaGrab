package media.grab.os.extractor.extractors

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.extractor.extractors.GenericExtractor

class RedditExtractor : Extractor {
    override val platform = Platform.REDDIT
    override fun canHandle(url: String) = "reddit.com" in url.lowercase() || "redd.it" in url.lowercase()
    private val delegate = GenericExtractor()
    override suspend fun extract(url: String): Result<MediaInfo> = delegate.extract(url)
        .map { it.copy(platform = Platform.REDDIT, fileName = "rd_${'$'}{it.fileName}") }
}
