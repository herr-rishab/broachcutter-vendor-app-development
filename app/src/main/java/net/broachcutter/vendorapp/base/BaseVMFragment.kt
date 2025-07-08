package net.broachcutter.vendorapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.broachcutter.vendorapp.util.ViewModelFactory
import javax.inject.Inject

abstract class BaseVMFragment<VM : ViewModel> : BaseFragment() {

    protected abstract val vmClassToken: Class<VM>

    protected val viewModel: VM by lazy {
        ViewModelProvider(this, viewModelFactory).get(vmClassToken)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<VM>

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        (activity as BaseActivity)
//            .getApplicationComponent()
//            .inject(this)
//    }
}
