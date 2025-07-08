package net.broachcutter.vendorapp.screens.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.hideKeyboard
import com.valartech.commons.utils.extensions.longToast
import com.valartech.loadinglayout.LoadingLayout.Companion.COMPLETE
import com.valartech.loadinglayout.LoadingLayout.Companion.EMPTY
import com.valartech.loadinglayout.LoadingLayout.Companion.LOADING
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.databinding.ActivitySearchBinding
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.util.ViewModelFactory
import org.jetbrains.anko.textColor
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import timber.log.Timber
import javax.inject.Inject

class SearchActivity : BaseActivity(), ResultItem.ClickListener, MoreResultsItem.ClickListener {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SearchViewModel>

    val adapter = GroupieAdapter()

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getApplicationComponent().inject(this)

        val searchText: TextView =
            binding.searchView.findViewById(R.id.search_src_text)
        searchText.typeface = ResourcesCompat.getFont(this, R.font.proxima_nova_alt_regular)
        searchText.textColor = ContextCompat.getColor(this, R.color.black)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // do search
                Timber.i("Query: $query")
                viewModel.setQuery(query)
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // TODO check if this still works
//        binding.btnSearchBack.setSafeOnClickListener {
//            router.exit()
//        }
        binding.searchView.requestFocus()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.results.observe(
            this
        ) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    binding.loadingLayout.setState(COMPLETE)
                    updateSearchResults(resource.data?.results)
                }

                Status.ERROR -> {
                    binding.loadingLayout.setState(COMPLETE)
                    resource.message?.let { longToast(it) }
                }

                Status.LOADING -> binding.loadingLayout.setState(LOADING)
                else -> binding.loadingLayout.setState(COMPLETE)
            }
        }
    }

    private fun updateSearchResults(results: List<Product>?) {
        adapter.clear()
        if (!results.isNullOrEmpty()) {
            adapter.add(TitleItem())

            for (groupType in ProductType.getGroups()) {
                // check if results contain the product type, for each product type
                if (results.any { result -> result.productType.group == groupType }) {
                    // make a new section
                    val section = Section()
                    // add a header
                    section.setHeader(SectionHeaderItem(this, groupType))
                    var resultCounter = 0
                    for (product in results) {
                        if (product.productType.group == groupType) {
                            resultCounter++
                            if (resultCounter > MAX_ITEMS_PER_SECTION) {
                                break
                            }
                            section.add(ResultItem(product, this))
                        }
                    }
                    // add "more items" if we have
                    if (resultCounter > MAX_ITEMS_PER_SECTION) {
                        section.setFooter(MoreResultsItem(this, groupType, this))
                    }
                    adapter.add(section)
                }
            }
        } else {
            binding.loadingLayout.setState(EMPTY)
        }
    }

    override fun onProductClick(product: Product) {
        val dialog = ProductDetailDialogFragment.newInstance(product)
        dialog.show(supportFragmentManager, ProductDetailDialogFragment.TAG)
    }

    override fun onShowMoreResults(productType: ProductType) {
        viewModel.showMoreResults(productType)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search)?.actionView as SearchView
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        return true
    }

    override val navigator: Navigator = object : SupportAppNavigator(this, 0) {}

    companion object {
        const val MAX_ITEMS_PER_SECTION = 4
    }
}
