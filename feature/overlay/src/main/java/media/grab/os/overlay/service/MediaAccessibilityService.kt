package media.grab.os.overlay.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import media.grab.os.overlay.detection.PostDetector

class MediaAccessibilityService : AccessibilityService() {
    private val detector = PostDetector()

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        detector.onAccessibilityEvent(event)
    }

    override fun onInterrupt() { /* TODO */ }
}
