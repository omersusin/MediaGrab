package media.grab.os.overlay.detection

import android.view.accessibility.AccessibilityNodeInfo
import media.grab.os.data.model.Platform
import media.grab.os.overlay.service.FloatingButtonController
import javax.inject.Inject
import javax.inject.Singleton

data class DetectedPost(val platform: Platform, val bounds: android.graphics.Rect, val viewId: String?)

@Singleton
class PostDetector @Inject constructor(private val controller: FloatingButtonController) {
    private var lastPackage: String? = null
    fun onWindowChanged(platform: Platform, root: AccessibilityNodeInfo) {
        val bounds = findMediaBounds(root, platform) ?: return
        controller.show(platform, bounds, root)
    }

    private fun findMediaBounds(root: AccessibilityNodeInfo, platform: Platform): android.graphics.Rect? {
        val viewId = when (platform) {
            Platform.INSTAGRAM -> "com.instagram.android:id/zoomable_view_container"
            Platform.TIKTOK -> "com.zhiliaoapp.musically:id/video_container"
            Platform.TWITTER -> "com.twitter.android:id/video_player"
            else -> null
        } ?: return null
        val nodes = root.findAccessibilityNodeInfosByViewId(viewId)
        val target = nodes.firstOrNull() ?: return null
        val rect = android.graphics.Rect()
        target.getBoundsInScreen(rect)
        return rect
    }
}