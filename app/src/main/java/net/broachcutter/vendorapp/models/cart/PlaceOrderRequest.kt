package net.broachcutter.vendorapp.models.cart

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class PlaceOrderRequest(
    @Json(name = "cartItems")
    @SerializedName("cartItems")
    val cartItems: List<SimplePricedCartItem>,
    @SerializedName("billingAddress")
    val billingAddress: String,
    @SerializedName("deliveryAddress")
    val deliveryAddress: String,
    @SerializedName("appliedCoupon")
    val appliedCoupon: String?
)
