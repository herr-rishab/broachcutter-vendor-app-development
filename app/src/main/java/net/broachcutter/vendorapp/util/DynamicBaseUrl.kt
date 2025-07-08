package net.broachcutter.vendorapp.util

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import net.broachcutter.vendorapp.BuildConfig
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.screens.splash.SplashViewModel
import net.broachcutter.vendorapp.util.Constants.INDUS
import timber.log.Timber
import java.util.concurrent.TimeUnit

object DynamicBaseUrl {

    private const val API_PLACE_ORDER_KEY = "api_place_order"
    private const val API_PLACE_ORDER_DEFAULT = "valartech"
    private const val API_ORDER_HISTORY = "api_order_history"
    private const val API_ORDER_HISTORY_DEFAULT = "indus"
    private const val API_USER = "api_user"
    private const val API_USER_DEFAULT = "indus"
    private const val API_USER_REGISTER = "api_user_register"
    private const val API_USER_REGISTER_DEFAULT = "indus"
    private const val API_PAYMENT_TERMS = "api_payment_terms"
    private const val API_PAYMENTS_TERMS_DEFAULT = "indus"
    private const val API_FINAL_ITEM_PRICE = "api_final_item_price"
    private const val API_FINAL_ITEM_PRICE_DEFAULT = "indus"

    private val defaultsMap = mapOf(
        API_PLACE_ORDER_KEY to API_PLACE_ORDER_DEFAULT,
        API_ORDER_HISTORY to API_ORDER_HISTORY_DEFAULT,
        API_USER to API_USER_DEFAULT,
        API_USER_REGISTER to API_USER_REGISTER_DEFAULT,
        API_PAYMENT_TERMS to API_PAYMENTS_TERMS_DEFAULT,
        API_FINAL_ITEM_PRICE to API_FINAL_ITEM_PRICE_DEFAULT
    )

    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init() {
        remoteConfig = getFirebaseRemoteConfig()
    }

    private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {

        val remoteConfig = Firebase.remoteConfig
        val cacheDuration = if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toSeconds(
            SplashViewModel.FIREBASE_CONFIG_CACHE_HOURS
        )
        val configSettings = remoteConfigSettings {
            fetchTimeoutInSeconds = SplashViewModel.FIREBASE_CONFIG_FETCH_TIMEOUT_SEC
            minimumFetchIntervalInSeconds = cacheDuration
        }

        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(defaultsMap)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("remoteConfig.fetchAndActivate success")
                if (task.result == true) {
                    // successfully fetched values
                    Timber.i("remoteConfig.fetchAndActivate remote values activated")
                } else {
                    // no configs were fetched from the backend and the local fetched configs
                    // have already been activated
                    Timber.i("remoteConfig.fetchAndActivate local values used")
                }
            } else {
                task.exception?.let {
                    val ex = AppException(it)
                    Timber.e(ex)
                }
            }
        }

        return remoteConfig
    }

    fun getPlaceOrderApi(userId: String): String {
        val getPlaceOrderEndpoint = remoteConfig.getString(API_PLACE_ORDER_KEY)
        return if (getPlaceOrderEndpoint == Constants.INDUS) {
            submitOrdersUrl(baseUrl = BuildConfig.API_ENDPOINT.dropLast(1), uid = userId)
        } else {
            submitOrdersUrl(
                baseUrl = BuildConfig.VALARTECH_API_ENDPOINT.dropLast(1),
                uid = userId
            )
        }
    }

    fun getFinalItemPrice(
        userId: String,
        partNumber: String,
        paymentTermsId: String,
        quantity: String
    ): String {
        val getFinalItemPriceEndpoint = remoteConfig.getString(API_FINAL_ITEM_PRICE)
        if (getFinalItemPriceEndpoint == Constants.INDUS) {
            return getFinalPriceUrl(
                baseUrl = BuildConfig.API_ENDPOINT.dropLast(1),
                uid = userId,
                partNumber = partNumber,
                paymentTermsId = paymentTermsId,
                quantity = quantity
            )
        } else {
            return getFinalPriceUrl(
                baseUrl = BuildConfig.VALARTECH_API_ENDPOINT.dropLast(1),
                uid = userId,
                partNumber = partNumber,
                paymentTermsId = paymentTermsId,
                quantity = quantity
            )
        }
    }

    fun getPaymentTerms(userId: String): String {
        val getPaymentTermsEndpoint = remoteConfig.getString(API_PAYMENT_TERMS)
        return if (getPaymentTermsEndpoint == Constants.INDUS) {
            getPaymentTermsUrl(BuildConfig.API_ENDPOINT.dropLast(1), userId)
        } else {
            getPaymentTermsUrl(BuildConfig.VALARTECH_API_ENDPOINT.dropLast(1), userId)
        }
    }

    fun getOrderHistory(month: String, year: String): String {
        val getOrderHistoryEndPoint = remoteConfig.getString(API_ORDER_HISTORY)
        return if (getOrderHistoryEndPoint == Constants.INDUS) {
            getOrderHistoryUrl(
                BuildConfig.API_ENDPOINT.dropLast(1),
                month,
                year
            )
        } else {
            getOrderHistoryUrl(
                BuildConfig.VALARTECH_API_ENDPOINT.dropLast(1),
                month,
                year
            )
        }
    }

    fun getUser(): String {
        val getUserEndpoint = remoteConfig.getString(API_USER)
        return if (getUserEndpoint == Constants.INDUS) {
            getUserUrl(BuildConfig.API_ENDPOINT.dropLast(1))
        } else {
            getUserUrl(BuildConfig.VALARTECH_API_ENDPOINT.dropLast(1))
        }
    }

    fun registerUser(): String {
        val registerUserEndpoint = remoteConfig.getString(API_USER_REGISTER)
        return if (registerUserEndpoint == INDUS) {
            getRegisterUserUrl(BuildConfig.API_ENDPOINT.dropLast(1))
        } else {
            getRegisterUserUrl(BuildConfig.VALARTECH_API_ENDPOINT.dropLast(1))
        }
    }
}
