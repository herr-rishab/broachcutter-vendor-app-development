package net.broachcutter.vendorapp.screens.login.first_login

import net.broachcutter.vendorapp.base.BaseMvpPresenter
import net.broachcutter.vendorapp.base.BaseMvpView
import net.broachcutter.vendorapp.screens.login.NewPasswordCodes

/**
 * Created by amitavk on 17/08/17.
 */
interface FirstLoginContract {
    interface View : BaseMvpView<Presenter> {

        fun onSetPasswordFailure(newPasswordCode: NewPasswordCodes)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter : BaseMvpPresenter {
        fun attemptSetPassword(password1: String, password2: String)
    }
}
