package net.broachcutter.vendorapp.screens.holesaws.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.valartech.commons.network.google.Status.*
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import com.valartech.loadinglayout.LoadingLayout
import com.valartech.loadinglayout.LoadingLayout.Companion.COMPLETE
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentHolesawsHomeBinding
import net.broachcutter.vendorapp.models.AccessoriesModelWiseItem
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.attachObserver
import javax.inject.Inject

class HolesawsHomeFragment : BaseFragment(), AccessoriesModelWiseItem.ClickListener {

    private lateinit var tabAdapter: HolesawsTabAdapter

    private val sparesAdapter = GroupAdapter<GroupieViewHolder>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HolesawsHomeViewModel>
    private val model: HolesawsHomeViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(HolesawsHomeViewModel::class.java)
    }

    private var _binding: FragmentHolesawsHomeBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
        tabAdapter = HolesawsTabAdapter(
            childFragmentManager,
            context
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHolesawsHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = tabAdapter
        binding.slidingTabs.setupWithViewPager(binding.viewPager)

        binding.holesawSparesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.holesawSparesRecyclerView.adapter = sparesAdapter

        model.holesawSpares.observe(
            viewLifecycleOwner,
            Observer { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        binding.holesawSpareLoadingLayout.setState(COMPLETE)
                        sparesAdapter.clear()
                        resource.data?.results?.forEach {
                            sparesAdapter.add(AccessoriesModelWiseItem(requireContext(), it, this@HolesawsHomeFragment))
                        }
                    }
                    ERROR -> {
                        binding.holesawSpareLoadingLayout.setState(COMPLETE)
                        longToast(resource.message)
                    }
                    LOADING -> binding.holesawSpareLoadingLayout.setState(LoadingLayout.LOADING)
                }
            }
        )
    }

    override fun onAddSingleQuantityToCart(product: Product) {
        model.addToCart(product).attachObserver {
            setLifecycleOwner(viewLifecycleOwner)
            setErrorState(COMPLETE)
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
