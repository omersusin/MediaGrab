package media.grab.os.extractor

interface Extractor {
    val platform: media.grab.os.data.model.Platform
    fun canHandle(url: String): Boolean
    suspend fun extract(url: String): Result<MediaInfo>
}
