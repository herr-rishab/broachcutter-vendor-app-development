package net.broachcutter.vendorapp.screens.coupon.slide

import com.google.gson.*
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type
import javax.inject.Inject

class ZonedDateTimeSerializerDeserializer @Inject constructor() :
    JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    private val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    override fun serialize(
        zonedDateTime: ZonedDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (zonedDateTime != null) {
            JsonPrimitive(zonedDateTime.format(formatter))
        } else {
            JsonPrimitive(ZonedDateTime.now().format(formatter))
        }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ZonedDateTime {
        return if (json != null) {
            ZonedDateTime.parse(json.asJsonPrimitive.asString, formatter)
        } else {
            ZonedDateTime.parse(ZonedDateTime.now().toString(), formatter)
        }
    }
}
