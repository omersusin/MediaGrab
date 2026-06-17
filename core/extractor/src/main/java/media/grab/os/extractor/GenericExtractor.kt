package media.grab.os.extractor

class GenericExtractor : Extractor {
    override val name = "Generic"
    override val supportedHosts = listOf("instagram.com", "tiktok.com", "twitter.com", "x.com")
    override suspend fun extract(url: String): ExtractionResult {
        return ExtractionResult(success = false, error = "Generic extractor not implemented")
    }
}
