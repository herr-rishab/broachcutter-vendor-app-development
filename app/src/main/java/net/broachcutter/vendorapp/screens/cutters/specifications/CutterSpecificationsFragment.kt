@file:Suppress("TooManyFunctions")

package net.broachcutter.vendorapp.screens.cutters.specifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.valartech.commons.utils.extensions.hideKeyboard
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentCutterSpecificationsBinding
import net.broachcutter.vendorapp.util.NothingSelectedSpinnerAdapter
import net.broachcutter.vendorapp.util.SpinnerInteractionListener
import net.broachcutter.vendorapp.views.FlatRadioButtonGroup

/**
 * A simple [Fragment] subclass.
 *
 */
@Deprecated("See AnnularCuttersHomeFragment")
class CutterSpecificationsFragment : BaseVMFragment<CutterSpecificationsViewModel>() {

    companion object {
        const val SOLID_DRILL_MIN_MM = 5
        const val SOLID_DRILL_MAX_MM = 10
        const val HOLESAW_DOC = 5
        const val SOLID_DRILL_DOC = 35
    }

    override val vmClassToken: Class<CutterSpecificationsViewModel>
        get() = CutterSpecificationsViewModel::class.java

    private var selectedCutterType: CutterType? = null
    private var selectedCutterMaterial: CutterMaterial? = null
    private var selectedDepthOfCut: Int? = null
    private var selectedDiameter: Float? = null

    private lateinit var typeOfCutterAdapter: ArrayAdapter<CutterType>
    private lateinit var solidDrillDiameterAdapter: ArrayAdapter<String>

    private var _binding: FragmentCutterSpecificationsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent()
            .inject(this)

        // cutter type adapter setup
        val cutterTypeNames = getCutterTypeNames()
        typeOfCutterAdapter = ArrayAdapter(
            context,
            R.layout.cutter_type_spinner_item,
            R.id.cutterTypeOption,
            cutterTypeNames
        )
        typeOfCutterAdapter.setDropDownViewResource(R.layout.cutter_type_spinner_dropdown_item)

        // solid drill diameter adapter setup
        val solidDrillOptions = getSolidDrillOptions()
        solidDrillDiameterAdapter = ArrayAdapter(
            context,
            R.layout.cutter_type_spinner_item,
            R.id.cutterTypeOption,
            solidDrillOptions
        )
        solidDrillDiameterAdapter.setDropDownViewResource(R.layout.cutter_type_spinner_dropdown_item)
    }

    private fun getSolidDrillOptions(): List<String> {
        val options = ArrayList<String>()
        for (i in SOLID_DRILL_MIN_MM..SOLID_DRILL_MAX_MM) {
            options.add(getString(R.string.int_mm, i))
        }
        return options
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // reset selected values
        selectedDepthOfCut = null
        selectedCutterMaterial = null
        selectedDiameter = null
        // don't reset selectedCutterType for the sake of user context
        // Inflate the layout for this fragment
        _binding = FragmentCutterSpecificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.search.setOnClickListener {
            executeSearch()
        }

        setupCutterTypeSpinner()
    }

    private fun setupCutterTypeSpinner() {
        binding.typeOfCutterSpinner.adapter = NothingSelectedSpinnerAdapter(
            typeOfCutterAdapter,
            R.layout.cutter_type_spinner_select_one,
            context!!
        )
        val typeInteractionListener = SpinnerInteractionListener { view: View?, i: Int ->
            setCutterTypeLayout()
        }
        binding.typeOfCutterSpinner.onItemSelectedListener = typeInteractionListener
        binding.typeOfCutterSpinner.setOnTouchListener(typeInteractionListener)
    }

    private fun setupSolidDrillDiameterSpinner(diameterSpinner: Spinner) {
        diameterSpinner.adapter = NothingSelectedSpinnerAdapter(
            solidDrillDiameterAdapter,
            R.layout.cutter_type_spinner_select_one,
            context!!
        )
        val diameterInteractionListener = SpinnerInteractionListener { view: View?, i: Int ->
            val diameter = (diameterSpinner.selectedItem as String)
                .substringBefore(' ')
                .toFloatOrNull()
            selectedDiameter = diameter
        }
        diameterSpinner.onItemSelectedListener = diameterInteractionListener
        diameterSpinner.setOnTouchListener(diameterInteractionListener)
    }

    private fun setCutterTypeLayout() {
        selectedCutterType = binding.typeOfCutterSpinner.selectedItem as CutterType?
        binding.cutterLayoutContainer.run {
            // clear existing
            removeAllViews()
            // get the current selected item
            val selectedCutterType = selectedCutterType ?: return
            // add the new view
            layoutInflater.inflate(selectedCutterType.layoutRes, this)
        }
        binding.search.isEnabled = true // disabled by default in xml

        setupCutterListeners()
    }

    private fun setupCutterListeners() {
        when (selectedCutterType) {
            CutterType.ANNULAR -> {
                // cutter material
                val materialGroup =
                    binding.cutterLayoutContainer.findViewById<FlatRadioButtonGroup>(R.id.cutterMaterial)
                materialGroup.buttonSelectListener =
                    object : FlatRadioButtonGroup.ButtonSelectListener {
                        override fun onButtonSelect(buttonId: Int?) {
                            selectedCutterMaterial = when (buttonId) {
                                R.id.tctButton -> CutterMaterial.TCT
                                R.id.hssButton -> CutterMaterial.HSS
//                                R.id.solidDrillButton -> CutterMaterial.SOLID_DRILL
                                else -> null
                            }
                        }
                    }
                // depth of cut
                val depthOfCutGroup =
                    binding.cutterLayoutContainer.findViewById<FlatRadioButtonGroup>(R.id.depthOfCutGroup)
                depthOfCutGroup.buttonSelectListener =
                    object : FlatRadioButtonGroup.ButtonSelectListener {
                        override fun onButtonSelect(buttonId: Int?) {
                            selectedDepthOfCut =
                                depthOfCutGroup?.selectedChild?.text.toString().toIntOrNull()
                        }
                    }
                // diameter
                val diameterEditText =
                    binding.cutterLayoutContainer.findViewById<EditText>(R.id.diameterEntry)
                setSearchExecute(diameterEditText)
            }
//            CutterType.SOLID_DRILL -> {
//                selectedDepthOfCut = SOLID_DRILL_DOC
//                val diameterSpinner =
//                    binding.cutterLayoutContainer.findViewById<Spinner>(R.id.diameterSpinner)
//                setupSolidDrillDiameterSpinner(diameterSpinner)
//            }
            CutterType.INVALID -> {
            }

            null -> {
            }

            CutterType.HOLESAW -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        setCutterTypeLayout()
    }

    private fun setSearchExecute(editText: EditText) {
        editText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    executeSearch()
                    true
                }

                else -> false
            }
        }
    }

    private fun getCutterTypeNames(): Array<CutterType> {
        return CutterType.values()
    }

    private fun executeSearch() {
        viewModel.search(
            cutterType = selectedCutterType!!,
            cutterMaterial = selectedCutterMaterial,
            depthOfCut = selectedDepthOfCut,
            diameter = 0f
//            diameter = getDiameter()
        )
        activity?.hideKeyboard()
    }

//    private fun getDiameter(): Float?
//        if (selectedCutterType == CutterType.SOLID_DRILL) {
//            this value gets set on spinner selection
//            selectedDiameter
//        } else
// {
//            val diameterEditText = binding.cutterLayoutContainer.findViewById<EditText>(R.id.diameterEntry)
//            diameterEditText.text.toString().toFloatOrNull()
//        }
}
