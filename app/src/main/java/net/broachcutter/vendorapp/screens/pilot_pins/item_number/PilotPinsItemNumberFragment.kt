package net.broachcutter.vendorapp.screens.pilot_pins.item_number

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.utils.extensions.hideKeyboard
import com.valartech.commons.utils.extensions.longToast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentPilotPinItemNumberBinding
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.setSafeOnClickListener
import javax.inject.Inject

class PilotPinsItemNumberFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<PilotPinsItemNumberViewModel>
    private val model: PilotPinsItemNumberViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PilotPinsItemNumberViewModel::class.java)
    }

    private var _binding: FragmentPilotPinItemNumberBinding? = null
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
        _binding = FragmentPilotPinItemNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.numberEntry.setOnEditorActionListener(
            TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    attemptSearch()
                    return@OnEditorActionListener true
                }
                false
            }
        )
        binding.pilotPinSearch.setSafeOnClickListener { attemptSearch() }
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
