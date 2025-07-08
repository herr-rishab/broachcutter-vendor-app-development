package net.broachcutter.vendorapp.util.timber

import android.util.Log
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.network.*
import timber.log.Timber
import javax.inject.Inject

class CrashlyticsTree @Inject constructor(private val firebaseHolder: FirebaseHolder) :
    Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        val crashlytics = firebaseHolder.crashlytics
        crashlytics.setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
        tag?.let { crashlytics.setCustomKey(CRASHLYTICS_KEY_TAG, it) }
        crashlytics.setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)

        if (priority == Log.INFO || priority == Log.WARN) {
            crashlytics.log("${getLogChar(priority)}/$tag: $message")
            return
        }
        // Timber.e gets till here
        if (throwable == null) {
            crashlytics.recordException(Exception(message))
        } else if (throwable is AppException) {
            val errorCode = throwable.errorCode
            // no need to log common exceptions that can't be resolved on our end
            if (!commonExceptions.contains(errorCode)) {
                crashlytics.recordException(throwable)
            }
        } else {
            // not an AppException, log it
            crashlytics.recordException(throwable)
        }
    }

    private fun getLogChar(logPriority: Int): String {
        return when (logPriority) {
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            else -> ""
        }
    }

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
        private val commonExceptions = listOf(
            HTTP_403_FORBIDDEN,
            HTTP_401_UNAUTHORIZED,
            HTTP_502_BAD_GATEWAY,
            NO_ACTIVE_CONNECTION,
            TIMEOUT
        )
    }
}
