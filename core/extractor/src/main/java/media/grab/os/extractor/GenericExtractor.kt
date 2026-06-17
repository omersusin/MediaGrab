package media.grab.os.extractor

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenericExtractor @Inject constructor() : Extractor {
    override val name = "Generic"
    override val supportedHosts = listOf("instagram.com", "tiktok.com", "twitter.com", "x.com")
    override suspend fun extract(url: String): ExtractionResult {
        return ExtractionResult(success = false, error = "Generic extractor not implemented")
    }
}
