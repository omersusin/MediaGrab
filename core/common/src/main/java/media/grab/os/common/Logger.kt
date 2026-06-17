package media.grab.os.common

import timber.log.Timber

object Logger {
    fun d(tag: String, message: String) = Timber.d("[$tag] $message")
    fun e(tag: String, message: String, t: Throwable? = null) =
        if (t != null) Timber.e(t, "[$tag] $message") else Timber.e("[$tag] $message")
    fun w(tag: String, message: String) = Timber.w("[$tag] $message")
}
