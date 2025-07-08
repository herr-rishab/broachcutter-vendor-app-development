package net.broachcutter.vendorapp.db

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.models.cart.PaymentTerm
import net.broachcutter.vendorapp.models.cart.Pricing
import javax.inject.Inject

class CartTypeConverters {

    @Inject
    lateinit var moshi: Moshi

    private var pricingAdapter: JsonAdapter<Pricing>
    private var paymentTermAdapter: JsonAdapter<PaymentTerm>
    private var paymentTermListAdapter: JsonAdapter<List<PaymentTerm>>

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)
        pricingAdapter = moshi.adapter(Pricing::class.java)
        paymentTermAdapter = moshi.adapter(PaymentTerm::class.java)

        val type = Types.newParameterizedType(List::class.java, PaymentTerm::class.java)
        paymentTermListAdapter = moshi.adapter(type)
    }

    @TypeConverter
    fun pricingToJsonString(pricing: Pricing?): String? {
        return if (pricing != null) {
            pricingAdapter.toJson(pricing)
        } else null
    }

    @TypeConverter
    fun pricingFromJsonString(jsonString: String?): Pricing? {
        return if (jsonString != null) {
            pricingAdapter.fromJson(jsonString)
        } else null
    }

    @TypeConverter
    fun paymentTermToJsonString(paymentTerm: PaymentTerm?): String? {
        return if (paymentTerm != null) {
            paymentTermAdapter.toJson(paymentTerm)
        } else null
    }

    @TypeConverter
    fun paymentTermFromJsonString(jsonString: String?): PaymentTerm? {
        return if (jsonString != null) {
            paymentTermAdapter.fromJson(jsonString)
        } else null
    }

    @TypeConverter
    fun paymentTermListToJsonString(paymentTerms: List<PaymentTerm>?): String? {
        return if (paymentTerms != null) {
            paymentTermListAdapter.toJson(paymentTerms)
        } else null
    }

    @TypeConverter
    fun paymentTermListFromJsonString(jsonString: String?): List<PaymentTerm>? {
        return if (jsonString != null) {
            paymentTermListAdapter.fromJson(jsonString)
        } else null
    }
}
