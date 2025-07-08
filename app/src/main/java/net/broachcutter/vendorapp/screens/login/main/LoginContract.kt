package net.broachcutter.vendorapp.screens.login.main

import android.app.Activity
import net.broachcutter.vendorapp.base.BaseMvpPresenter
import net.broachcutter.vendorapp.base.BaseMvpView
import net.broachcutter.vendorapp.screens.login.LoginCodes
import java.lang.ref.WeakReference

/**
 * Created by amitavk on 20/06/17.
 */
interface LoginContract {
    interface View : BaseMvpView<Presenter> {

        fun onLoginFailure(loginErrorCode: LoginCodes)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter : BaseMvpPresenter {
        fun attemptLogin(email: String, password: String, activityRef: WeakReference<Activity>)

        fun onForgotPassword()
    }
}
