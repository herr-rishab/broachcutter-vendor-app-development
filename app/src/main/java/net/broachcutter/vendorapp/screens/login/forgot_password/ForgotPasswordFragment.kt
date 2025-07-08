package net.broachcutter.vendorapp.screens.login.forgot_password

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.valartech.commons.utils.extensions.longToast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentForgotPasswordBinding
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.login.ResetPasswordCodes
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : BaseFragment(), ForgotPasswordContract.View {

    private var presenter: ForgotPasswordPresenter? = null

    @Inject
    lateinit var loginRepository: LoginRepository

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ForgotPasswordPresenter(this, loginRepository)

        binding.emailInput.requestFocus()

        binding.resetLinkButton.setOnClickListener {
            presenter!!.attemptResetPassword(binding.emailInput.text.toString())
        }
    }

    override fun onSubmitEmailFailure(resetPasswordCode: ResetPasswordCodes) {
        // clear previous errors
        binding.emailInputLayout.error = null

        if (resetPasswordCode == ResetPasswordCodes.INVALID_EMAIL) {
            binding.emailInputLayout.error = getString(R.string.enter_valid_email)
            binding.emailInput.requestFocus()
        }
    }

    override fun showError(throwable: Throwable) {
        throwable.message?.let { longToast(it) }
    }

    override fun showSuccess() {
        longToast(getString(R.string.forget_password_success))
    }

    override fun showProgress() {
        loadingDialog.show()
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
