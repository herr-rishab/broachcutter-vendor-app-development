package net.broachcutter.vendorapp.screens.login.main

import android.app.Activity
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.screens.login.LoginCodes
import net.broachcutter.vendorapp.screens.login.LoginRepository
import java.lang.ref.WeakReference

class LoginPresenter(
    val view: LoginContract.View,
    private val loginRepository: LoginRepository,
    private val analytics: Analytics
) :
    LoginContract.Presenter {

    override fun attemptLogin(
        email: String,
        password: String,
        activityRef: WeakReference<Activity>
    ) {
        view.showProgress()
        loginRepository.attemptLogin(
            email, password,
            object : LoginRepository.LoginListener {
                override fun onLoginAttemptComplete(loginCode: LoginCodes, phoneNumber: String?) {
                    view.hideProgress()
                    when (loginCode) {
                        LoginCodes.SUCCESS -> router.newRootScreen(Screens.Main())
                        // phone number
                        LoginCodes.OTP_SENT -> router.navigateTo(Screens.RelogOtp(phoneNumber as String))
                        LoginCodes.SUCCESS_FIRST_LOGIN -> {
                            analytics.firstLoginPresetPasswordSuccess()
                            router.navigateTo(Screens.LoginNewPassword())
                        }
                        else -> {
                            view.onLoginFailure(loginCode)
                        }
                    }
                }

                override fun onLoginProcessing() {
                    view.showProgress()
                }
            },
            activityRef
        )
    }

    override fun onForgotPassword() {
        router.navigateTo(Screens.LoginForgotPassword())
    }
}
