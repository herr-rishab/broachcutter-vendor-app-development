package net.broachcutter.vendorapp.screens.home.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.valartech.commons.network.google.ApiResponse
import com.valartech.commons.network.google.NetworkBoundResource
import com.valartech.commons.network.google.RateLimiter
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.db.UpdatedOrderDao
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.models.UpdatedOrderHistory
import net.broachcutter.vendorapp.models.UserDetail
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.network.HTTP_500_INTERNAL_ERROR
import net.broachcutter.vendorapp.network.MALFORMED_JSON
import net.broachcutter.vendorapp.network.MISSING_DATA
import net.broachcutter.vendorapp.network.NO_ACTIVE_CONNECTION
import net.broachcutter.vendorapp.network.google.AppExecutors
import net.broachcutter.vendorapp.network.networkCall
import net.broachcutter.vendorapp.util.BCResponse
import net.broachcutter.vendorapp.util.DI.INDUS_API
import net.broachcutter.vendorapp.util.DynamicBaseUrl
import net.broachcutter.vendorapp.util.isInternetConnected
import net.broachcutter.vendorapp.util.isValidJson
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class BroachcutterUserRepository @Inject constructor(
    @Named(INDUS_API) val broachCutterApi: BroachCutterApi,
    val appExecutors: AppExecutors,
    val updatedOrderDao: UpdatedOrderDao,
    val firebaseHolder: FirebaseHolder,
    val context: Application
) : UserRepository {

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)
    }

    private val userDetailLiveData = MutableLiveData<UserDetail>()

    companion object {
        private const val ORDER_HISTORY_CACHE_TIME_MIN = 1
    }

    /**
     * Cached version of the user details.
     */
    private var userDetail: UserDetail? = null
        set(value) {
            field = value
            value?.let { userDetailLiveData.postValue(it) }
        }

    override suspend fun getUserDetail(): UserDetail {
        return userDetail ?: refreshUserDetailsAsync()
    }

    override fun getCachedUserDetail(): UserDetail? = userDetail

    override fun getUserUpdates(): LiveData<UserDetail> = userDetailLiveData

    /**
     * Keep orders in the db cache for 10 minutes.
     */
    private val orderHistoryDataRateLimit =
        RateLimiter<String>(ORDER_HISTORY_CACHE_TIME_MIN, TimeUnit.MINUTES)

    override fun refreshUserDetails(): LiveData<Resource<UserDetail>> =
        networkCall<BCResponse<UserDetail>, UserDetail>(
            block = {
                val getUserEndPoint = DynamicBaseUrl.getUser()
                client = broachCutterApi.getUserDetailsAsync(getUserEndPoint)
            },
            successCallback = { userDetail = it }
        )

    override suspend fun refreshUserDetailsAsync(): UserDetail {
        val getUserEndPoint = DynamicBaseUrl.getUser()
        val userDetails = broachCutterApi.getDeferredUserDetailsAsync(getUserEndPoint).await()
        if (userDetails.isSuccessful()) {
            userDetail = userDetails.data
            return userDetails.data!!
        } else {
            throw AppException(userDetails)
        }
    }

    @Suppress("MagicNumber")
    override fun getOrderHistoryUpdated(
        month: Int,
        year: Int,
        forceRefresh: Boolean,
        scope: CoroutineScope
    ): LiveData<Resource<UpdatedOrderHistory>> {

        return object :
            NetworkBoundResource<UpdatedOrderHistory, BCResponse<UpdatedOrderHistory>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<BCResponse<UpdatedOrderHistory>>> {
                val liveData = MutableLiveData<ApiResponse<BCResponse<UpdatedOrderHistory>>>()
                scope.launch {
                    try {
                        if (context.isInternetConnected()) {
                            val getOrderHistoryEndPoint =
                                DynamicBaseUrl.getOrderHistory(month.toString(), year.toString())
                            val response = withContext(Dispatchers.IO) {
                                broachCutterApi.getUpdatedOrderHistory(getOrderHistoryEndPoint)
                            }
                            if (response.isSuccessful && response.body()?.isSuccessful() == true) {
                                val responseBody = response.body()?.data!!
                                val bcResource = BCResponse.createSuccessResponse(responseBody)
                                liveData.postValue(ApiResponse.create(Response.success(bcResource)))
                            } else {
                                val errorBody = response.errorBody()?.charStream()?.readText() ?: ""

                                /**
                                 * Check errorBody is not empty and valid json
                                 */
                                if (errorBody.isNotEmpty() && errorBody.isValidJson()) {
                                    val errorResponse = JSONObject(errorBody)
                                    Timber.i("getOrderHistoryUpdated errorResponse $errorResponse")

                                    /**
                                     * If errorResponse have status_code
                                     * then parse in AppException
                                     */
                                    if (errorResponse.has("status_code")) {
                                        val statusCode = errorResponse.getInt("status_code")
                                        Timber.e("getOrderHistoryUpdated $statusCode $errorResponse")
                                        val appException = AppException(statusCode)
                                        liveData.postValue(ApiResponse.Companion.create(appException))
                                    } else {
                                        /**
                                         * Return HTTP_500_INTERNAL_ERROR if errorResponse don't have status_code
                                         */
                                        Timber.e("getOrderHistoryUpdated $errorResponse")
                                        val appException = AppException(HTTP_500_INTERNAL_ERROR)
                                        liveData.postValue(ApiResponse.Companion.create(appException))
                                    }
                                } else {
                                    /**
                                     * API call failed and not having valid json body
                                     */
                                    val appException = AppException(MALFORMED_JSON)
                                    Timber.e("getOrderHistoryUpdated ${appException.message}")
                                    liveData.postValue(ApiResponse.Companion.create(appException))
                                }
                            }
                        } else {
                            liveData.postValue(
                                ApiResponse.Companion.create(
                                    AppException(
                                        NO_ACTIVE_CONNECTION
                                    )
                                )
                            )
                        }
                    } catch (exception: Exception) {
                        Timber.e(exception)
                        liveData.postValue(ApiResponse.Companion.create(AppException(exception)))
                    }
                }
                return liveData
            }

            override fun loadFromDb(): LiveData<UpdatedOrderHistory> =
                updatedOrderDao.getAllOrdersLiveData().map { orders ->
                    // filtering directly in room query is painful since month and year are part of ZonedDateTime
                    val selectedMonthOrders =
                        orders?.filter { it.orderDate.monthValue == month && it.orderDate.year == year }
                    UpdatedOrderHistory(month, year, selectedMonthOrders)
                }

            override fun onFetchFailed(errorMessage: String) {
            }

            override fun saveCallResult(item: BCResponse<UpdatedOrderHistory>) {
                item.data?.updatedOrder?.let {
                    updatedOrderDao.addOrders(it)
                }
            }

            override fun shouldFetch(data: UpdatedOrderHistory?): Boolean {
                return data == null || data.updatedOrder.isNullOrEmpty() || forceRefresh ||
                    orderHistoryDataRateLimit.shouldFetch(UpdatedOrderHistory.TAG)
            }
        }.asLiveData()
    }

    override fun resetPassword(): LiveData<Resource<Any>> {
        val resetLiveData = MutableLiveData<Resource<Any>>()
        resetLiveData.postValue(Resource.loading(null))
        GlobalScope.launch {
            val email = getUserEmail()
            email?.let {
                val auth = firebaseHolder.auth
                auth.sendPasswordResetEmail(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resetLiveData.postValue(Resource.success(null))
                    } else {
                        val appException = AppException(task.exception)
                        resetLiveData.postValue(Resource.error(appException.message, null))
                    }
                }
            } ?: resetLiveData.postValue(Resource.error(AppException(MISSING_DATA).message, null))
        }
        return resetLiveData
    }

    private fun getUserEmail(): String? {
        return userDetail?.email
    }
}
