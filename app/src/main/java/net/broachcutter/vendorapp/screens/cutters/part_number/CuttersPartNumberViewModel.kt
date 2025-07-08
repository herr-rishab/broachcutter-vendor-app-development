package net.broachcutter.vendorapp.screens.cutters.part_number

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.valartech.commons.aac.AbsentLiveData
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.product_list.ProductRepository
import java.util.Locale
import javax.inject.Inject

class CuttersPartNumberViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val query = MutableLiveData<String>()

    val results: LiveData<Resource<SearchResults>> = query.switchMap { query ->
        if (query.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            productRepository.searchCutters(query)
        }
    }

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == query.value) {
            return
        }
        query.value = input
    }

    fun addToCart(product: Product, selectedQuantity: Int): LiveData<Resource<Any>> {
        return cartRepository.addToOrUpdateCart(product, selectedQuantity)
    }
}
