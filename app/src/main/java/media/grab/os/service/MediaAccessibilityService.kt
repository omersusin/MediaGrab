package media.grab.os.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaAccessibilityService : AccessibilityService() {

    companion object {
        val currentMediaBounds: MutableStateFlow<Rect?> = MutableStateFlow(null)
        private val SUPPORTED_PACKAGES = setOf(
            "com.instagram.android",
            "com.zhiliaoapp.musically",
            "com.twitter.android",
            "com.facebook.katana"
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode = rootInActiveWindow ?: return
        val packageName = event?.packageName?.toString() ?: return
        if (SUPPORTED_PACKAGES.contains(packageName)) {
            findMediaContainer(rootNode, packageName)
        }
    }

    private fun findMediaContainer(node: AccessibilityNodeInfo, packageName: String) {
        val targetIds = when (packageName) {
            "com.instagram.android" -> listOf("com.instagram.android:id/zoomable_view_container")
            "com.zhiliaoapp.musically" -> listOf("com.zhiliaoapp.musically:id/player_container")
            "com.twitter.android" -> listOf("com.twitter.android:id/tweet_media_view")
            "com.facebook.katana" -> listOf("com.facebook.katana:id/video_container")
            else -> emptyList()
        }

        if (node.viewIdResourceName in targetIds) {
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            currentMediaBounds.value = bounds
            Log.d("MediaGrab", "Media container found: $bounds")
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findMediaContainer(it, packageName) }
        }
    }

    override fun onInterrupt() {}
}
