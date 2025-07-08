package net.broachcutter.vendorapp.api

import androidx.lifecycle.LiveData
import com.valartech.commons.network.google.ApiResponse
import kotlinx.coroutines.Deferred
import net.broachcutter.vendorapp.models.*
import net.broachcutter.vendorapp.models.cart.*
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.network.Retry
import net.broachcutter.vendorapp.util.BCResponse
import retrofit2.Response
import retrofit2.http.*

@Suppress("TooManyFunctions")
interface BroachCutterApi {

    @GET("products/{partNumber}")
    fun getProductByPartNumberAsync(
        @Path("partNumber") partNumber: String
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/cutters")
    fun getCutterByPartNumAsync(
        @Query("partNumber") partNumber: String
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products")
    fun getProductList(
        @Query("search") search: String?,
        @Query("productType") productType: String?
    ): LiveData<ApiResponse<BCResponse<List<Product>>>>

    @GET("products")
    fun searchAsync(
        @Query("search") search: String?,
        @Query("productType") productType: String?
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/spares")
    fun getSparesAsync(
        @Query("spareOf") spareOf: String,
        @Query("search") query: String
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/cutters")
    fun getCuttersAsync(
        @Query("depthOfCut") depthOfCut: Int?,
        @Query("diameter") diameter: Float?,
        @Query("type") cutterType: String,
        @Query("material") cutterMaterial: String?,
        @Query("ShankType") shankType: String?
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/cutters/pilotPins")
    fun getPilotPinsAsync(
        @Query("diameter") diameter: Float?,
        @Query("length") length: Int?
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/accessories")
    fun getAccessoriesAsync(
        @Query("accessoriesOf") machinePartNumber: String
    ): Deferred<Response<BCResponse<List<Product>>>>

    @Retry
    @GET
    fun getUserDetailsAsync(
        @Url url: String
    ): Deferred<Response<BCResponse<UserDetail>>>

    @Retry
    @GET
    fun getDeferredUserDetailsAsync(
        @Url url: String
    ): Deferred<BCResponse<UserDetail>>

    @GET
    suspend fun getUpdatedOrderHistory(
        @Url url: String
    ): Response<BCResponse<UpdatedOrderHistory>>

    @POST
    fun registerUserAsync(
        @Url string: String,
        @Body userRequest: User
    ): Deferred<Response<BCResponse<Any>>>

    @GET("products/solidDrills")
    fun getSolidDrillsAsync(@Query("diameter") diameter: Float?): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/drillBits")
    fun getDrillBitsAsync(@Query("diameter") diameter: Float?): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/arbors")
    fun getArborsListAsync(
        @Query("morseTaper") morseTaper: String,
        @Query("diameter") diameter: Double?,
        @Query("depthOfCut") depthOfCut: Double?
    ): Deferred<Response<BCResponse<List<Product>>>>

    @POST
    fun getPaymentTermsAsync(
        @Url url: String,
        @Body cartItems: List<SimpleCartItem>
    ): Deferred<Response<BCResponse<PaymentTermsResponse>>>

    @GET("products/holesaws/spares")
    fun getHolesawSparesAsync(
        @Query("diameter") diameter: Double = 0.0
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET("products/holesaws")
    fun getHolesawsAsync(
        @Query("diameter") diameter: Int
    ): Deferred<Response<BCResponse<List<Product>>>>

    @GET
    suspend fun getFinalItemPrice(
        @Url url: String
    ): Response<BCResponse<SimplePricedCartItem>>

    @POST
    suspend fun submitOrder(
        @Url url: String,
        @Body request: PlaceOrderRequest
    ): Response<BCResponse<PlaceOrderResponse>>

    @GET("coupons")
    suspend fun getAllCoupons(): BCResponse<List<Coupon>>

    @POST("cart/totalCart")
    suspend fun getTotalCartPrice(
        @Body totalCart: TotalCart
    ): Response<BCResponse<CartPrice>>
}
