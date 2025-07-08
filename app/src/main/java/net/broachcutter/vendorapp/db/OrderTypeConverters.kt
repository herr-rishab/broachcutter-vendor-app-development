package net.broachcutter.vendorapp.db

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.models.UpdatedOrderItem
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.models.cart.TaxItem
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@Suppress("TooManyFunctions")
class OrderTypeConverters {

    @Inject
    lateinit var moshi: Moshi

    private var taxItemListAdapter: JsonAdapter<List<TaxItem>>
    private var updatedOrderItemListAdapter: JsonAdapter<List<UpdatedOrderItem>>

    companion object {
        val indianZoneId = ZoneId.of("Asia/Kolkata")
    }

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)

        val updatedOrderItemListType =
            Types.newParameterizedType(List::class.java, UpdatedOrderItem::class.java)
        updatedOrderItemListAdapter = moshi.adapter(updatedOrderItemListType)

        val taxItemListType = Types.newParameterizedType(List::class.java, TaxItem::class.java)
        taxItemListAdapter = moshi.adapter(taxItemListType)
    }

    @TypeConverter
    fun fromUpdatedOrderItemList(orderItems: List<UpdatedOrderItem>?): String? {
        return if (orderItems != null) {
            updatedOrderItemListAdapter.toJson(orderItems)
        } else null
    }

    @TypeConverter
    fun toUpdatedOrderItemList(jsonString: String?): List<UpdatedOrderItem>? {
        return if (jsonString != null) {
            updatedOrderItemListAdapter.fromJson(jsonString)
        } else null
    }

    @TypeConverter
    fun fromTaxItemList(taxItems: List<TaxItem>?): String? {
        return if (taxItems != null) {
            taxItemListAdapter.toJson(taxItems)
        } else null
    }

    @TypeConverter
    fun toOrderStatus(value: Int) = enumValues<UpdatedOrderStatus>()[value]

    @TypeConverter
    fun fromOrderStatus(value: UpdatedOrderStatus) = value.ordinal

    @TypeConverter
    fun toTaxItemList(jsonString: String?): List<TaxItem>? {
        return if (jsonString != null) {
            taxItemListAdapter.fromJson(jsonString)
        } else null
    }

    /**
     * Expected timezone is IST.
     */
    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime?): Long? {
        return zonedDateTime?.toEpochSecond()
    }

    @TypeConverter
    fun toZonedDateTime(epoch: Long?): ZonedDateTime? {
        val instant = epoch?.let { Instant.ofEpochSecond(it) }
        return instant?.let { ZonedDateTime.ofInstant(instant, indianZoneId) }
    }
}
