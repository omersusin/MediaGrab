package media.grab.os.overlay.overlay

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class OverlayLifecycleOwner : SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val controller = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry = controller.savedStateRegistry
    fun init() { controller.performRestore(null); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
    fun destroy() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY) }
}