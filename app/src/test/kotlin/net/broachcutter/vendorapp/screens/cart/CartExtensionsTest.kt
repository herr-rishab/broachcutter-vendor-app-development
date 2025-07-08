package net.broachcutter.vendorapp.screens.cart

import net.broachcutter.vendorapp.models.cart.Cart
import net.broachcutter.vendorapp.models.cart.isReadyCart
import org.amshove.kluent.shouldBeFalse
import org.junit.Test

class CartExtensionsTest {

    @Test
    fun isReadyCart() {
        val cart =
            Cart(
                null,
                null,
                null,
                null,
                deliveryAddress = null,
                minRupeeAmountRequired = 0.0
            )
        cart.isReadyCart().shouldBeFalse()
    }
}
