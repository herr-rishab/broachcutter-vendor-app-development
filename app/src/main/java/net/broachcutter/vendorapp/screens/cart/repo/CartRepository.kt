package net.broachcutter.vendorapp.screens.cart.repo

import androidx.lifecycle.LiveData
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.cart.*

interface CartRepository {

    fun getCart(): LiveData<Resource<Cart>>

    fun addToOrUpdateCart(product: Product, quantity: Int): LiveData<Resource<Any>>

    fun removeFromCart(product: Product)

    fun onCartItemUpdate(cartItem: CartItem, newQuantity: Int, newTerms: PaymentTerm)

    fun submitOrder(cartUiModel: CartUiModel, appliedCoupon: String?): LiveData<Resource<PlaceOrderResponse>>

    fun isCartEmpty(): LiveData<Boolean>

    fun clearCart()

    suspend fun fetchFinalCartPrice(totalCart: TotalCart): CartPrice
}
