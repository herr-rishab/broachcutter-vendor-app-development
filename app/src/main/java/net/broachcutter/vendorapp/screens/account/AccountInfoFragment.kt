package net.broachcutter.vendorapp.screens.account

import android.content.Context
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.valartech.commons.network.google.Status.ERROR
import com.valartech.commons.network.google.Status.LOADING
import com.valartech.commons.network.google.Status.SUCCESS
import com.valartech.commons.utils.extensions.formatINRwithPrefix
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentAccountInfoBinding
import net.broachcutter.vendorapp.models.UserDetail

class AccountInfoFragment : BaseVMFragment<AccountInfoViewModel>() {

    override val vmClassToken: Class<AccountInfoViewModel>
        get() = AccountInfoViewModel::class.java

    private var _binding: FragmentAccountInfoBinding? = null
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
        _binding = FragmentAccountInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userDetail.observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource?.status) {
                SUCCESS -> {
                    setDetails(resource.data)
                }

                ERROR -> resource.message?.let { longToast(it) }
                LOADING -> {
                }

                else -> {}
            }
        }
        binding.signOut.setOnClickListener { viewModel.signOut() }
        binding.resetPassword.setOnClickListener {
            viewModel.resetPassword().observe(
                viewLifecycleOwner,
                Observer { resource ->
                    when (resource?.status) {
                        SUCCESS -> {
                            longToast(getString(R.string.reset_email_sent))
                        }

                        ERROR -> resource.message?.let { message -> longToast(message) }
                        LOADING -> toast(getString(R.string.sending))
                        else -> {}
                    }
                }
            )
        }
    }

    private fun setDetails(userDetail: UserDetail?) {
        userDetail?.run {
            binding.accountDetailsCard.dealerName.text = name
            binding.accountDetailsCard.dealerEmail.text = email
            @Suppress("DEPRECATION")
            binding.accountDetailsCard.dealerNumber.text =
                PhoneNumberUtils.formatNumber(phoneNumber)
            val currentCredit = credit.availableCredit.formatINRwithPrefix(context)
            val maxCredit = credit.creditLimit.formatINRwithPrefix(context)
            binding.accountDetailsCard.dealerCredit.text =
                getString(R.string.credit_left, currentCredit, maxCredit)
            binding.dealerAddressDetail.text = addresses?.get(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
