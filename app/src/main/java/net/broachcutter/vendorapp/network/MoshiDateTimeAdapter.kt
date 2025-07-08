package net.broachcutter.vendorapp.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class MoshiDateTimeAdapter @Inject constructor() {

    @ToJson
    fun toJson(zonedDateTime: ZonedDateTime): String {
        return DateTimeFormatter.ISO_DATE_TIME.format(zonedDateTime)
    }

    @FromJson
    fun fromJson(isoDateTimeString: String): ZonedDateTime {
        return ZonedDateTime.parse(isoDateTimeString)
    }
}
