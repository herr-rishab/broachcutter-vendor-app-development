package net.broachcutter.vendorapp.screens.login.otp

import android.app.Activity
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.valartech.commons.aac.AbsentLiveData
import com.valartech.commons.network.google.Resource
import com.valartech.commons.network.google.Status.ERROR
import com.valartech.commons.network.google.Status.LOADING
import com.valartech.commons.network.google.Status.SUCCESS
import com.valartech.commons.utils.exceptions.MissingArgumentException
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.screens.login.LoginRepository
import ru.terrakok.cicerone.Router
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Set [signUpFlow] before using this VM.
 */
class OTPViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val router: Router,
    private val analytics: Analytics
) : ViewModel() {

    companion object {
        private const val COUNTDOWN_SEC = 30
        private const val COUNTDOWN_MS = COUNTDOWN_SEC * 1000L
        private const val COUNTDOWN_TICK = 1000L
    }

    private val _otp = MutableLiveData<String>()
    private val _countDownTimer = MediatorLiveData<Resource<Int>>()
    private val timerLiveData: MutableLiveData<Resource<Int>> = MutableLiveData()
    val countDownTimer: LiveData<Resource<Int>>
        get() = _countDownTimer

    private val timer = object : CountDownTimer(COUNTDOWN_MS, COUNTDOWN_TICK) {
        override fun onTick(millisUntilFinished: Long) {
            val secTillFinished = (millisUntilFinished / COUNTDOWN_TICK).toInt()
            _countDownTimer.value = Resource.success(secTillFinished)
        }

        override fun onFinish() {
        }
    }

    init {
        _countDownTimer.addSource(timerLiveData) {
            _countDownTimer.value = it
        }
        timer.start()
    }

    var signUpFlow: Boolean? = null

    val otpVerification: LiveData<Resource<Unit>> =
        _otp.switchMap { otp ->
            signUpFlow?.let {
                if (otp.isNullOrBlank()) {
                    AbsentLiveData.create()
                } else {
                    loginRepository.verifyOtp(otp, it)
                }
            } ?: throw MissingArgumentException("signUpFlow needs to be set")
        }

    fun verifyOtp(originalInput: String) {
        val input = originalInput.trim()
        _otp.value = input
    }

    fun resendOtp(activityRef: WeakReference<Activity>, phoneNumber: String) {
        signUpFlow?.let { signUpFlow ->
            val resendToCountdown =
                loginRepository.resendOtp(signUpFlow, activityRef, phoneNumber).map {
                    return@map when (it.status) {
                        SUCCESS -> {
                            timer.cancel()
                            timer.start()
                            Resource.success(COUNTDOWN_SEC)
                        }

                        ERROR -> Resource.error(it.message!!, null)
                        LOADING -> Resource.loading(null)
                    }
                }

            _countDownTimer.addSource(resendToCountdown) {
                _countDownTimer.value = it
            }
        }
    }

    fun onOtpVerified() {
        signUpFlow?.let { signUpFlow ->
            if (signUpFlow) {
                analytics.firstLoginSuccess()
            }
        }
        router.newRootScreen(Screens.Main())
    }

    fun editNumber() {
        router.exit()
    }
}
