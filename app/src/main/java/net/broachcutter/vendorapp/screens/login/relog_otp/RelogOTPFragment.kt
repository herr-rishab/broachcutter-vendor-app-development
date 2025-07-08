package net.broachcutter.vendorapp.screens.login.relog_otp

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import com.google.android.material.snackbar.Snackbar
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentRelogOtpBinding
import net.broachcutter.vendorapp.screens.login.otp.OTPViewModel
import java.lang.ref.WeakReference

/**
 * User needs to verify their phone number via OTP when logging into the app after completing signup
 * as well.
 *
 * Shares the viewmodel with [SignupOTPFragment]
 */
class RelogOTPFragment : BaseVMFragment<OTPViewModel>() {

    private var _binding: FragmentRelogOtpBinding? = null
    private val binding get() = _binding!!

    override val vmClassToken: Class<OTPViewModel>
        get() = OTPViewModel::class.java

    private var phoneNumber: String? = null

    companion object {
        private const val ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER"
        private const val MASKED_PHONE_NUM_START = 9 // last 4 digits of +919876543210

        fun newInstance(phoneNumber: String): RelogOTPFragment {
            val bundle = bundleOf(ARG_PHONE_NUMBER to phoneNumber)
            val fragment = RelogOTPFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)

        viewModel.signUpFlow = false
        arguments?.let {
            phoneNumber = it.getString(ARG_PHONE_NUMBER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelogOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.retryText.paintFlags = binding.retryText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.otpDescription.text = getString(
            R.string.registered_otp_sent,
            phoneNumber
        )
        binding.confirmButton.setOnClickListener {
            if (binding.otp.text.toString().isNotEmpty()) {
                viewModel.verifyOtp(binding.otp.text.toString())
            } else {
                toast(R.string.enter_otp)
            }
        }
        binding.otp.setOnEditorActionListener(
            TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.verifyOtp(binding.otp.text.toString())
                    return@OnEditorActionListener true
                }
                false
            }
        )

        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewModel.otpVerification.observe(viewLifecycleOwner) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    showLoading(false)
                    viewModel.onOtpVerified()
                }

                Status.ERROR -> {
                    showLoading(false)
                    val rootView = binding.root
                    rootView?.let {
                        val errorMsg = resource.message
                        errorMsg?.let {
                            Snackbar.make(rootView, errorMsg, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }

                Status.LOADING -> showLoading(true)
                else -> {}
            }
        }
        viewModel.countDownTimer.observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    resource.data?.let { sec ->
                        if (sec > 0) {
                            binding.retryProgress.visibility = View.GONE
                            binding.retryText.visibility = View.VISIBLE
                            binding.retryText.text = getString(R.string.retry_help, sec)
                            binding.retryText.setOnClickListener { }
                        } else {
                            binding.retryText.text = getString(R.string.retry_now)
                            binding.retryText.setOnClickListener {
                                binding.retryProgress.visibility = View.VISIBLE
                                binding.retryText.visibility = View.GONE
                                viewModel.resendOtp(WeakReference(activity), phoneNumber!!)
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    binding.retryProgress.visibility = View.GONE
                    binding.retryText.visibility = View.VISIBLE
                    resource.message?.let { longToast(it) }
                }

                Status.LOADING -> {
                }

                else -> {}
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

    override fun onDestroy() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
