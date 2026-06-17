package media.grab.os.extractor

interface Extractor {
    val name: String
    val supportedHosts: List<String>
    suspend fun extract(url: String): ExtractionResult
}

data class ExtractionResult(
    val success: Boolean,
    val mediaUrl: String? = null,
    val thumbnail: String? = null,
    val title: String? = null,
    val error: String? = null
)
