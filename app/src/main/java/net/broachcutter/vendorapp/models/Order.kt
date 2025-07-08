package net.broachcutter.vendorapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import net.broachcutter.vendorapp.models.cart.TaxItem
import org.threeten.bp.ZonedDateTime

@Parcelize
@JsonClass(generateAdapter = true)
data class TrackingDetail(
    @SerializedName("courierName")
    @Json(name = "courierName")
    val courierName: String?,
    @SerializedName("trackingId")
    @Json(name = "trackingId")
    val trackingId: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UpdatedOrderItem(
    @SerializedName("item")
    @Json(name = "product")
    val item: Product? = null,
    @SerializedName("itemSubTotal")
    @Json(name = "itemSubTotal")
    val itemSubtotal: Float? = null,
    @SerializedName("totalQuantity")
    @Json(name = "totalQuantity")
    val totalQuantity: Int? = null
) : Parcelable

@Parcelize
@Suppress("MagicNumber")
enum class UpdatedOrderStatus(val position: Int) : Parcelable {
    @Json(name = "PENDING")
    @SerializedName("PENDING")
    PENDING(0),

    @Json(name = "AWAITING_PAYMENT")
    @SerializedName("AWAITING_PAYMENT")
    AWAITING_PAYMENT(1),

    @Json(name = "PROCESSING")
    @SerializedName("PROCESSING")
    PROCESSING(2),

    @Json(name = "AWAITING_DISPATCH")
    @SerializedName("AWAITING_DISPATCH")
    AWAITING_DISPATCH(3),

    @Json(name = "DISPATCHED")
    @SerializedName("DISPATCHED")
    DISPATCHED(4),

    @Json(name = "CANCELLED")
    @SerializedName("CANCELLED")
    CANCELLED(5)
}

@Parcelize
@Entity
@JsonClass(generateAdapter = true)
data class UpdatedOrder(
    @PrimaryKey
    @SerializedName("orderNumber")
    @Json(name = "orderNumber")
    val orderId: String,
    @SerializedName("orderDate")
    @Json(name = "orderDate")
    val orderDate: ZonedDateTime,
    @SerializedName("subtotal")
    @Json(name = "subtotal")
    val subtotal: Double?,
    @SerializedName("orderItems")
    @Json(name = "orderItems")
    val product: List<UpdatedOrderItem>,
    @SerializedName("confirmationDate")
    @Json(name = "confirmationDate")
    val confirmedDate: ZonedDateTime?,
    @SerializedName("status")
    @Json(name = "status")
    val orderStatus: UpdatedOrderStatus?,
    @SerializedName("taxes")
    @Json(name = "taxes")
    val taxItems: List<TaxItem>?,
    @SerializedName("total")
    @Json(name = "total")
    val total: Double?,
    @SerializedName("couponDiscount")
    @Json(name = "couponDiscount")
    val couponDiscount: Double?,
    @SerializedName("dispatchDate")
    @Json(name = "dispatchDate")
    val dispatchDate: ZonedDateTime?,
    @SerializedName("trackingDetails")
    @Json(name = "trackingDetails")
    val trackingDetails: TrackingDetail?,
    @SerializedName("docDueDate")
    @Json(name = "docDueDate")
    val docDueDate: ZonedDateTime?
) : Parcelable

@JsonClass(generateAdapter = true)
data class UpdatedOrderHistory(
    @SerializedName("month")
    val month: Int,
    @SerializedName("year")
    val year: Int,
    @SerializedName("orders")
    var updatedOrder: List<UpdatedOrder>?
) {
    companion object {
        const val TAG = "OrderHistory"
    }
}
