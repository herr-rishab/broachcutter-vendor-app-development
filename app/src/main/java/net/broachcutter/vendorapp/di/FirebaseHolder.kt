package net.broachcutter.vendorapp.di

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.broachcutter.vendorapp.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseHolder @Inject constructor(
    private val appContext: Application
) {

    fun setFirebaseApp(app: FirebaseApp) {
        auth = FirebaseAuth.getInstance(app)
        phoneAuth = PhoneAuthProvider.getInstance(auth)
        // todo check if crashes get logged to the correct firebase app
        crashlytics = FirebaseCrashlytics.getInstance()
        remoteConfig = FirebaseRemoteConfig.getInstance(app)
        analytics = FirebaseAnalytics.getInstance(appContext)
    }

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
        private set

    var phoneAuth: PhoneAuthProvider = PhoneAuthProvider.getInstance()
        private set

    var crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
        private set

    var remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        private set

    var analytics: FirebaseAnalytics = run {
        val instance = FirebaseAnalytics.getInstance(appContext)
        instance.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        instance
    }
        private set
}
