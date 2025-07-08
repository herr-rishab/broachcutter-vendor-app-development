package net.broachcutter.vendorapp.screens.accessories.arbor_extensions.specification

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentAccessoriesArborExtensionsSpecificationBinding
import net.broachcutter.vendorapp.util.NothingSelectedSpinnerAdapter

class ArborExtensionSpecificationFragment : BaseFragment() {

    private lateinit var lengthOfArborExtensionAdapter: ArrayAdapter<String>

    private var _binding: FragmentAccessoriesArborExtensionsSpecificationBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lengthOfArborExtensionAdapter = ArrayAdapter(
            context,
            R.layout.cutter_type_spinner_item,
            R.id.cutterTypeOption,
            listOf("50 mm", "75 mm", "100 mm")
        )
        lengthOfArborExtensionAdapter.setDropDownViewResource(R.layout.cutter_type_spinner_dropdown_item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccessoriesArborExtensionsSpecificationBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCutterTypeSpinner()
    }

    private fun setupCutterTypeSpinner() {
        binding.lengthArborExtSpinner.adapter = NothingSelectedSpinnerAdapter(
            lengthOfArborExtensionAdapter,
            R.layout.cutter_type_spinner_select_one,
            requireContext()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
