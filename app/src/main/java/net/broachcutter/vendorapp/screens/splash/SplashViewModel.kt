package net.broachcutter.vendorapp.screens.splash

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.valartech.commons.aac.SingleLiveEvent
import com.valartech.commons.network.google.Resource
import com.valartech.commons.network.google.Status.ERROR
import com.valartech.commons.network.google.Status.LOADING
import com.valartech.commons.network.google.Status.SUCCESS
import net.broachcutter.vendorapp.BuildConfig
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.models.UserDetail
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.util.Constants
import net.broachcutter.vendorapp.util.SharedPreferenceKeys
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val router: Router,
    private val sharedPreferences: SharedPreferences,
    firebaseHolder: FirebaseHolder
) : ViewModel() {

    private val remoteConfig = firebaseHolder.remoteConfig
    val showDialog: LiveData<UpdateDialog>
        get() = _showDialog
    private val _showDialog = SingleLiveEvent<UpdateDialog>()
    var coupon: Coupon? = null

    private val fetchUserDetail = MutableLiveData<Unit>()
    private val userDetailLiveData = fetchUserDetail.switchMap {
        userRepository.refreshUserDetails()
    }
    private var userDetailObserver = Observer<Resource<UserDetail>> {
        when (it.status) {
            SUCCESS -> router.navigateTo(Screens.Main())
            ERROR -> _showDialog.postValue(UpdateDialog.Error(AppException(it.throwable)))
            LOADING -> { // do nothing
            }
        }
    }

    companion object {
        const val FIREBASE_CONFIG_CACHE_HOURS = 12L
        const val FIREBASE_CONFIG_FETCH_TIMEOUT_SEC = 15L

        private const val MIN_RECOMMENDED_VERSION_KEY = "min_recommended_version"
        private const val MIN_RECOMMENDED_VERSION_DEFAULT = 55
        private const val MIN_REQUIRED_VERSION_KEY = "min_required_version"
        private const val MIN_REQUIRED_VERSION_DEFAULT = 51
        private const val COUPONS_ENABLED_KEY = "coupons_enabled"

        val defaultsMap = mapOf(
            MIN_RECOMMENDED_VERSION_KEY to MIN_RECOMMENDED_VERSION_DEFAULT,
            MIN_REQUIRED_VERSION_KEY to MIN_REQUIRED_VERSION_DEFAULT,
            COUPONS_ENABLED_KEY to false
        )
    }

    init {
        val cacheDuration = if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toSeconds(
            FIREBASE_CONFIG_CACHE_HOURS
        )
        val configSettings = remoteConfigSettings {
            fetchTimeoutInSeconds = FIREBASE_CONFIG_FETCH_TIMEOUT_SEC
            minimumFetchIntervalInSeconds = cacheDuration
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(defaultsMap)
    }

    //    @HunterDebug
    fun runChecks(skipUpdate: Boolean = false) {
        if (skipUpdate) {
            checkLogin()
        } else {
            checkForAppUpdate()
        }
    }

    // @HunterDebug
    private fun checkForAppUpdate() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("remoteConfig.fetchAndActivate success")
                if (task.result == true) {
                    // successfully fetched values
                    Timber.i("remoteConfig.fetchAndActivate remote values activated")
                } else {
                    // no configs were fetched from the backend and
                    // the local fetched configs have already been activated
                    Timber.i("remoteConfig.fetchAndActivate local values used")
                }
                updateFeatureFlags()
                when {
                    isUpdateRequired() -> // force update
                        _showDialog.postValue(UpdateDialog.Required)

                    isUpdateRecommended() -> // optional update
                        _showDialog.postValue(UpdateDialog.Recommended)

                    else -> // usual app flow
                        checkLogin()
                }
            } else {
                // exception, don't let the user proceed
                task.exception?.let {
                    val ex = AppException(it)
                    Timber.e(ex)
                    _showDialog.postValue(UpdateDialog.Error(ex))
                }
            }
        }
    }

    private fun updateFeatureFlags() {
        val couponsEnabled = remoteConfig.getBoolean(COUPONS_ENABLED_KEY)
        sharedPreferences.edit(commit = true) {
            putBoolean(SharedPreferenceKeys.COUPONS_ENABLED, couponsEnabled)
        }
    }

    private fun isUpdateRequired(): Boolean {
        val requiredVersion = remoteConfig.getLong(MIN_REQUIRED_VERSION_KEY)
        return BuildConfig.VERSION_CODE < requiredVersion
    }

    private fun isUpdateRecommended(): Boolean {
        val recommendedVersion = remoteConfig.getLong(MIN_RECOMMENDED_VERSION_KEY)
        return BuildConfig.VERSION_CODE < recommendedVersion
    }

    private fun checkLogin() {
        loginRepository.checkUserLogin { isLoggedIn ->
            if (isLoggedIn) {
                Timber.i("User is already logged in")
                if (coupon != null) {
                    val args = bundleOf(
                        Constants.COUPON to coupon
                    )
                    router.navigateTo(Screens.MainTransition(args))
                } else {
                    router.navigateTo(Screens.Main())
                } // refresh user details
                userDetailLiveData.observeForever(userDetailObserver)
//                fetchUserDetail.value = Unit
            } else {
                Timber.i("User is logged out")
                router.navigateTo(Screens.Login())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userDetailLiveData.removeObserver(userDetailObserver)
    }
}
