package net.broachcutter.vendorapp.base

abstract class BaseViewActivity<P : BaseMvpPresenter> : BaseActivity(), BaseMvpView<P> {

    lateinit var presenter: P

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onPause() {
        super.onPause()
        presenter.stop()
    }
}
