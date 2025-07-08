package net.broachcutter.vendorapp.screens.pilot_pins.specifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.valartech.commons.utils.extensions.hideKeyboard
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentPilotPinsSpecificationsBinding
import net.broachcutter.vendorapp.views.FlatRadioButtonGroup

class PilotPinsSpecificationFragment : BaseVMFragment<PilotPinsSpecificationsViewModel>() {

    override val vmClassToken: Class<PilotPinsSpecificationsViewModel>
        get() = PilotPinsSpecificationsViewModel::class.java

    private var _binding: FragmentPilotPinsSpecificationsBinding? = null
    private val binding get() = _binding!!

    private var selectedDiameter: Float? = null

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
        _binding = FragmentPilotPinsSpecificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pilotPinSearch.setOnClickListener {
            activity?.hideKeyboard()
            viewModel.search(
                selectedDiameter,
                binding.lengthPin.text.toString().toIntOrNull()
            )
        }

        binding.diameterPinGroup.buttonSelectListener =
            object : FlatRadioButtonGroup.ButtonSelectListener {
                override fun onButtonSelect(buttonId: Int?) {
                    val buttonText = binding.diameterPinGroup.selectedChild?.text.toString()
                    selectedDiameter =
                        buttonText.substring(0, buttonText.length - 2).toFloatOrNull()
                }
            }

        binding.diameterPinGroup.selectChildWithTextLike(selectedDiameter?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
