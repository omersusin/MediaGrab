package media.grab.os.access

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import media.grab.os.data.model.Platform
import media.grab.os.download.DownloadService

/**
 * Shows a one-tap floating download button while the user is in a supported social app.
 * On tap it scans the current screen for a URL and queues a download; otherwise opens the app.
 */
class MediaAccessibilityService : AccessibilityService() {

    private var controller: FloatingButtonController? = null
    private var currentPackage: String = ""

    private val targets = setOf(
        "com.instagram.android",
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.trill",
        "com.twitter.android",
        "com.facebook.katana",
        "com.pinterest"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        controller = FloatingButtonController(this) { onButtonTap() }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg == currentPackage) return
        currentPackage = pkg
        if (pkg in targets) controller?.show() else controller?.hide()
    }

    private fun onButtonTap() {
        val url = findUrl(rootInActiveWindow)
        if (url != null) {
            DownloadService.start(this, url)
            Toast.makeText(this, "MediaGrab: downloading…", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No link found — opening MediaGrab to paste.", Toast.LENGTH_SHORT).show()
            val launch = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            launch?.let { startActivity(it) }
        }
    }

    private fun findUrl(node: AccessibilityNodeInfo?): String? {
        if (node == null) return null
        val regex = Regex("https?://[^\\s\"']+")
        val texts = listOfNotNull(node.text?.toString(), node.contentDescription?.toString())
        for (t in texts) {
            regex.find(t)?.value?.let { return it.trimEnd('.', ',', ')') }
        }
        for (i in 0 until node.childCount) {
            findUrl(node.getChild(i))?.let { return it }
        }
        return null
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        controller?.hide()
        controller = null
        super.onDestroy()
    }
}
