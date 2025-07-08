package net.broachcutter.vendorapp.network.retrofit

import net.broachcutter.vendorapp.models.BaseResponse
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.network.MISSING_DATA
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Unwraps a [BaseResponse] to give the underlying [BaseResponse.data] to the API service.
 */
class BaseResponseConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        // The type which is wrapped
        val wrappedType = object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(type)
            }

            override fun getOwnerType(): Type? {
                return null
            }

            override fun getRawType(): Type {
                return BaseResponse::class.java
            }
        }

        // The next converter which needs to handle the response if current was not there.
        // we're using the gson converter that's next here
        val delegate =
            retrofit.nextResponseBodyConverter<ResponseBody>(this, wrappedType, annotations)

        return Converter<ResponseBody, Any> { value: ResponseBody ->
            val responseObject: Any? = delegate.convert(value)
            if (responseObject is BaseResponse<*>) {
                if (!responseObject.isSuccessful()) {
                    throw AppException(responseObject)
                }
                // Return BaseResponse object if it is of the type BaseResponse(not a subclass), else return the data.
                if (type == BaseResponse::class.java) {
                    responseObject
                } else {
                    responseObject.data ?: throw AppException(MISSING_DATA)
                }
            } else {
                responseObject
            }
        }
    }
}
