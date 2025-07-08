package net.broachcutter.vendorapp.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import net.broachcutter.vendorapp.models.TrackingDetail

class TrackingDetailTypeConverter {
    /**
     * WRITE
     * Convert a TrackingDetail to a Json
     */
    @TypeConverter
    fun fromTrackingDetailJson(trackingDetails: TrackingDetail?): String? {
        return if (trackingDetails != null) {
            Gson().toJson(trackingDetails)
        } else
            null
    }

    /**
     * READ
     * Convert a json to a TrackingDetail
     */
    @TypeConverter
    fun toTrackingDetail(trackingId: String?): TrackingDetail? {
        return if (trackingId != null) {
            Gson().fromJson(trackingId, TrackingDetail::class.java)
        } else
            null
    }
}
