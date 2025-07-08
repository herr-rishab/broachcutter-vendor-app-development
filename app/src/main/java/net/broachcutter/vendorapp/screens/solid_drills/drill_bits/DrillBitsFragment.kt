package net.broachcutter.vendorapp.screens.solid_drills.drill_bits

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.utils.extensions.hideKeyboard
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentDrillBitsBinding
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.solid_drills.home.AutoCompleteTextViewAdapter
import net.broachcutter.vendorapp.screens.solid_drills.home.SolidDrillAndDrillBitsViewModel
import net.broachcutter.vendorapp.screens.solid_drills.home.SolidDrillAndDrillBitsViewModel.Companion.SOLID_DRILL_MAX_MM
import net.broachcutter.vendorapp.screens.solid_drills.home.SolidDrillAndDrillBitsViewModel.Companion.SOLID_DRILL_MIN_MM
import net.broachcutter.vendorapp.util.ViewModelFactory
import javax.inject.Inject

class DrillBitsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SolidDrillAndDrillBitsViewModel>

    lateinit var adapter: ArrayAdapter<String>

    private val drillDiameterOptions = getDrillBitsOptionsValue()
    private var selectedDrillDiameterValue: Int = drillDiameterOptions[0]

    private val model: SolidDrillAndDrillBitsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)
            .get(SolidDrillAndDrillBitsViewModel::class.java)
    }

    private var _binding: FragmentDrillBitsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)

        setupAdapter(context)
    }

    private fun setupAdapter(context: Context) {
        adapter =
            AutoCompleteTextViewAdapter(
                context,
                R.layout.dropdown_menu_item_view,
                getDrillBitsOptionsString()
            )
    }

    private fun getDrillBitsOptionsString(): List<String> {
        val options = ArrayList<String>()
        for (i in SOLID_DRILL_MIN_MM..SOLID_DRILL_MAX_MM) {
            options.add(getString(R.string.int_mm, i))
        }
        return options
    }

    private fun getDrillBitsOptionsValue(): ArrayList<Int> {
        val options = ArrayList<Int>()
        for (i in SOLID_DRILL_MIN_MM..SOLID_DRILL_MAX_MM) {
            options.add(i)
        }
        return options
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrillBitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropDown()
        binding.search.setOnClickListener {
            activity?.hideKeyboard()
            model.searchSolidDrillAndDrillBits(
                selectedDrillDiameterValue.toFloat(),
                ListType.DRILL_BITS
            )
        }
    }

    private fun setupDropDown() {
        binding.drillBitsDiameterDropdown.inputType = InputType.TYPE_NULL
        binding.drillBitsDiameterDropdown.setAdapter(adapter)
        binding.drillBitsDiameterDropdown.setText(getDrillBitsOptionsString()[0], false)
        binding.drillBitsDiameterDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedDrillDiameterValue = drillDiameterOptions[position]
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
