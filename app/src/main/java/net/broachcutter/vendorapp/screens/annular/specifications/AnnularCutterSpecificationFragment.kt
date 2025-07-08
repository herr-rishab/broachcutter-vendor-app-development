package net.broachcutter.vendorapp.screens.annular.specifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.valartech.commons.utils.extensions.hideKeyboard
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentAnnularCutterSpecificationsBinding
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterShank
import net.broachcutter.vendorapp.util.NothingSelectedSpinnerAdapter
import net.broachcutter.vendorapp.util.SpinnerInteractionListener
import net.broachcutter.vendorapp.views.FlatRadioButtonGroup

class AnnularCutterSpecificationFragment : BaseVMFragment<AnnularCutterSpecificationsViewModel>() {

    override val vmClassToken: Class<AnnularCutterSpecificationsViewModel>
        get() = AnnularCutterSpecificationsViewModel::class.java

    private lateinit var typeOfAnnularCutterAdapter: ArrayAdapter<CutterMaterial>
    private var selectedCutterMaterial: CutterMaterial? = null
    private var selectedDepthOfCut: Int? = null
    private var selectedShank: CutterShank? = null

    private var _binding: FragmentAnnularCutterSpecificationsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)

        val cutterTypeNames = getAnnularCutterTypeNames()
        typeOfAnnularCutterAdapter = ArrayAdapter(
            context,
            R.layout.cutter_type_spinner_item,
            R.id.cutterTypeOption,
            cutterTypeNames
        )
        typeOfAnnularCutterAdapter.setDropDownViewResource(R.layout.cutter_type_spinner_dropdown_item)
    }

    private fun getAnnularCutterTypeNames(): Array<CutterMaterial> {
        return CutterMaterial.values()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnularCutterSpecificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCutterTypeSpinner()
        binding.cutterSearch.setOnClickListener {
            activity?.hideKeyboard()
            viewModel.search(
                selectedCutterMaterial!!,
                selectedShank,
                selectedDepthOfCut,
                binding.diameterOfCutterEdit.text.toString().toFloatOrNull()
            )
        }

        binding.depthOfCutGroup.buttonSelectListener =
            object : FlatRadioButtonGroup.ButtonSelectListener {
                override fun onButtonSelect(buttonId: Int?) {
                    selectedDepthOfCut =
                        binding.depthOfCutGroup?.selectedChild?.text.toString().toIntOrNull()
                }
            }

        binding.depthOfCutGroup.selectChildWithTextLike(selectedDepthOfCut?.toString())

        binding.shankGroup.selectChildWithTextLike(getString(R.string.weldon))

        // Set default selectedShank
        selectedShank =
            CutterShank.getCutterShank(binding.shankGroup?.selectedChild?.text.toString())

        binding.shankGroup.buttonSelectListener =
            object : FlatRadioButtonGroup.ButtonSelectListener {
                override fun onButtonSelect(buttonId: Int?) {
                    selectedShank = CutterShank
                        .getCutterShank(binding.shankGroup?.selectedChild?.text.toString())
                }
            }

        updateSearchEnabled()
    }

    private fun updateSearchEnabled() {
        binding.cutterSearch.isEnabled = selectedCutterMaterial != null
    }

    private fun setupCutterTypeSpinner() {
        binding.typeOfAnnularCutterSpinner.adapter = NothingSelectedSpinnerAdapter(
            typeOfAnnularCutterAdapter,
            R.layout.cutter_type_spinner_select_one,
            requireContext()
        )

        /**
         * while coming back to this screen,
         * fragment get recreated,so we need to update the selected depth
         */
        selectedCutterMaterial?.let {
            updateViews(it)
        }

        val typeInteractionListener = SpinnerInteractionListener { _: View?, itemPosition: Int ->
            val selectedCutterType = CutterMaterial.getCutterMaterial(itemPosition)
            selectedCutterMaterial = selectedCutterType
            updateViews(selectedCutterType)
        }
        binding.typeOfAnnularCutterSpinner.onItemSelectedListener = typeInteractionListener
        binding.typeOfAnnularCutterSpinner.setOnTouchListener(typeInteractionListener)
    }

    private fun updateViews(selectedCutterMaterial: CutterMaterial) {
        when (selectedCutterMaterial) {
            CutterMaterial.TCT -> binding.doc25.setText(R.string.doc_35)
            CutterMaterial.HSS -> binding.doc25.setText(R.string.doc_25)
        }
        // deselect depth of cut since we're changing the values of the children
        binding.depthOfCutGroup.deselectAll()
        selectedDepthOfCut = null

        updateSearchEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
