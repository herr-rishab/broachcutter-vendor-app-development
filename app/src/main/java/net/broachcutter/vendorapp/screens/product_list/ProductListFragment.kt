package net.broachcutter.vendorapp.screens.product_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.valartech.commons.network.google.Resource
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import com.valartech.loadinglayout.LoadingLayout
import com.valartech.loadinglayout.LoadingLayout.Companion.COMPLETE
import com.valartech.loadinglayout.LoadingLayout.Companion.LOADING
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentProductListBinding
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.TITLE
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.setSafeOnClickListener
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class ProductListFragment : BaseFragment(), ProductListAdapter.ProductInteractor {

    companion object {
        /**
         * This bundle needs to contain [ListType]!
         */
        fun newInstance(args: Bundle): ProductListFragment {
            val fragment = ProductListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var results: LiveData<Resource<SearchResults>>

    lateinit var adapter: ProductListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<ProductListViewModel>

    private val model: ProductListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ProductListViewModel::class.java]
    }

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
        arguments?.let { model.init(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductListAdapter(this)
        binding.recyclerView.adapter = adapter

        binding.resultsTitle.text = arguments?.getString(TITLE)

        model.results.observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    // stop loading animations
                    binding.loadingLayout.setState(COMPLETE)
                    if (binding.swipeRefreshLayout.isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    // set results
                    resource.data?.results?.let {
                        adapter.productList = it
                        if (it.isEmpty()) {
                            binding.loadingLayout.setState(LoadingLayout.EMPTY)
                        }
                    }
                }

                Status.ERROR -> {
                    // stop loading animations
                    binding.loadingLayout.setState(COMPLETE)
                    if (binding.swipeRefreshLayout.isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    // show error
                    resource.message?.let { longToast(it) }
                }

                Status.LOADING -> {
                    if (binding.swipeRefreshLayout.isRefreshing) {
                        // set results if any
                        resource.data?.results?.let {
                            val productList = it as ArrayList<Product>
                            adapter.productList = productList
                            if (productList.isEmpty()) {
                                binding.loadingLayout.setState(LoadingLayout.EMPTY)
                            }
                        }
                    } else {
                        binding.loadingLayout.setState(LOADING)
                    }
                }

                else -> {}
            }
        }
        setupSwipeLayout()

        binding.noSearchResults.btnSearchBack.setSafeOnClickListener {
            model.goBack()
        }
    }

    private fun setupSwipeLayout() {
        val red = ContextCompat.getColor(requireContext(), R.color.tomato)
        val blue = ContextCompat.getColor(requireContext(), R.color.nice_blue)
        binding.swipeRefreshLayout.setColorSchemeColors(red, blue)
        binding.swipeRefreshLayout.setOnRefreshListener {
            model.refreshResults()
        }
    }

    override fun addToCart(product: Product, quantity: Int) {
        val addToCartResult = model.addToCart(product, quantity)
        addToCartResult.observe(
            viewLifecycleOwner
        ) {
            when (it?.status) {
                Status.SUCCESS -> {
                    binding.loadingLayout.setState(COMPLETE)
                    toast(getString(R.string.added_to_cart))
                    Timber.i("Added to cart")
                }

                Status.ERROR -> {
                    binding.loadingLayout.setState(COMPLETE)
                    longToast(getString(R.string.error_adding_cart, it.message))
                }

                Status.LOADING -> binding.loadingLayout.setState(LOADING)
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
