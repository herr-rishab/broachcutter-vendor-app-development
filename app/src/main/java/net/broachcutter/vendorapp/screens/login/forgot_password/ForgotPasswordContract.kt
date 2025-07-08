package net.broachcutter.vendorapp.screens.login.forgot_password

import net.broachcutter.vendorapp.base.BaseMvpPresenter
import net.broachcutter.vendorapp.base.BaseMvpView
import net.broachcutter.vendorapp.screens.login.ResetPasswordCodes

interface ForgotPasswordContract {
    interface View : BaseMvpView<Presenter> {

        fun onSubmitEmailFailure(resetPasswordCode: ResetPasswordCodes)

        fun showProgress()

        fun hideProgress()

        fun showError(throwable: Throwable)

        fun showSuccess()
    }

    interface Presenter : BaseMvpPresenter {
        fun attemptResetPassword(email: String)
    }
}
