package media.grab.feature.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow

class OverlayManager(
    private val context: Context,
    private val onDownloadClick: () -> Unit
) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: ComposeView? = null
    private val lifecycleOwner = OverlayLifecycleOwner()
    private val downloadState = MutableStateFlow(DownloadStatus.IDLE)
    private var layoutParams: WindowManager.LayoutParams? = null

    fun show() {
        if (overlayView != null) return
        lifecycleOwner.initLifecycle()
        val composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setContent {
                androidx.compose.material3.MaterialTheme {
                    OverlayButton(
                        downloadState = downloadState,
                        onClick = onDownloadClick
                    )
                }
            }
        }
        overlayView = composeView

        layoutParams = WindowManager.LayoutParams(
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
        windowManager.addView(composeView, layoutParams)
    }

    fun updatePosition(bounds: Rect) {
        layoutParams?.let { params ->
            params.x = bounds.centerX() - 24.dpToPx(context)
            params.y = bounds.bottom - 48.dpToPx(context)
            try {
                windowManager.updateViewLayout(overlayView, params)
            } catch (_: Exception) { }
        }
    }

    fun hide() {
        overlayView?.let { windowManager.removeView(it) }
        lifecycleOwner.destroyLifecycle()
        overlayView = null
        layoutParams = null
    }

    fun setDownloadState(state: DownloadStatus) {
        downloadState.value = state
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
}
