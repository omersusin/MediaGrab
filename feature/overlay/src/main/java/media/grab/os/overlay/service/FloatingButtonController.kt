package media.grab.os.overlay.service

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import dagger.hilt.android.qualifiers.ApplicationContext
import media.grab.os.data.model.Platform
import media.grab.os.overlay.overlay.OverlayLifecycleOwner
import media.grab.os.overlay.ui.FloatingButton
import javax.inject.Singleton

@Singleton
class FloatingButtonController @Inject constructor(@ApplicationContext private val context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null
    private var lifecycleOwner: OverlayLifecycleOwner? = null

    fun show(platform: Platform, bounds: Rect, hint: Any) {
        if (composeView != null) return
        val owner = OverlayLifecycleOwner().also { it.init() }
        lifecycleOwner = owner
        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START; x = bounds.centerX() - 60; y = bounds.bottom - 80 }
        val view = ComposeView(context).apply { setViewTreeLifecycleOwner(owner); setViewTreeSavedStateRegistryOwner(owner); setContent { FloatingButton(onClick = { /* TODO trigger extraction */ }) } }
        runCatching { windowManager.addView(view, params); composeView = view }
    }

    fun hide() {
        composeView?.let { runCatching { windowManager.removeView(it) } }
        composeView = null
        lifecycleOwner?.destroy()
        lifecycleOwner = null
    }
}