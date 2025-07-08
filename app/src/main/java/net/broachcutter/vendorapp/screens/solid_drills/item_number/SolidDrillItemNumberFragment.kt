package net.broachcutter.vendorapp.screens.solid_drills.item_number

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.utils.extensions.hideKeyboard
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentSolidDrillItemNumberBinding
import net.broachcutter.vendorapp.screens.solid_drills.home.SolidDrillAndDrillBitsViewModel
import net.broachcutter.vendorapp.util.ViewModelFactory
import javax.inject.Inject

class SolidDrillItemNumberFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SolidDrillAndDrillBitsViewModel>

    private val model: SolidDrillAndDrillBitsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)
            .get(SolidDrillAndDrillBitsViewModel::class.java)
    }

    private var _binding: FragmentSolidDrillItemNumberBinding? = null
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
        _binding = FragmentSolidDrillItemNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemNumberField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.search.isEnabled = s?.length!! > 0
            }
        })

        binding.search.setOnClickListener {
            activity?.hideKeyboard()
            model.searchSolidDrillAndDrillBitsByItemNumber(binding.itemNumberField.text.toString())
        }
    }
}
