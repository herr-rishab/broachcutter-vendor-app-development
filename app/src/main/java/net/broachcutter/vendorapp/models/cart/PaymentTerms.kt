package net.broachcutter.vendorapp.models.cart

import com.google.gson.annotations.SerializedName

data class PaymentTermsRequest(val cartItems: List<SimpleCartItem>) {

    constructor(cart: Cart) : this(
        getCartItemsFromCart(
            cart
        )
    )

    companion object {
        fun getCartItemsFromCart(cart: Cart): List<SimpleCartItem> {
            val simpleCartItems = ArrayList<SimpleCartItem>()
            cart.cartItems?.forEach {
                simpleCartItems.add(SimpleCartItem(it))
            }
            return simpleCartItems
        }
    }
}

data class SimpleCartItem(
    @SerializedName("PartNumber")
    val partNumber: String,
    @SerializedName("quantity")
    val quantity: Int
) {
    constructor(cartItem: CartItem) : this(cartItem.partNumber, cartItem.quantity)
}

data class PaymentTermsResponse(
    @SerializedName("ItemTerms")
    val itemTerms: List<ItemTerm>
)

data class ItemTerm(
    @SerializedName("PartNumber")
    val partNumber: String,
    @SerializedName("Terms")
    val terms: List<PaymentTerm>
)
