package net.broachcutter.vendorapp.screens.holesaws.item_number

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.utils.extensions.hideKeyboard
import com.valartech.commons.utils.extensions.longToast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentHolesawsItemNumberBinding
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.setSafeOnClickListener
import javax.inject.Inject

class HolesawsItemNumberFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HolesawsItemNumberViewModel>
    private val model: HolesawsItemNumberViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(HolesawsItemNumberViewModel::class.java)
    }

    private var _binding: FragmentHolesawsItemNumberBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHolesawsItemNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.numberEntry.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                attemptSearch()
                true
            } else {
                false
            }
        }
        binding.holesawSearch.setSafeOnClickListener { attemptSearch() }
    }

    private fun attemptSearch() {
        val itemNumber = binding.numberEntry.text.toString()
        if (itemNumber.isNotBlank()) {
            activity?.hideKeyboard()
            model.search(itemNumber)
        } else {
            longToast(R.string.item_number_needed)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
