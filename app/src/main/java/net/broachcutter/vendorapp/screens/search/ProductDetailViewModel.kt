package net.broachcutter.vendorapp.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import javax.inject.Inject

class ProductDetailViewModel @Inject constructor(
    val cartRepository: CartRepository
) : ViewModel() {

    fun addToCart(product: Product, quantity: Int): LiveData<Resource<Any>> {
        return cartRepository.addToOrUpdateCart(product, quantity)
    }
}
