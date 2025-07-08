package net.broachcutter.vendorapp.screens.login.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentForgotPasswordSuccessBinding

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordSuccessFragment : BaseFragment() {

    companion object {
        const val ARGS_EMAIL = "args_email"
        fun newInstance(email: String): ForgotPasswordSuccessFragment {
            val fragment = ForgotPasswordSuccessFragment()
            val args = Bundle()
            args.putString(ARGS_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }

    private var email: String? = null

    private var _binding: FragmentForgotPasswordSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString(ARGS_EMAIL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val router = DealerApplication.INSTANCE.appComponent.getRouter()
        binding.backToLoginButton.setOnClickListener { router.newRootScreen(Screens.LoginMain()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
