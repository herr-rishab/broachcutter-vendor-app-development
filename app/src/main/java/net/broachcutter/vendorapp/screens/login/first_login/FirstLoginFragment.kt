package net.broachcutter.vendorapp.screens.login.first_login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentFirstLoginBinding
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.login.NewPasswordCodes
import javax.inject.Inject

/**
 * Asks for a new password the first time a user enters an app.
 */
class FirstLoginFragment : BaseFragment(), FirstLoginContract.View {

    private var presenter: FirstLoginPresenter? = null

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var analytics: Analytics

    private var _binding: FragmentFirstLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = FirstLoginFragment()
    }

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
        _binding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = FirstLoginPresenter(this, loginRepository, analytics)
        binding.submit.setOnClickListener {
            presenter!!.attemptSetPassword(
                binding.newPassword1.text.toString(),
                binding.newPassword2.text.toString()
            )
        }

        binding.newPassword2.setOnEditorActionListener(
            TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.submit.performClick()
                    return@OnEditorActionListener true
                }
                false
            }
        )
    }

    override fun onSetPasswordFailure(newPasswordCode: NewPasswordCodes) {
        // clear previous errors
        if (binding.newPassword1 != null) {
            binding.newPassword1.error = null
        }

        if (binding.newPassword2 != null) {
            binding.newPassword2.error = null
        }

        // show new error
        when (newPasswordCode) {
            NewPasswordCodes.EMPTY_PASSWORD1 -> {
                binding.newPassword1.error = getString(R.string.empty_password)
                binding.newPassword1.requestFocus()
            }
            NewPasswordCodes.EMPTY_PASSWORD2 -> {
                binding.newPassword2.error = getString(R.string.empty_password)
                binding.newPassword2.requestFocus()
            }
            NewPasswordCodes.PASSWORD_MISMATCH -> {
                showSnackbar(getString(R.string.login_passwords_mismatch))
            }
            NewPasswordCodes.PASSWORD_REQUIREMENTS -> {
                showSnackbar(getString(R.string.min_password_length))
            }
            else -> {
                // Unknown error code
                showSnackbar(getString(R.string.unknown_error_try_again))
            }
        }
    }

    private fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showProgress() {
        loadingDialog.show()
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
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
