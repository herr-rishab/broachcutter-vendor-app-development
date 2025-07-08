package net.broachcutter.vendorapp.screens.login.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.valartech.commons.utils.extensions.hideKeyboard
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentLoginViewBinding
import net.broachcutter.vendorapp.screens.login.LoginCodes
import net.broachcutter.vendorapp.screens.login.LoginCodes.DEVELOPER_ERROR
import net.broachcutter.vendorapp.screens.login.LoginCodes.DISABLED_USER
import net.broachcutter.vendorapp.screens.login.LoginCodes.INVALID_EMAIL
import net.broachcutter.vendorapp.screens.login.LoginCodes.INVALID_PHONE_NUMBER
import net.broachcutter.vendorapp.screens.login.LoginCodes.LOCAL_VALIDATION_SUCCESS
import net.broachcutter.vendorapp.screens.login.LoginCodes.NETWORK_ERROR
import net.broachcutter.vendorapp.screens.login.LoginCodes.OTP_SENT
import net.broachcutter.vendorapp.screens.login.LoginCodes.RELOGIN_REQUIRED
import net.broachcutter.vendorapp.screens.login.LoginCodes.SUCCESS
import net.broachcutter.vendorapp.screens.login.LoginCodes.SUCCESS_FIRST_LOGIN
import net.broachcutter.vendorapp.screens.login.LoginCodes.TOKEN_RETRIEVAL_FAILURE
import net.broachcutter.vendorapp.screens.login.LoginCodes.TOO_MANY_ATTEMPTS
import net.broachcutter.vendorapp.screens.login.LoginCodes.UNKNOWN_ERROR
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.util.getProximaNovaRegTypeface
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Login view asking for username and password.
 */
class LoginFragment : BaseFragment(), LoginContract.View {

    private var _binding: FragmentLoginViewBinding? = null
    private val binding get() = _binding!!

    private var presenter: LoginPresenter? = null

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var router: Router

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    //region LifeCycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = LoginPresenter(this, loginRepository, analytics)

        binding.loginButton.typeface = activity?.assets?.getProximaNovaRegTypeface()

        binding.loginButton.setOnClickListener {
            presenter!!.attemptLogin(
                binding.emailInput.text.toString(),
                binding.passwordInput.text.toString(),
                WeakReference(activity)
            )
        }

        binding.forgotPasswordText.setOnClickListener {
            presenter!!.onForgotPassword()
        }

        binding.passwordInput.addTextChangedListener(passwordWatcher)
        binding.passwordInput.setOnEditorActionListener(
            TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.loginButton.performClick()
                    return@OnEditorActionListener true
                }
                false
            }
        )
    }

    @Suppress("ComplexMethod")
    override fun onLoginFailure(loginErrorCode: LoginCodes) {
        // dismiss so that we can see our snackbar errors
        activity?.hideKeyboard()
        // clear all previous errors
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null

        // show new errors
        when (loginErrorCode) {
            LoginCodes.EMPTY_EMAIL -> {
                binding.emailInputLayout.error = getString(R.string.empty_username)
                binding.emailInput.requestFocus()
            }

            LoginCodes.EMPTY_PASSWORD -> {
                binding.passwordInputLayout.error = getString(R.string.empty_password)
                binding.passwordInput.requestFocus()
            }

            LoginCodes.AUTH_FAILURE -> {
                binding.passwordInputLayout.error = getString(R.string.login_auth_failure)
                binding.passwordInput.requestFocus()
            }

            LoginCodes.INVALID_USER -> {
                binding.emailInputLayout.error = getString(R.string.unregistered_email)
                binding.emailInput.requestFocus()
            }

            DISABLED_USER -> {
                binding.emailInputLayout.error = getString(R.string.user_disabled)
            }

            INVALID_EMAIL -> {
                binding.emailInputLayout.error = getString(R.string.enter_valid_email)
                binding.emailInput.requestFocus()
            }

            TOO_MANY_ATTEMPTS -> showError(getString(R.string.unusual_activity))
            NETWORK_ERROR -> showError(getString(R.string.error_no_internet_available))
            DEVELOPER_ERROR -> showError(getString(R.string.error_retry))
            LOCAL_VALIDATION_SUCCESS, SUCCESS_FIRST_LOGIN, SUCCESS, OTP_SENT, INVALID_PHONE_NUMBER -> {
                Timber.e("Bad LoginCode: $loginErrorCode")
                // we should never get these values here
                showError(getString(R.string.error_retry))
            }

            TOKEN_RETRIEVAL_FAILURE -> showError(getString(R.string.trouble_with_server))
            RELOGIN_REQUIRED -> {
                router.newRootScreen(Screens.LoginMain())
            }

            UNKNOWN_ERROR -> showError(getString(R.string.unknown_error))
        }
    }

    private fun showError(message: String) {
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

    private val passwordWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding.passwordInputLayout.isErrorEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
