package media.grab.os

import android.app.Application
import timber.log.Timber

class MediaGrabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
