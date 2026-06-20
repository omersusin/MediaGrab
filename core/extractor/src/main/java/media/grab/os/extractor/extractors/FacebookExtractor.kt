package media.grab.os.extractor.extractors

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.extractor.extractors.GenericExtractor

class FacebookExtractor : Extractor {
    override val platform = Platform.FACEBOOK
    override fun canHandle(url: String) =
        "facebook.com" in url.lowercase() || "fb.com" in url.lowercase()

    // FB og:tags use prefix og:video:url and og:image
    private val delegate = GenericExtractor()
    override suspend fun extract(url: String): Result<MediaInfo> = delegate.extract(url)
        .map { it.copy(platform = Platform.FACEBOOK, fileName = "fb_${'$'}{it.fileName}") }
}
