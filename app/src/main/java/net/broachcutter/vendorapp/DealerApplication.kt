package net.broachcutter.vendorapp

import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.jakewharton.threetenabp.AndroidThreeTen
import com.onesignal.OneSignal
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import io.sentry.android.fragment.FragmentLifecycleIntegration
import io.sentry.android.timber.SentryTimberIntegration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.broachcutter.vendorapp.di.AppComponent
import net.broachcutter.vendorapp.di.AppModule
import net.broachcutter.vendorapp.di.DaggerAppComponent
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.util.DynamicBaseUrl
import net.broachcutter.vendorapp.util.timber.CrashlyticsTree
import net.broachcutter.vendorapp.util.timber.KotlinLinkingTree
import timber.log.Timber
import javax.inject.Inject

class DealerApplication : MultiDexApplication() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    @Inject
    lateinit var firebaseHolder: FirebaseHolder

    override fun onCreate() {
        super.onCreate()
        Timber.i("Initialising instance")
        appComponent.inject(this)
        if (BuildConfig.DEBUG || BuildConfig.FLAVOR == "dev") {
            Timber.plant(KotlinLinkingTree())
        } else {
            // release
            Timber.plant(CrashlyticsTree(firebaseHolder))
            sentrySetup(this)
        }
        INSTANCE = this
        AndroidThreeTen.init(this)
        initFirebase()
        DynamicBaseUrl.init()
        setupOneSignal()
    }

    private fun initFirebase() {
        val options = FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.FIREBASE_APPLICATION_ID)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .build()
        val firebaseApp = FirebaseApp.initializeApp(this, options, BuildConfig.FIREBASE_APP_NAME)
        firebaseHolder.setFirebaseApp(firebaseApp)
    }

    private fun setupOneSignal() {
        // OneSignal Initialization
        OneSignal.initWithContext(this, BuildConfig.ONESIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
    }

    companion object {
        lateinit var INSTANCE: DealerApplication
    }

    private fun sentrySetup(dealerApplication: DealerApplication) {
        SentryAndroid.init(dealerApplication) { options ->
            options.apply {
                dsn = BuildConfig.SENTRY_DSN
                sampleRate = 1.0
                isSendDefaultPii = true
                isAttachScreenshot = true
                maxBreadcrumbs = 150
                isAnrEnabled = true
                environment = BuildConfig.FLAVOR
                tracesSampleRate = 1.0
                beforeSend = SentryOptions.BeforeSendCallback { event, _ ->
                    val ex = event.throwable
                    if (ex is AppException && AppException.allowedErrors.contains(ex.errorCode)) {
                        null
                    } else {
                        event
                    }
                }
                enableAllAutoBreadcrumbs(true)
                addTracingOrigin("api.broachcutter.net")
                addIntegration(
                    SentryTimberIntegration(
                        minEventLevel = SentryLevel.WARNING,
                        minBreadcrumbLevel = SentryLevel.INFO
                    ),
                )
                addIntegration(
                    FragmentLifecycleIntegration(
                        this@DealerApplication
                    )
                )
            }
        }
        Timber.i("Sentry init called")
    }
}

val AppCompatActivity.app: DealerApplication
    get() = application as DealerApplication
