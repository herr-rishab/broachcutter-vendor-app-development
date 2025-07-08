package net.broachcutter.vendorapp.models.cart

import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.models.coupon.CouponType
import net.broachcutter.vendorapp.models.coupon.PercentApplyTo
import net.broachcutter.vendorapp.screens.cart.areReadyCartItems

data class CartUiModel(
    val cartItems: List<CartItem>?,
    val taxItems: List<TaxItem>?,
    val subtotal: Double?,
    val total: Double?,
    var exceededCredit: Double,
    var overduePayment: Boolean,
    var minRupeeAmountRequired: Double,
    val isCheckoutEnabled: Boolean,
    val checkoutDisabledReason: CartDisabledReason,
    val cartPrice: CartPrice?
) {
    fun printCartInfo(): StringBuilder {
        val stringBuilder = StringBuilder()
        cartItems?.forEach { item ->
            stringBuilder.append('\n')
            stringBuilder.append("**** Item ****")
            stringBuilder.append('\n')
            stringBuilder.append("PartNumber => ${item.partNumber}")
            stringBuilder.append('\n')
            stringBuilder.append("PaymentTerm => ${item.selectedPaymentTerm}")
            stringBuilder.append('\n')
            stringBuilder.append("Quantity => ${item.quantity}")
            stringBuilder.append('\n')
            stringBuilder.append("Product Type => ${item.product.productType}")
            stringBuilder.append('\n')
            stringBuilder.append("Price => ${item.unitPrice}")
        }
        return stringBuilder
    }

    fun isReadyCart(): Boolean {
        cartItems?.forEach {
            if (it.selectedPaymentTerm == null || it.unitPrice == null) {
                return false
            }
        }
        return true
    }
}

fun Resource<Cart>.convertToUiModel(coupon: Coupon?): Resource<CartUiModel> {
    val cartData = this.data
    val checkoutDisabledReason =
        cartData?.let { getCheckoutAllowed(it, coupon) } ?: CartDisabledReason.NO_CART_ITEMS
    val uiModel = CartUiModel(
        cartItems = cartData?.cartItems,
        taxItems = cartData?.taxItems,
        subtotal = cartData?.subtotal,
        total = cartData?.total,
        exceededCredit = cartData?.exceededCredit ?: 0.0,
        overduePayment = cartData?.overduePayment ?: false,
        minRupeeAmountRequired = cartData?.minRupeeAmountRequired ?: 0.0,
        isCheckoutEnabled = checkoutDisabledReason == CartDisabledReason.NONE,
        checkoutDisabledReason = checkoutDisabledReason,
        cartPrice = cartData?.cartPrice
    )
    return Resource(this.status, uiModel, this.message)
}

@Suppress("ComplexMethod", "NestedBlockDepth")
private fun getCheckoutAllowed(cart: Cart, coupon: Coupon?): CartDisabledReason {
    var cartDisabledReason = CartDisabledReason.NONE
    if (cart.isEmptyCart()) {
        cartDisabledReason = CartDisabledReason.NO_CART_ITEMS
    } else if (cart.overduePayment != false) {
        cartDisabledReason = CartDisabledReason.OVERDUE_PAYMENT
    } else if (cart.exceededCredit?.let { it > 0 } != false) {
        cartDisabledReason = CartDisabledReason.CREDIT_EXCEEDED
    } else if (cart.minBalanceCheckNeeded() && cart.minRupeeAmountRequired != 0.0) {
        cartDisabledReason = CartDisabledReason.MIN_BALANCE
    } else if (!cart.isReadyCart()) {
        cartDisabledReason = CartDisabledReason.PAYMENT_TERMS_NOT_SELECTED
    } else if (cart.cartPrice == null) {
        cartDisabledReason = CartDisabledReason.NO_CART_PRICE_CALCULATED
    } else if (cart.cartItems?.isNotEmpty() == true && cart.cartPrice?.couponDiscount != 0.0) {
        cart.cartItems.forEach {
            if (it.selectedPaymentTerm?.id?.startsWith("P00") == false) {
                if (coupon?.couponType == CouponType.PERCENTAGE) {
                    if (coupon.percentApplyTo == PercentApplyTo.ITEM &&
                        coupon.percentageProduct?.partNumber == it.partNumber
                    ) {
                        cartDisabledReason = CartDisabledReason.NON_CREDIT_PAYMENT_TERM
                    } else if (coupon.percentApplyTo == PercentApplyTo.PRODUCT_TYPE &&
                        coupon.percentageProduct?.productType == it.product.productType
                    ) {
                        cartDisabledReason = CartDisabledReason.NON_CREDIT_PAYMENT_TERM
                    }
                } else {
                    if (it.partNumber == coupon?.xProduct?.partNumber ||
                        it.partNumber == coupon?.yProduct?.partNumber
                    ) {
                        cartDisabledReason = CartDisabledReason.NON_CREDIT_PAYMENT_TERM
                    }
                }
            }
        }
    } else {
        cartDisabledReason = CartDisabledReason.NONE
    }
    return cartDisabledReason
}

enum class CartDisabledReason {
    NONE,
    CREDIT_EXCEEDED,
    OVERDUE_PAYMENT,
    MIN_BALANCE,
    PAYMENT_TERMS_NOT_SELECTED,
    NO_CART_ITEMS,
    NO_CART_PRICE_CALCULATED,
    NON_CREDIT_PAYMENT_TERM
}

/**
 * Checks if all cart items have their payment terms selected.
 */
fun Cart.isReadyCart(): Boolean = this.cartItems?.areReadyCartItems() ?: false

fun Cart.isEmptyCart(): Boolean = this.cartItems?.isEmpty() ?: true
