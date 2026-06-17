package media.grab.os.overlay.detection

import android.view.accessibility.AccessibilityEvent

data class PostCandidate(
    val packageName: String,
    val postId: String,
    val mediaUrls: List<String>,
    val timestamp: Long
)

class PostDetector {
    fun onAccessibilityEvent(event: AccessibilityEvent): PostCandidate? {
        return null
    }
}
