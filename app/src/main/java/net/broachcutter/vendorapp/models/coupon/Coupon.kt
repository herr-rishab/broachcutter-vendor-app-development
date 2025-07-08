package net.broachcutter.vendorapp.models.coupon

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import org.threeten.bp.ZonedDateTime

@Parcelize
data class Coupon(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("active")
    val active: Boolean = false,
    @SerializedName("couponType", alternate = ["type"])
    val couponType: CouponType?,
    @SerializedName("valid_from", alternate = ["validFrom"])
    var validFrom: ZonedDateTime = ZonedDateTime.now(),
    @SerializedName("valid_till", alternate = ["validTill"])
    var validTill: ZonedDateTime = ZonedDateTime.now(),
    @SerializedName("short_desc", alternate = ["shortDesc"])
    val shortDesc: String = "",
    @SerializedName("till_stocks_last", alternate = ["tillStockLast"])
    val tillStockLast: Boolean = false,
    @SerializedName("x_qty", alternate = ["xQuantity"])
    val xQuantity: Int? = -1,
    @SerializedName("y_qty", alternate = ["yQuantity"])
    val yQuantity: Int? = -1,
    @SerializedName("y_percent_disc", alternate = ["yPercentDiscount"])
    val yPercentDiscount: Int? = -1,
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @SerializedName("percent_disc", alternate = ["percentDisc"])
    val percentDisc: Int? = -1,
    @SerializedName("percent_min_qty", alternate = ["percentMinQty"])
    val percentMinQty: Int = -1,
    @SerializedName("percent_apply_to", alternate = ["percentApplyTo"])
    val percentApplyTo: PercentApplyTo? = PercentApplyTo.INVALID,
    @SerializedName("percentageProduct")
    val percentageProduct: Product? = null,
    @SerializedName("percent_product_type", alternate = ["percentProductType"])
    val percentProductType: ProductType? = null,
    @SerializedName("max_usages", alternate = ["maxUsages"])
    val maxUsages: Int = -1,
    @SerializedName("allow_type_a", alternate = ["allowTypeA"])
    val allowTypeA: Boolean = false,
    @SerializedName("allow_type_b", alternate = ["allowTypeB"])
    val allowTypeB: Boolean = false,
    @SerializedName("notification_title", alternate = ["notificationTitle"])
    val notificationTitle: String = "",
    @SerializedName("current_usage_count")
    val currentUsageCount: Int = -1,
    @SerializedName("xProduct")
    val xProduct: Product? = null,
    @SerializedName("yProduct")
    val yProduct: Product? = null
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Coupon -> {
                this.id == other.id
            }

            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

enum class PercentApplyTo(val code: String) {
    @SerializedName("item")
    ITEM("item"),

    @SerializedName("product_type")
    PRODUCT_TYPE("product_type"),
    INVALID("invalid");
}
