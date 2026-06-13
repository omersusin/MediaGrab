package media.grab.feature.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import kotlinx.coroutines.flow.MutableStateFlow

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: ComposeView? = null
    private val lifecycleOwner = OverlayLifecycleOwner()
    private val downloadState = MutableStateFlow(DownloadStatus.IDLE)

    fun show() {
        lifecycleOwner.initLifecycle()
        val composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                androidx.compose.material3.MaterialTheme {
                    OverlayButton(downloadState)
                }
            }
        }
        overlayView = composeView

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }
        windowManager.addView(composeView, params)
    }

    fun hide() {
        overlayView?.let { windowManager.removeView(it) }
        lifecycleOwner.destroyLifecycle()
        overlayView = null
    }
}
