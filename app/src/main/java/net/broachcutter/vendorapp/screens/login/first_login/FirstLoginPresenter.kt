package net.broachcutter.vendorapp.screens.login.first_login

import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.login.NewPasswordCodes.*

class FirstLoginPresenter(
    private val view: FirstLoginContract.View,
    private val loginRepository: LoginRepository,
    private val analytics: Analytics
) : FirstLoginContract.Presenter {

    override fun attemptSetPassword(password1: String, password2: String) {
        view.showProgress()
        loginRepository.attemptToSetPassword(password1, password2) {
            view.hideProgress()
            when (it) {
                SUCCESS -> {
                    analytics.firstLoginSetPasswordSuccess()
                    router.navigateTo(Screens.LoginLinkPhoneNumber())
                }
                EMPTY_PASSWORD1 -> view.onSetPasswordFailure(EMPTY_PASSWORD1)
                EMPTY_PASSWORD2 -> view.onSetPasswordFailure(EMPTY_PASSWORD2)
                PASSWORD_MISMATCH -> view.onSetPasswordFailure(PASSWORD_MISMATCH)
                PASSWORD_REQUIREMENTS -> view.onSetPasswordFailure(PASSWORD_REQUIREMENTS)
                RELOGIN_REQUIRED -> {
                    router.newRootScreen(Screens.LoginMain())
                }
                UNKNOWN_ERROR -> view.onSetPasswordFailure(UNKNOWN_ERROR)
            }
        }
    }
}
