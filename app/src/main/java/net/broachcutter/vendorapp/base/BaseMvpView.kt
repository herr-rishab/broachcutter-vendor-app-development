package net.broachcutter.vendorapp.base

/**
 * Created by amitavk on 26/06/17.
 */
interface BaseMvpView<in T : BaseMvpPresenter> {

    fun setLoading(isLoading: Boolean) {}
//    fun setPresenter(presenter: T)

//    fun showError(error: String?)
//
//    fun showError(@StringRes stringResId: Int)
//
//    fun showMessage(@StringRes stringResId: Int)
//
//    fun showMessage(message: String)
}
