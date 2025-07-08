package net.broachcutter.vendorapp.screens.cart

import net.broachcutter.vendorapp.models.cart.CartItem

/**
 * Checks if all cart items have their payment terms selected.
 */
fun List<CartItem>.areReadyCartItems(): Boolean {
    var readyItems = 0
    this.forEach {
        if (it.selectedPaymentTerm != null) {
            readyItems++
        }
    }
    return this.size == readyItems && this.isNotEmpty()
}
