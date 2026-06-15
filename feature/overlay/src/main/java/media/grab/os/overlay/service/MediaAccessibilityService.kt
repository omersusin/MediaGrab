package media.grab.os.overlay.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import dagger.hilt.android.AndroidEntryPoint
import media.grab.os.common.logging.Logger
import media.grab.os.data.model.Platform
import media.grab.os.overlay.detection.PostDetector
import javax.inject.Inject

@AndroidEntryPoint
class MediaAccessibilityService : AccessibilityService() {
    @Inject lateinit var postDetector: PostDetector

    override fun onServiceConnected() { super.onServiceConnected(); serviceInfo = serviceInfo.apply { eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_VIEW_SCROLLED } }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return
        val pkg = event?.packageName?.toString() ?: return
        val platform = Platform.fromPackage(pkg)
        if (platform == Platform.UNKNOWN) return
        postDetector.onWindowChanged(platform, root)
    }

    override fun onInterrupt() { Logger.w("A11Y", "Service interrupted") }
}