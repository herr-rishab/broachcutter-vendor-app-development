package net.broachcutter.vendorapp.screens.accessories.commons

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import com.valartech.loadinglayout.LoadingLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentAccessoriesCommonBinding
import net.broachcutter.vendorapp.models.AccessoriesCommonItem
import net.broachcutter.vendorapp.models.AccessoriesModelWiseItem
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.screens.accessories.AccessoriesViewModel
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.attachObserver
import javax.inject.Inject

class AccessoriesCommonFragment : BaseFragment(), AccessoriesModelWiseItem.ClickListener {

    private var commonAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private var cubAndCubAutoAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private var superAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private var tridentAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private var titanAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private val adapterList =
        listOf(commonAdapter, cubAndCubAutoAdapter, superAdapter, tridentAdapter, titanAdapter)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AccessoriesViewModel>
    val model: AccessoriesViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AccessoriesViewModel::class.java)
    }

    private var _binding: FragmentAccessoriesCommonBinding? = null
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
        _binding = FragmentAccessoriesCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.accessoriesCommonRecyclerView.adapter = commonAdapter
        binding.accessoriesCommonRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.cubAndCubAutoAccessoriesCommonRecyclerView.adapter = cubAndCubAutoAdapter
        binding.cubAndCubAutoAccessoriesCommonRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.superAccessoriesCommonRecyclerView.adapter = superAdapter
        binding.superAccessoriesCommonRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.tridentAccessoriesCommonRecyclerView.adapter = tridentAdapter
        binding.tridentAccessoriesCommonRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.titanAccessoriesCommonRecyclerView.adapter = titanAdapter
        binding.titanAccessoriesCommonRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // setup click listener for items on all the adapters to show bottom sheets
        adapterList.forEach {
            it.setOnItemClickListener { item, _ ->
                val product = if (item is AccessoriesModelWiseItem) {
                    item.product
                } else {
                    (item as AccessoriesCommonItem).product
                }
                val bottomSheetFragment =
                    AccessoriesCommonProductBottomSheetDialog.newInstance(product) { clickedProduct, qty ->
                        addToCart(clickedProduct, qty)
                    }
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }
        }

        setupObserver()
        model.getAccessories()
    }

    @Suppress("LongMethod")
    private fun setupObserver() {
        model.getCubAccessories.liveData.attachObserver {
            setLifecycleOwner(viewLifecycleOwner)
            setLoadingLayout(binding.cubAndCubAutoAccessoriesCommonRecyclerViewLoadingLoayout)
            setErrorState(LoadingLayout.COMPLETE)
            onSuccess {
                val response = it.data
                if (response != null) {
                    response.results?.forEach { product ->
                        cubAndCubAutoAdapter.add(
                            AccessoriesModelWiseItem(
                                requireContext(),
                                product,
                                this@AccessoriesCommonFragment
                            )
                        )
                    }
                } else {
                    longToast(it.message)
                }
            }
            onError {
                longToast(it.message)
            }
            observe(true)
        }

        model.getSuperAccessories.liveData.attachObserver {
            setLifecycleOwner(viewLifecycleOwner)
            setLoadingLayout(binding.superAccessoriesCommonRecyclerViewLoadingLoayout)
            setErrorState(LoadingLayout.COMPLETE)
            onSuccess {
                val response = it.data
                if (response != null) {
                    response.results?.forEach { product ->
                        superAdapter.add(
                            AccessoriesModelWiseItem(
                                requireContext(),
                                product,
                                this@AccessoriesCommonFragment
                            )
                        )
                    }
                } else {
                    longToast(it.message)
                }
            }
            onError {
                longToast(it.message)
            }
            observe(true)
        }

        model.getTridentAccessories.liveData.attachObserver {
            setLifecycleOwner(viewLifecycleOwner)
            setLoadingLayout(binding.tridentAccessoriesCommonRecyclerViewLoadingLoayout)
            setErrorState(LoadingLayout.COMPLETE)
            onSuccess {
                val response = it.data
                if (response != null) {
                    response.results?.forEach { product ->
                        tridentAdapter.add(
                            AccessoriesModelWiseItem(
                                requireContext(),
                                product,
                                this@AccessoriesCommonFragment
                            )
                        )
                    }
                } else {
                    longToast(it.message)
                }
            }
            onError {
                longToast(it.message)
            }
            observe(true)
        }

        model.getTitanAccessories.liveData.attachObserver {
            setLifecycleOwner(viewLifecycleOwner)
            setLoadingLayout(binding.titanAccessoriesCommonRecyclerViewContainer)
            setErrorState(LoadingLayout.COMPLETE)
            onSuccess {
                val response = it.data
                if (response != null) {
                    response.results?.forEach { product ->
                        titanAdapter.add(
                            AccessoriesModelWiseItem(
                                requireContext(),
                                product,
                                this@AccessoriesCommonFragment
                            )
                        )
                    }
                } else {
                    longToast(it.message)
                }
            }
            onError {
                longToast(it.message)
            }
            observe(true)
        }
        model.getGeneralAccessories.liveData.attachObserver {
            setLifecycleOwner(viewLifecycleOwner)
            setLoadingLayout(binding.accessoriesCommonRecyclerViewLoadingLoayout)
            setErrorState(LoadingLayout.COMPLETE)
            onSuccess { resource ->
                val response = resource.data
                if (response != null) {
                    response.results?.forEach { product ->
                        commonAdapter.add(
                            AccessoriesCommonItem(requireContext(), product) {
                                addToCart(it)
                            }
                        )
                    }
                } else {
                    longToast(resource.message)
                }
            }
            onError {
                longToast(it.message)
            }
            observe(true)
        }
    }

    override fun onAddSingleQuantityToCart(product: Product) {
        addToCart(product)
    }

    private fun addToCart(product: Product, qty: Int = 1) {
        model.addToCart(product, qty).attachObserver {
            setLifecycleOwner(viewLifecycleOwner)
            setErrorState(LoadingLayout.COMPLETE)
            onSuccess {
                toast(R.string.added_to_cart)
            }
            onError {
                longToast(it.message)
            }
            observe(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
