package media.grab.os.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import media.grab.feature.overlay.OverlayManager

class MediaAccessibilityService : AccessibilityService() {

    companion object {
        val currentMediaBounds: MutableStateFlow<Rect?> = MutableStateFlow(null)
        val currentMediaUrl: MutableStateFlow<String?> = MutableStateFlow(null)
        private val SUPPORTED_PACKAGES = setOf(
            "com.instagram.android",
            "com.zhiliaoapp.musically",
            "com.twitter.android",
            "com.facebook.katana"
        )
    }

    private var overlayManager: OverlayManager? = null
    private var lastKnownBounds: Rect? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlayManager = OverlayManager(this) {
            currentMediaUrl.value?.let { url ->
                val intent = Intent(this, DownloadService::class.java).apply {
                    action = DownloadService.ACTION_DOWNLOAD
                    putExtra(DownloadService.EXTRA_URL, url)
                }
                startService(intent)
                overlayManager?.setDownloadState(media.grab.feature.overlay.DownloadStatus.DOWNLOADING)
                Log.d("MediaGrab", "Download started for: $url")
            }
        }
        Log.d("MediaGrab", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode = rootInActiveWindow ?: return
        val packageName = event?.packageName?.toString() ?: return
        if (SUPPORTED_PACKAGES.contains(packageName)) {
            findMediaContainer(rootNode, packageName)
            findShareLink(rootNode)
            updateOverlay()
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
            lastKnownBounds = bounds
            Log.d("MediaGrab", "Media container found: $bounds")
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findMediaContainer(it, packageName) }
        }
    }

    private fun findShareLink(node: AccessibilityNodeInfo) {
        if (node.isClickable && node.text != null) {
            val text = node.text.toString()
            if (text.contains("instagram.com") || text.contains("tiktok.com") ||
                text.contains("twitter.com") || text.contains("facebook.com")) {
                currentMediaUrl.value = text
                Log.d("MediaGrab", "Found share link: $text")
            }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findShareLink(it) }
        }
    }

    private fun updateOverlay() {
        if (lastKnownBounds != null && currentMediaUrl.value != null) {
            overlayManager?.show()
            overlayManager?.updatePosition(lastKnownBounds!!)
        } else {
            overlayManager?.hide()
        }
    }

    override fun onDestroy() {
        overlayManager?.hide()
        overlayManager = null
        super.onDestroy()
    }

    override fun onInterrupt() {}
}
