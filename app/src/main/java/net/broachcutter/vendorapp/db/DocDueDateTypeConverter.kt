package net.broachcutter.vendorapp.db

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate

class DocDueDateTypeConverter {
    /**
     * WRITE
     */
    @TypeConverter
    fun fromTimeStamp(timeStamp: Long?): LocalDate? {
        return if (timeStamp != null) {
            LocalDate.ofEpochDay(timeStamp)
        } else
            null
    }

    /**
     * READ
     */
    @TypeConverter
    fun toTimestamp(localDate: LocalDate?): Long? {
        return if (localDate != null) {
            return localDate.toEpochDay()
        } else
            null
    }
}
