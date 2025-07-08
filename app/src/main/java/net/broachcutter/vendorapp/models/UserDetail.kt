package net.broachcutter.vendorapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import net.broachcutter.vendorapp.network.DataResponse

@JsonClass(generateAdapter = true)
@Parcelize
data class UserDetail(
    @SerializedName("uid")
    val userId: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("credit")
    val credit: Credit,
    @SerializedName("addresses")
    val addresses: List<String>?,
    @SerializedName("paymentOverdue")
    val paymentOverdue: Boolean?
) : DataResponse<UserDetail>, Parcelable {
    override fun retrieveData() = this

    fun isCreditDealer() = credit.creditLimit > 0
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Credit(
    @SerializedName("availableCredit")
    val availableCredit: Float,
    @SerializedName("creditLimit")
    val creditLimit: Float
) : Parcelable
