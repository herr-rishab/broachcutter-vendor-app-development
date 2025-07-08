package net.broachcutter.vendorapp.analytics

import android.app.Activity
import androidx.annotation.MainThread
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.models.cart.CartUiModel
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse
import ru.terrakok.cicerone.Screen

@Suppress("TooManyFunctions")
interface Analytics {

    fun firstLoginPresetPasswordSuccess()

    fun firstLoginSetPasswordSuccess()

    fun firstLoginSuccess()

    fun userSessionActive(userId: String)

    fun landingSectionClick(landingSection: LandingSection)

    fun logout()

    fun viewCart(cart: CartUiModel)

    fun addToCart(product: Product, quantity: Int)

    /**
     * When updating to non-zero quantity.
     */
    fun updateCartQuantity(product: Product, oldQuantity: Int, newQuantity: Int)

    /**
     * When quantity is set to 0.
     */
    fun removeFromCart(cartItem: CartItem)

    fun checkoutAllPaymentTermsSelected()

    fun checkoutSubmitOrderConfirmed()

    fun checkoutOrderPlaceSuccess(cartModel: CartUiModel, response: PlaceOrderResponse)

    fun overduePaymentShown()

    fun searchIconClick()

    fun searchPerformed(query: String)

    fun onNavDrawerShown()

    @MainThread
    fun navigatedTo(activity: Activity, screen: Screen)
}

enum class LandingSection {
    Cutters,
    Machines,
    Accessories,
    Spares,
    SolidDrills,
    Holesaws,
    OrderHistory,
    OfferScheme
}
