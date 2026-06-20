package media.grab.os.extractor.extractors

import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.extractor.Extractor
import media.grab.os.extractor.MediaInfo
import media.grab.os.extractor.extractors.GenericExtractor

class TelegramExtractor : Extractor {
    override val platform = Platform.TELEGRAM
    override fun canHandle(url: String) = "t.me" in url.lowercase() || "telegram" in url.lowercase()
    private val delegate = GenericExtractor()
    override suspend fun extract(url: String): Result<MediaInfo> = delegate.extract(url)
        .map { it.copy(platform = Platform.TELEGRAM, fileName = "tg_${'$'}{it.fileName}") }
}
