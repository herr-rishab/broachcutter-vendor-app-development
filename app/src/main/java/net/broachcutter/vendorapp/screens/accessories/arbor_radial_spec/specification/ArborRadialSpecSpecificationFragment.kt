package net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.specification

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentAccessoriesArborRadialSpecSpecificationBinding
import net.broachcutter.vendorapp.models.ArborRequestModel
import net.broachcutter.vendorapp.screens.accessories.AccessoriesViewModel
import net.broachcutter.vendorapp.util.NothingSelectedSpinnerAdapter
import net.broachcutter.vendorapp.util.SpinnerInteractionListener
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.views.FlatRadioButtonGroup
import javax.inject.Inject

class ArborRadialSpecSpecificationFragment : BaseFragment() {
    private lateinit var morseTaperAdapter: ArrayAdapter<String>

    var depthOfCut: Double? = null
    var shankDiameter: Double? = null
    var morseTaperString: String? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AccessoriesViewModel>
    private val model: AccessoriesViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AccessoriesViewModel::class.java)
    }

    private var _binding: FragmentAccessoriesArborRadialSpecSpecificationBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
        morseTaperAdapter = ArrayAdapter(
            context,
            R.layout.cutter_type_spinner_item,
            R.id.cutterTypeOption,
            listOf("MT2", "MT3", "MT4", "MT5")
        )
        morseTaperAdapter.setDropDownViewResource(R.layout.cutter_type_spinner_dropdown_item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccessoriesArborRadialSpecSpecificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCutterTypeSpinner()

        binding.shankDiameterGroup.buttonSelectListener =
            object : FlatRadioButtonGroup.ButtonSelectListener {
                override fun onButtonSelect(buttonId: Int?) {
                    shankDiameter =
                        binding.shankDiameterGroup.selectedChild?.text.toString().toDoubleOrNull()
                }
            }

        binding.depthOfCutMmGroup.buttonSelectListener =
            object : FlatRadioButtonGroup.ButtonSelectListener {
                override fun onButtonSelect(buttonId: Int?) {
                    depthOfCut =
                        binding.depthOfCutMmGroup.selectedChild?.text.toString().toDoubleOrNull()
                }
            }

        val typeInteractionListener = SpinnerInteractionListener { _: View?, _: Int ->
            binding.search.isEnabled = true
        }
        binding.morseTaperMtSpinner.onItemSelectedListener = typeInteractionListener
        binding.morseTaperMtSpinner.setOnTouchListener(typeInteractionListener)

        binding.search.setOnClickListener {
            searchArbors()
        }
        isSearchEnabled()
    }

    private fun isSearchEnabled() {
        // perform an initial check if something is selected already. This is the case when the
        // user comes back from search results.
        binding.search.isEnabled = morseTaperString != null
    }

    private fun setupCutterTypeSpinner() {
        binding.morseTaperMtSpinner.adapter = NothingSelectedSpinnerAdapter(
            morseTaperAdapter,
            R.layout.cutter_type_spinner_select_one,
            requireContext()
        )
    }

    private fun searchArbors() {
        morseTaperString = binding.morseTaperMtSpinner.selectedItem as String
        morseTaperString?.let {
            model.onArborSearchClick(
                resources.getString(R.string.broachcutter_arbors_for_radial_drilling),
                ArborRequestModel(it, shankDiameter, depthOfCut)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        morseTaperString = null
        shankDiameter = null
        depthOfCut = null
        binding.morseTaperMtSpinner.setSelection(0)
        isSearchEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
