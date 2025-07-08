package net.broachcutter.vendorapp.util

import com.google.gson.annotations.SerializedName
import net.broachcutter.vendorapp.network.DataResponse

/**
 * A wrapper for the API response data.
 * [status] - Status of the response
 * [message] - Message if the API fails
 * [data] - Actual data in the API
 */
data class BCResponse<T>(
    @SerializedName("status_code") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
) : DataResponse<T?> {

    override fun retrieveData() = data

    // Check if the API is successful
    fun isSuccessful() = status == SUCCESS_STATUS

    companion object {
        private const val SUCCESS_STATUS = 200
        private const val SUCCESS_MESSAGE = "success"

        /**
         * Create a BCResponse object when fetching data from Local database
         */
        fun <U> createSuccessResponse(data: U): BCResponse<U> =
            BCResponse(SUCCESS_STATUS, SUCCESS_MESSAGE, data)
    }
}
