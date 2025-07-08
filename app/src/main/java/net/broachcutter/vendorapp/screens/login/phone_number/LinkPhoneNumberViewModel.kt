package net.broachcutter.vendorapp.screens.login.phone_number

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.valartech.commons.aac.AbsentLiveData
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.Screens.SignUpOtp
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.login.VerificationState
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

class LinkPhoneNumberViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val router: Router,
    private val analytics: Analytics
) : ViewModel() {

    companion object {
        private const val COUNTRY_PREFIX = "+91"
    }

    private val phoneNumber = MutableLiveData<String>()
    private lateinit var activityRef: WeakReference<Activity>

    val verificationState: LiveData<Resource<VerificationState>> =
        phoneNumber.switchMap { phoneNumber ->
            if (phoneNumber.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                loginRepository.enrollMultiFactorAuth(COUNTRY_PREFIX + phoneNumber, activityRef)
            }
        }

    fun onNext(phoneNumber: String, activityRef: WeakReference<Activity>) {
        this.activityRef = activityRef
        this.phoneNumber.value = phoneNumber
    }

    fun onOtpSent(verificationState: VerificationState?) {
        Timber.d("verificationState: $verificationState")
        verificationState?.let {
            if (it.isVerified) {
                analytics.firstLoginSuccess()
                router.newRootScreen(Screens.Main())
            } else {
                router.navigateTo(SignUpOtp(phoneNumber.value!!))
            }
        }
    }
}
