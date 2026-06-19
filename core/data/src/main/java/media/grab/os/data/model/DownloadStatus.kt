package media.grab.os.data.model

enum class DownloadStatus(val displayName: String) {
    QUEUED("Queued"),
    DOWNLOADING("Downloading"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");
}
