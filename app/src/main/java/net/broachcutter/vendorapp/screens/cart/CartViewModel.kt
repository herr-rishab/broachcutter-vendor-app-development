@file:Suppress("TooManyFunctions")

package net.broachcutter.vendorapp.screens.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.valartech.commons.aac.SingleLiveEvent
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.launch
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.cart.Cart
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.models.cart.CartLineItem
import net.broachcutter.vendorapp.models.cart.CartUiModel
import net.broachcutter.vendorapp.models.cart.PaymentTerm
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse
import net.broachcutter.vendorapp.models.cart.TotalCart
import net.broachcutter.vendorapp.models.cart.convertToUiModel
import net.broachcutter.vendorapp.models.cart.isReadyCart
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.coupon.repo.CouponRepository
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class CartViewModel @Inject constructor(
    private val router: Router,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository
) : ViewModel() {

    private var deliveryAddress: String = ""

    private var cart: Cart? = null

    val cartUiModel: LiveData<Resource<CartUiModel>> =
        cartRepository.getCart().map { resource ->
            deliveryAddress = resource.data?.deliveryAddress ?: ""
            resource.data?.let {
                val isCartReady = it.isReadyCart()
                // TODO fix this workaround, this gets called 3-4 times with the same data, diff statuses
                if (isCartReady && this.cart !== it) {
                    this.cart = it
                    fetchFinalCartPrice(it)
                }
            }
            resource.convertToUiModel(null)
        }

    private val _showDeliveryAddress = SingleLiveEvent<String>()

    val showDeliveryAddress: LiveData<String>
        get() = _showDeliveryAddress

    private val _cartLineItem = SingleLiveEvent<Resource<Cart>>()

    val cartLineItem: LiveData<Resource<Cart>>
        get() = _cartLineItem

    fun onCartItemUpdate(cartItem: CartItem, newQuantity: Int, newTerms: PaymentTerm) {
        return cartRepository.onCartItemUpdate(cartItem, newQuantity, newTerms)
    }

    fun onDelete(cartItem: CartItem) {
        cartRepository.removeFromCart(cartItem.product)
    }

    fun onPaymentOverdue() = router.exit()

    fun submitOrder(
        cartUiModel: CartUiModel,
        appliedCoupon: Coupon?
    ): LiveData<Resource<PlaceOrderResponse>> {
        return cartRepository.submitOrder(cartUiModel, appliedCoupon?.id)
    }

    fun onSubmitOrderSuccess(orderResponse: PlaceOrderResponse) {
        Timber.i("onSubmitOrderSuccess")
        navigateAfterSubmission(orderResponse)
    }

    private fun navigateAfterSubmission(orderResponse: PlaceOrderResponse) {
        cartRepository.clearCart()
        router.newRootChain(Screens.Home(), Screens.CheckoutOrderConfirmation(orderResponse))
    }

    fun goBack() {
        router.exit()
    }

    fun updateAllPaymentTerm(
        cartItems: List<CartItem>?,
        cartItem: CartItem,
        newTerms: PaymentTerm,
    ): Int {
        var noOfSameProductType = 0
        cartItems?.let {
            for (cart in it) {
                if (cart.product.productType
                    != ProductType.MACHINE &&
                    cart.product.productType == cartItem.product.productType &&
                    cart.selectedPaymentTerm == null &&
                    cart.selectedPaymentTerm?.id != newTerms.id
                ) {
                    noOfSameProductType++
                }
            }
        }
        return noOfSameProductType
    }

    fun onDeliveryAddressClick() {
        _showDeliveryAddress.value = deliveryAddress
    }

    fun getCouponListFromPref(): List<Coupon> {
        return couponRepository.getCouponListFromPref()
    }

    fun getAppliedCouponFromPref(): Coupon? {
        return couponRepository.getAppliedCouponFromPref()
    }

    fun clearAppliedCoupon() {
        couponRepository.clearCouponFromPref()
    }

    fun offersAvailableCLicked(couponList: List<Coupon>, fragmentName: String) {
        router.navigateTo(Screens.CouponListScreen(couponList, fragmentName))
    }

    private fun fetchFinalCartPrice(
        cartItems: Cart,
    ) {
        _cartLineItem?.postValue(Resource.loading())
        viewModelScope.launch {
            try {
                val cartLineItems = ArrayList<CartLineItem>()
                val appliedCoupon: Coupon? = couponRepository.getAppliedCouponFromPref()
                cartItems.cartItems?.forEach { cartItem ->
                    cartLineItems.add(
                        CartLineItem(
                            cartItem.quantity, cartItem.partNumber,
                            cartItem.selectedPaymentTerm?.id ?: "",
                            cartItem.product.productType ?: ProductType.INVALID
                        )
                    )
                }
                val totalCart = TotalCart(cartLineItems, appliedCoupon?.id)
                val finalCartPrice = cartRepository.fetchFinalCartPrice(totalCart)
                cartItems.cartPrice = finalCartPrice
                _cartLineItem.postValue(Resource.success(cartItems))
            } catch (appException: AppException) {
                Timber.e(appException)
                _cartLineItem.postValue(Resource.error(appException.message))
            }
        }
    }
}
