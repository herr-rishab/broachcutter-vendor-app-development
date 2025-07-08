package net.broachcutter.vendorapp.screens.holesaws.specifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.valartech.commons.utils.extensions.hideKeyboard
import com.valartech.commons.utils.extensions.toast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentHolesawSpecificationsBinding

class HolesawsSpecificationFragment : BaseVMFragment<HolesawsSpecificationsViewModel>() {

    override val vmClassToken: Class<HolesawsSpecificationsViewModel>
        get() = HolesawsSpecificationsViewModel::class.java

    private var _binding: FragmentHolesawSpecificationsBinding? = null
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
        _binding = FragmentHolesawSpecificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.holesawDiameter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.holesawSearch.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val diameter = binding.holesawDiameter.text.toString().toIntOrNull()
        if (diameter != null) {
            activity?.hideKeyboard()
            viewModel.search(diameter)
        } else {
            toast(R.string.diameter_needed)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
