package net.broachcutter.vendorapp.analytics

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.cart.Cart
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.models.cart.CartUiModel
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse
import ru.terrakok.cicerone.Screen
import javax.inject.Inject

@Suppress("TooManyFunctions")
class FirebaseAnalyticsImpl @Inject constructor(
    firebaseHolder: FirebaseHolder
) : Analytics {

    private val analytics = firebaseHolder.analytics

    companion object {
        private const val INR = "INR"
        private const val LANDING_CLICK = "LANDING_CLICK"
        private const val SECTION_NAME = "section_name"
        private const val MENU_SHOWN = "menu_shown"
        private const val GLOBAL_SEARCH_CLICK = "global_search_click"
        private const val OVERDUE_PAYMENT_SHOWN = "overdue_payment_shown"
        private const val FIRST_LOGIN_SUCCESS = "first_login_success"
        private const val FIRST_LOGIN_PRESET_SUCCESS = "first_login_preset_success"
        private const val FIRST_LOGIN_SET_PASSWORD_SUCCESS = "first_login_set_pswd_success"
        private const val CART_QUANTITY_UPDATE = "cart_quantity_update"
        private const val OLD_QUANTITY = "old_quantity"
        private const val NEW_QUANTITY = "new_quantity"
        private const val CHECKOUT_PAYMENT_TERMS_SELECTED = "checkout_payment_terms_selected"
        private const val CHECKOUT_SUBMIT_CONFIRMED = "checkout_submit_order_confirmed"
    }

    override fun userSessionActive(userId: String) {
        analytics.setUserId(userId)
    }

    override fun landingSectionClick(landingSection: LandingSection) {
        analytics.logEvent(LANDING_CLICK) {
            param(SECTION_NAME, landingSection.name)
        }
    }

    override fun logout() {
        analytics.setUserId(null)
    }

    override fun viewCart(cart: CartUiModel) {
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_CART) {}
        analytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT) {
            param(FirebaseAnalytics.Param.CURRENCY, INR)
            cart.total?.let { param(FirebaseAnalytics.Param.VALUE, it) }
            param(FirebaseAnalytics.Param.ITEMS, createCartBundle(cart))
        }
    }

    override fun addToCart(product: Product, quantity: Int) {
        val addedItem = createCartItem(product, quantity)
        analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
            param(FirebaseAnalytics.Param.CURRENCY, INR)
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(addedItem))
        }
    }

    override fun updateCartQuantity(product: Product, oldQuantity: Int, newQuantity: Int) {
        val bundle = Bundle(createProductBundle(product)).apply {
            putLong(OLD_QUANTITY, oldQuantity.toLong())
            putLong(NEW_QUANTITY, newQuantity.toLong())
        }
        analytics.logEvent(CART_QUANTITY_UPDATE, bundle)
    }

    override fun removeFromCart(cartItem: CartItem) {
        val removedItem = createCartItem(cartItem.product, cartItem.quantity)
        analytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART) {
            param(FirebaseAnalytics.Param.CURRENCY, INR)
            cartItem.unitPrice?.let {
                param(FirebaseAnalytics.Param.VALUE, (cartItem.quantity * it.finalPrice))
            }
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(removedItem))
        }
    }

    override fun checkoutOrderPlaceSuccess(cartModel: CartUiModel, response: PlaceOrderResponse) {
        val confirmationNumbers = StringBuilder()
        response.successfulOrder.forEach {
            confirmationNumbers.append(it.orderNumber).append(" ")
        }
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
            param(FirebaseAnalytics.Param.TRANSACTION_ID, confirmationNumbers.toString())
            param(FirebaseAnalytics.Param.AFFILIATION, "Dealer native android app")
            param(FirebaseAnalytics.Param.CURRENCY, INR)
            param(FirebaseAnalytics.Param.VALUE, cartModel.total!!)
            param(FirebaseAnalytics.Param.TAX, cartModel.total - cartModel.subtotal!!)
            param(FirebaseAnalytics.Param.SHIPPING, 0.0)
            param(FirebaseAnalytics.Param.ITEMS, createCartBundle(cartModel))
        }
    }

    override fun searchPerformed(query: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SEARCH) {
            param(FirebaseAnalytics.Param.SEARCH_TERM, query)
        }
    }

    override fun navigatedTo(activity: Activity, screen: Screen) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen.screenKey)
        }
    }

    override fun firstLoginSuccess() {
        analytics.logEvent(FIRST_LOGIN_SUCCESS) {}
    }

    override fun overduePaymentShown() {
        analytics.logEvent(OVERDUE_PAYMENT_SHOWN) {}
    }

    override fun searchIconClick() {
        analytics.logEvent(GLOBAL_SEARCH_CLICK) {}
    }

    override fun onNavDrawerShown() {
        analytics.logEvent(MENU_SHOWN) {}
    }

    private fun createProductBundle(product: Product): Bundle {
        return Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, product.partNumber)
            putString(FirebaseAnalytics.Param.ITEM_NAME, product.name)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.productType?.name)
        }
    }

    private fun createCartItem(product: Product, quantity: Int): Bundle {
        return Bundle(createProductBundle(product)).apply {
            putLong(FirebaseAnalytics.Param.QUANTITY, quantity.toLong())
        }
    }

    private fun createCartBundle(cart: Cart): Array<Bundle> {
        val cartItemList = mutableListOf<Bundle>()
        cart.cartItems?.forEach {
            val productBundle = createCartItem(it.product, it.quantity)
            cartItemList.add(productBundle)
        }
        return cartItemList.toTypedArray()
    }

    private fun createCartBundle(cart: CartUiModel): Array<Bundle> {
        val cartItemList = mutableListOf<Bundle>()
        cart.cartItems?.forEach {
            val productBundle = createCartItem(it.product, it.quantity)
            cartItemList.add(productBundle)
        }
        return cartItemList.toTypedArray()
    }

    override fun firstLoginPresetPasswordSuccess() {
        analytics.logEvent(FIRST_LOGIN_PRESET_SUCCESS) {}
    }

    override fun firstLoginSetPasswordSuccess() {
        analytics.logEvent(FIRST_LOGIN_SET_PASSWORD_SUCCESS) {}
    }

    override fun checkoutAllPaymentTermsSelected() {
        analytics.logEvent(CHECKOUT_PAYMENT_TERMS_SELECTED) {}
    }

    override fun checkoutSubmitOrderConfirmed() {
        analytics.logEvent(CHECKOUT_SUBMIT_CONFIRMED) {}
    }
}
