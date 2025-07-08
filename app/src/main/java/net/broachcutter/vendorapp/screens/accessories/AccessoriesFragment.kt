package net.broachcutter.vendorapp.screens.accessories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentAccessories2Binding
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.util.ViewModelFactory
import javax.inject.Inject

/**
 *
 */
class AccessoriesFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AccessoriesViewModel>

    private val model: AccessoriesViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AccessoriesViewModel::class.java)
    }
    private var _binding: FragmentAccessories2Binding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccessories2Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commonModelWiseLayout.setOnClickListener { model.onCommonAccessoriesClick() }
        binding.pilotPinsLayout.setOnClickListener { model.onPilotPinsClick() }
        binding.arborsExtensionsLayout.setOnClickListener {
            model.getAccessories(
                listType = ListType.ACCESSORIES_ARBORS_EXTENSIONS,
                productType = ProductType.ARBOR_EXTENSIONS
            )
        }
        binding.shankAdaptersLayout.setOnClickListener {
            model.getAccessories(
                listType = ListType.ACCESSORIES_ADAPTORS,
                productType = ProductType.ADAPTOR
            )
        }
        binding.radialDrillingArborLayout.setOnClickListener { model.onArborRadialSpecClick() }
    }
}
