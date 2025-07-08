package net.broachcutter.vendorapp.screens.holesaws.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.product_list.ProductRepository
import javax.inject.Inject

class HolesawsHomeViewModel @Inject constructor(
    productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    val holesawSpares = productRepository.getHolesawSpares()

    fun addToCart(product: Product): LiveData<Resource<Any>> {
        return cartRepository.addToOrUpdateCart(product, 1)
    }
}
