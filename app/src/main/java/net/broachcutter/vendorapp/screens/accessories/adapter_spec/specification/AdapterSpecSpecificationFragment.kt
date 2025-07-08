package net.broachcutter.vendorapp.screens.accessories.adapter_spec.specification

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentAccessoriesAdapterSpecSpecificationBinding
import net.broachcutter.vendorapp.util.NothingSelectedSpinnerAdapter

class AdapterSpecSpecificationFragment : BaseFragment() {
    private lateinit var lengthOfAdapterSpecAdapter: ArrayAdapter<String>
    private var _binding: FragmentAccessoriesAdapterSpecSpecificationBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lengthOfAdapterSpecAdapter = ArrayAdapter(
            context,
            R.layout.cutter_type_spinner_item,
            R.id.cutterTypeOption,
            listOf("Universal", "Non-Universal")
        )
        lengthOfAdapterSpecAdapter.setDropDownViewResource(R.layout.cutter_type_spinner_dropdown_item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentAccessoriesAdapterSpecSpecificationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCutterTypeSpinner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCutterTypeSpinner() {
        binding.machineArborShankSpinner.adapter = NothingSelectedSpinnerAdapter(
            lengthOfAdapterSpecAdapter,
            R.layout.cutter_type_spinner_select_one,
            requireContext()
        )
    }
}
