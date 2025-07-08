package net.broachcutter.vendorapp.screens.login.phone_number

import android.content.Context
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.valartech.commons.aac.observeFreshly
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.toast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentPhoneNumberBinding
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.network.RECENT_LOGIN_REQUIRED
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Link first-time user phone numbers to their email ID.
 */
class LinkPhoneNumberFragment : BaseVMFragment<LinkPhoneNumberViewModel>() {

    @Inject
    lateinit var router: Router

    private var _binding: FragmentPhoneNumberBinding? = null
    private val binding get() = _binding!!

    override val vmClassToken: Class<LinkPhoneNumberViewModel>
        get() = LinkPhoneNumberViewModel::class.java

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.phoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        viewModel.verificationState.observeFreshly(
            viewLifecycleOwner,
            { resource ->
                Timber.d("Resource status: ${resource.status}")
                // todo figure why we're not getting success call here when auto verification happens
                when (resource.status) {
                    Status.SUCCESS -> {
                        showLoading(false)
                        viewModel.onOtpSent(resource.data)
                    }

                    Status.ERROR -> {
                        val exception = resource.throwable
                        if (exception != null &&
                            exception is AppException &&
                            exception.errorCode == RECENT_LOGIN_REQUIRED
                        ) {
                            router.newRootScreen(Screens.LoginMain())
                        } else {
                            showLoading(false)
                            val errorMsg = resource.message
                            errorMsg?.let {
                                Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }

                    Status.LOADING -> showLoading(true)
                }
            }
        )
        binding.confirmButton.setOnClickListener {
            if (binding.phoneNumber.text.toString().isNotEmpty()) {
                viewModel.onNext(binding.phoneNumber.text.toString(), WeakReference(activity))
            } else {
                toast(R.string.invalid_phone_number)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
