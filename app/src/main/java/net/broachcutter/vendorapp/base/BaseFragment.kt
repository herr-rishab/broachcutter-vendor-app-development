package net.broachcutter.vendorapp.base

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dmax.dialog.SpotsDialog
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.R

abstract class BaseFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    fun getAppComponent() = DealerApplication.INSTANCE.appComponent

    protected lateinit var loadingDialog: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog =
            SpotsDialog.Builder().setContext(activity).setTheme(R.style.LoadingDialog).build()
    }
}
