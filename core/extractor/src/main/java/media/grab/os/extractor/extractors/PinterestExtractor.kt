package media.grab.os.extractor.extractors

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.extractor.extractors.GenericExtractor

class PinterestExtractor : Extractor {
    override val platform = Platform.PINTEREST
    override fun canHandle(url: String) = "pinterest.com" in url.lowercase() || "pin.it" in url.lowercase()
    private val delegate = GenericExtractor()
    override suspend fun extract(url: String): Result<MediaInfo> = delegate.extract(url)
        .map { it.copy(platform = Platform.PINTEREST, fileName = "pin_${'$'}{it.fileName}") }
}
