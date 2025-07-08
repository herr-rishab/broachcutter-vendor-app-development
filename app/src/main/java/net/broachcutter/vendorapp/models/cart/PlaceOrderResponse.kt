package net.broachcutter.vendorapp.models.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime

@Parcelize
data class PlaceOrderResponse(
    @SerializedName("failed")
    val failedOrder: FailedOrder,
    @SerializedName("successful")
    val successfulOrder: List<SuccessfulOrder>
) : Parcelable {
    @Parcelize
    data class FailedOrder(
        @SerializedName("items")
        val items: List<Items>,
        @SerializedName("message")
        val message: String
    ) : Parcelable {
        @Parcelize
        data class Items(
            @SerializedName("cartItem")
            val cartItem: SimplePricedCartItem,
        ) : Parcelable
    }

    @Parcelize
    data class SuccessfulOrder(
        @SerializedName("orderDate")
        val orderDate: ZonedDateTime,
        @SerializedName("orderItems")
        val orderItems: List<OrderItem>,
        @SerializedName("orderNumber")
        val orderNumber: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("subtotal")
        val subtotal: Double,
        @SerializedName("taxes")
        val taxes: List<Taxes>,
        @SerializedName("total")
        val total: Double
    ) : Parcelable {
        @Parcelize
        data class OrderItem(
            @SerializedName("item")
            val item: Item,
            @SerializedName("itemSubTotal")
            val itemSubTotal: Double,
            @SerializedName("totalQuantity")
            val totalQuantity: Int
        ) : Parcelable {
            @Parcelize
            data class Item(
                @SerializedName("Name")
                val name: String,
                @SerializedName("PartNumber")
                val partNumber: String
            ) : Parcelable
        }

        @Parcelize
        data class Taxes(
            @SerializedName("amount")
            val amount: Double,
            @SerializedName("percentage")
            val percentage: Double,
            @SerializedName("type")
            val type: String
        ) : Parcelable
    }
}
