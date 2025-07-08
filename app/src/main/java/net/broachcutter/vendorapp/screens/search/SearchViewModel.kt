package net.broachcutter.vendorapp.screens.search

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.valartech.commons.aac.AbsentLiveData
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs
import net.broachcutter.vendorapp.screens.product_list.ProductRepository
import ru.terrakok.cicerone.Router
import java.util.Locale
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val router: Router,
    private val context: Application,
    private val analytics: Analytics
) :
    ViewModel() {

    private val query = MutableLiveData<String>()

    val results: LiveData<Resource<SearchResults>> = query.switchMap { query ->
        if (query.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            productRepository.genericSearch(query)
        }
    }

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == query.value) {
            return
        }
        query.value = input
        analytics.searchPerformed(input)
    }

    fun showMoreResults(productType: ProductType) {
        val title = query.value + " - " + context.getString(productType.nameRes)
        // prepare args to pass on
        val args = bundleOf(
            ProductListArgs.LIST_TYPE to ListType.SEARCH_DETAILS,
            ProductListArgs.TITLE to title,
            ProductListArgs.QUERY to query.value,
            ProductListArgs.PRODUCT_TYPE to productType
        )
        // navigate to next screen
//        router.newChain(Screens.Main(), Screens.ProductResultList(args))
        router.navigateTo(Screens.MainTransition(args))
    }
}
