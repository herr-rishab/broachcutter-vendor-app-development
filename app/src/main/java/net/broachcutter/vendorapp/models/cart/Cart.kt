package net.broachcutter.vendorapp.models.cart

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.util.Constants

@Parcelize
@JsonClass(generateAdapter = true)
data class Cart(
    val cartItems: List<CartItem>?,
    val taxItems: List<TaxItem>?,
    val subtotal: Double?,
    val total: Double?,
    var exceededCredit: Double? = 0.0,
    var overduePayment: Boolean? = false,
    /** The amount of spares (in terms of Rs) that need to be added to the cart to meet the
     * minimum threshold
     **/
    var minRupeeAmountRequired: Double? = Constants.MIN_CART_TOTAL_RUPEES,
    var deliveryAddress: String?,
    var cartPrice: @RawValue CartPrice? = null
) : Parcelable {
    companion object {
        fun createEmptyCart(deliveryAddress: String?): Cart {
            return Cart(null, null, null, null, deliveryAddress = deliveryAddress)
        }
    }

    fun minBalanceCheckNeeded(): Boolean {
        return if (cartItems != null && cartItems.isNotEmpty()) {
            // Min balance check is not needed if we have only spares in cart
            var allSparesInCart = true
            cartItems.forEach {
                if (it.product.productType != ProductType.SPARE) {
                    allSparesInCart = false
                }
            }
            !allSparesInCart
        } else false
    }
}

@Parcelize
@Entity
@JsonClass(generateAdapter = true)
class CartItem(
    @Embedded
    val product: Product,
    @PrimaryKey
    @ColumnInfo(name = "part_number_primary")
    val partNumber: String,
    var quantity: Int,
    var unitPrice: Pricing?,
    var selectedPaymentTerm: PaymentTerm?,
    var paymentTerms: List<PaymentTerm>?
) : Parcelable {
    fun getLineItemPrice(): Pricing? {
        return unitPrice?.let {
            Pricing(
                basePrice = it.basePrice * quantity,
                discountPercent = it.discountPercent,
                finalPrice = it.finalPrice * quantity
            )
        }
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class SimplePricedCartItem(
    @Json(name = "cartQuantity")
    @SerializedName("cartQuantity")
    val cartQuantity: Int,
    @Json(name = "PartNumber")
    @SerializedName("PartNumber")
    val partNumber: String,
    @Json(name = "paymentTermsId")
    @SerializedName("paymentTermsId")
    val paymentTermId: String,
    @Json(name = "pricing")
    @SerializedName("pricing")
    val pricing: Pricing,
    @Json(name = "name")
    @SerializedName("name")
    val name: String
) : Parcelable {
    companion object {
        fun fromCartItems(cartItems: List<CartItem>): ArrayList<SimplePricedCartItem> {
            val list = ArrayList<SimplePricedCartItem>()
            var simplePricedCartItem: SimplePricedCartItem
            cartItems.forEach { cartItem ->
                if (cartItem.unitPrice != null && cartItem.selectedPaymentTerm != null) {
                    simplePricedCartItem = SimplePricedCartItem(
                        cartQuantity = cartItem.quantity,
                        partNumber = cartItem.partNumber,
                        paymentTermId = cartItem.selectedPaymentTerm!!.id,
                        pricing = cartItem.unitPrice!!,
                        name = ""
                    )
                    list.add(simplePricedCartItem)
                }
            }
            return list
        }
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class PaymentTerm(
    @Json(name = "Id")
    @SerializedName("Id")
    val id: String,
    @Json(name = "Description")
    @SerializedName("Description")
    val description: String,
    @Json(name = "DiscountPercent")
    @SerializedName("DiscountPercent")
    val discountPercent: Double,
    @Json(name = "GroupId")
    @SerializedName("GroupId")
    val groupId: Int?
) : Parcelable

/**
 * Pricing for a single unit of a product.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class Pricing(
    @Json(name = "listPrice")
    @SerializedName("listPrice")
    val basePrice: Double,
    @Json(name = "discountPercent")
    @SerializedName("discountPercent")
    val discountPercent: Float,
    @Json(name = "finalPrice")
    @SerializedName("finalPrice")
    val finalPrice: Double
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TaxItem(
    @SerializedName("type")
    @Json(name = "type")
    val type: String,
    @SerializedName("percentage")
    @Json(name = "percentage")
    val percentage: Float,
    @SerializedName("amount")
    @Json(name = "amount")
    var amount: Double = 0.0
) : Parcelable

data class TotalCart(
    @SerializedName("cartItems")
    val cartLineItem: List<CartLineItem>,
    @SerializedName("couponCode")
    val couponCode: String?
)

data class CartLineItem(
    @SerializedName("cartQuantity")
    val cartQuantity: Int,
    @SerializedName("partNumber")
    val partNumber: String,
    @SerializedName("paymentTermsId")
    val paymentTermsId: String,
    @SerializedName("productType")
    val productType: ProductType?,
)

data class CartPrice(
    @SerializedName("subTotal")
    val itemSubTotal: Double = 0.0,
    @SerializedName("totalDiscountPrice")
    val totalDiscountPrice: Double = 0.0,
    @SerializedName("couponDiscount")
    val couponDiscount: Double = 0.0,
    @SerializedName("totalTax")
    val totalTax: Double = 0.0,
    @SerializedName("totalPrice")
    val totalPrice: Double = 0.0,
)
