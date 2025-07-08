package net.broachcutter.vendorapp.screens.login.forgot_password

import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.login.ResetPasswordCodes
import timber.log.Timber

class ForgotPasswordPresenter(
    val view: ForgotPasswordContract.View,
    private val loginRepository: LoginRepository
) : ForgotPasswordContract.Presenter {

    private var disposable: Disposable? = null

    override fun attemptResetPassword(email: String) {
        view.showProgress()
        val resetResultObservable = loginRepository.resetPassword(email)
        disposable =
            resetResultObservable.subscribeWith(object :
                    DisposableObserver<ResetPasswordCodes>() {
                    override fun onComplete() {
                        view.hideProgress()
                        view.showSuccess()
                        router.backTo(Screens.LoginMain())
                    }

                    override fun onNext(resetResult: ResetPasswordCodes) {
                        when (resetResult) {
                            ResetPasswordCodes.SUCCESS -> router.navigateTo(Screens.LoginForgotPassword())
                            ResetPasswordCodes.INVALID_EMAIL -> view.onSubmitEmailFailure(
                                ResetPasswordCodes.INVALID_EMAIL
                            )
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                        view.showError(e)
                    }
                })
    }

    override fun stop() {
        val dis = disposable
        if (dis != null && !dis.isDisposed) {
            dis.dispose()
        }
        super.stop()
    }
}
