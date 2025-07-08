package net.broachcutter.vendorapp.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

/**
 * Deserializer for [Gson].
 */
class ZonedDateTimeTypeAdapter @Inject constructor() : JsonDeserializer<ZonedDateTime> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ZonedDateTime? {
        val isoDateTimeString = json.asJsonPrimitive.asString
        return parseDateTime(isoDateTimeString)
    }

    @Suppress("ReturnCount")
    fun parseDateTime(dateTimeString: String): ZonedDateTime? {
        if (dateTimeString.isBlank()) {
            return null
        }
        if (!isDataFormatted(dateTimeString)) {
            val dateTimePatterns =
                arrayListOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ss")
            var exception: DateTimeParseException? = null
            dateTimePatterns.forEach {
                try {
                    val formatter = DateTimeFormatter.ofPattern(it)
                    val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
                    return ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Kolkata"))
                } catch (ex: DateTimeParseException) {
                    exception = ex
                }
            }
            // Parsing failed with all the known patterns, log the last exception
            Timber.e(exception)
            return null
        } else {
            val localDate =
                LocalDate.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            return ZonedDateTime.of(
                LocalDateTime.of(localDate, LocalTime.of(0, 0)),
                ZoneId.of("Asia/Kolkata")
            )
        }
    }

    private fun isDataFormatted(date: String): Boolean {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: DateTimeParseException) {
            return false
        }
        return true
    }
}
