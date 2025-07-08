package net.broachcutter.vendorapp.models.coupon

import com.google.gson.annotations.SerializedName

enum class CouponType(val code: String) {
    @SerializedName("percentage")
    PERCENTAGE("percentage"),
    @SerializedName("buy_x_get_y")
    BUYXGETY("buy_x_get_y"),
    INVALID("invalid");
    companion object {
        fun fromShortName(code: String): CouponType {
            return when (code) {
                PERCENTAGE.code -> PERCENTAGE
                BUYXGETY.code -> BUYXGETY
                else -> INVALID
            }
        }
    }
}
