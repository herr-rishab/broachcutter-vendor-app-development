package net.broachcutter.vendorapp.screens.cart.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.models.cart.*
import net.broachcutter.vendorapp.network.*
import net.broachcutter.vendorapp.util.DI.INDUS_API
import net.broachcutter.vendorapp.util.DI.VALARTECH_API
import net.broachcutter.vendorapp.util.DynamicBaseUrl
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@Suppress("MagicNumber", "TooManyFunctions")
class BroachCutterCartRepository @Inject constructor(
    @Named(INDUS_API) val indusApi: BroachCutterApi,
    @Named(VALARTECH_API) val valartechApi: BroachCutterApi
) :
    BaseRoomCartRepository() {

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)
    }

    override suspend fun fetchPaymentTerms(paymentTermsRequest: PaymentTermsRequest): PaymentTermsResponse {
        Timber.i("Fetching payment terms")
        val response = withContext(IO) {
            val userId = userRepository.getUserDetail().userId
            val getPaymentTermsEndpoint = DynamicBaseUrl.getPaymentTerms(userId)
            indusApi.getPaymentTermsAsync(getPaymentTermsEndpoint, paymentTermsRequest.cartItems)
                .await()
        }
        if (response.isSuccessful) {
            return response.body()?.data!!
        } else {
            throw response.errorBody()?.let { AppException(it) } ?: AppException(response.code())
        }
    }

    override suspend fun getItemPricing(
        cartItem: CartItem,
        paymentTerm: PaymentTerm
    ): SimplePricedCartItem {
        val userId = userRepository.getUserDetail().userId
        val getFinalItemPriceEndpoint = DynamicBaseUrl.getFinalItemPrice(
            userId = userId,
            partNumber = cartItem.partNumber,
            paymentTermsId = paymentTerm.id,
            quantity = cartItem.quantity.toString()
        )
        val response = withContext(IO) {
            indusApi.getFinalItemPrice(
                getFinalItemPriceEndpoint
            )
        }
        if (response.isSuccessful && response.body()?.isSuccessful() == true) {
            return response.body()?.data!!
        } else {
            throw AppException(response.code())
        }
    }

    override suspend fun updatePaymentTerms(
        cartItem: CartItem,
        newTerms: PaymentTerm,
        newQuantity: Int
    ): CartItem {
        val paymentTerms =
            if (cartItem.product.partNumber == CUB_PART_NUMBER || cartItem.product.partNumber == CUB_XL_PART_NUMBER) {
                // we need to recheck that the current payment term still applies
                // after changing cub quantity
                val userId = userRepository.getUserDetail().userId
                val simpleItem = SimpleCartItem(cartItem.partNumber, newQuantity)
                val termsResponse = withContext(IO) {
                    val getPaymentTermsEndpoint = DynamicBaseUrl.getPaymentTerms(userId)
                    indusApi.getPaymentTermsAsync(getPaymentTermsEndpoint, listOf(simpleItem))
                        .await()
                }
                if (termsResponse.isSuccessful) {
                    // we know that we're getting just one payment term back for cub. Automatically select that
                    val firstTerm = termsResponse.body()?.data?.itemTerms?.get(0)?.terms?.get(0)
                    firstTerm ?: throw AppException(MISSING_DATA)
                } else {
                    // we had some network error while fetching updated terms
                    Timber.e(termsResponse.errorBody().toString())
                    throw AppException(termsResponse.code())
                }
            } else if (cartItem.selectedPaymentTerm != newTerms) {
                // we need to apply the new payment terms
                newTerms
            } else return cartItem // payment terms are the same as before

        Timber.i("Payment terms to be applied: ${paymentTerms.id}")
        // update to the determined payment terms
        cartItem.selectedPaymentTerm = paymentTerms
        val pricedCartItem = getItemPricing(
            cartItem,
            paymentTerms
        )
        cartItem.unitPrice = pricedCartItem.pricing
        cartDao.updateCartItem(cartItem)
        return cartItem
    }

    @Suppress("LongMethod")
    @DelicateCoroutinesApi
    override fun submitOrder(
        cartUiModel: CartUiModel,
        appliedCoupon: String?
    ): LiveData<Resource<PlaceOrderResponse>> {
        val liveData: MutableLiveData<Resource<PlaceOrderResponse>> = MutableLiveData()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            try {
                val userId =
                    userRepository.getUserDetail().userId // we should definitely have user details at this point
                val cartItems = withContext(IO) { cartDao.getCartItems() }
                val request = PlaceOrderRequest(
                    cartItems = SimplePricedCartItem.fromCartItems(cartItems),
                    billingAddress = getDeliveryAddress(),
                    deliveryAddress = getDeliveryAddress(),
                    appliedCoupon = appliedCoupon
                )
                val getPlaceOrderEndpoint = DynamicBaseUrl.getPlaceOrderApi(userId)
                val response = withContext(IO) {
                    valartechApi.submitOrder(getPlaceOrderEndpoint, request)
                }
                if (response.isSuccessful && response.body()?.isSuccessful() == true) {
                    val responseBody = response.body()?.data!!
                    analytics.checkoutOrderPlaceSuccess(cartUiModel, responseBody)
                    liveData.postValue(Resource.success(responseBody))
                } else {
                    val errorBody = response.errorBody()?.charStream()?.readText() ?: ""
                    Timber.i("submitOrder errorBody $errorBody")
                    if (errorBody.isNotEmpty()) {
                        val errorResponse = JSONObject(errorBody)
                        Timber.i("submitOrder errorResponse $errorResponse")
                        if (errorResponse.has("status_code")) {
                            val statusCode = errorResponse.getInt("status_code")
                            Timber.e("submitOrder $statusCode $errorResponse")
                            val appException = AppException(statusCode)
                            liveData.postValue(
                                Resource.error(
                                    appException.message,
                                    null,
                                    appException
                                )
                            )
                        } else {
                            Timber.e("submitOrder $errorResponse")
                            val appException = AppException(HTTP_500_INTERNAL_ERROR)
                            liveData.postValue(
                                Resource.error(
                                    appException.message,
                                    null,
                                    appException
                                )
                            )
                        }
                    } else {
                        val appException = AppException(HTTP_404_NOT_FOUND)
                        liveData.postValue(Resource.error(appException.message, null, appException))
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                val appException = AppException(ex)
                liveData.postValue(Resource.error(appException.message, null, appException))
            }
        }
        return liveData
    }

    override suspend fun fetchFinalCartPrice(totalCart: TotalCart): CartPrice {
        return withContext(IO) {
            val response = valartechApi.getTotalCartPrice(totalCart)
            if (response.isSuccessful && response.body()?.isSuccessful() == true) {
                return@withContext response.body()?.data!!
            } else {
                val errorBody = response.errorBody()?.charStream()?.readText() ?: ""
                Timber.i("fetchFinalCartPrice errorBody $errorBody")
                if (errorBody.isNotEmpty()) {
                    val errorResponse = JSONObject(errorBody)
                    Timber.i("fetchFinalCartPrice errorResponse $errorResponse")
                    val errorCode = errorResponse.getInt("status_code")
                    val errorMessage = errorResponse.getString("message")
                    throw AppException(errorCode, errorMessage)
                } else {
                    val errorCode: Int = response.body()?.status ?: UNKNOWN_ERROR
                    val errorMessage: String = response.body()?.message ?: ""
                    throw AppException(errorCode, errorMessage)
                }
            }
        }
    }
}
