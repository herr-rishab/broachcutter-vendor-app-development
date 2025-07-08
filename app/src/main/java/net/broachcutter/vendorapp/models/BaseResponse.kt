package net.broachcutter.vendorapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

private const val STATUS_CODE_SUCCESS = 200

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @field:Json(name = "status_code") val statusCode: Int,
    val message: String?,
    val data: T?
) {
    fun isSuccessful() = statusCode == STATUS_CODE_SUCCESS
}
