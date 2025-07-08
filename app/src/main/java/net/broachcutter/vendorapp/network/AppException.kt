package net.broachcutter.vendorapp.network

import android.app.Application
import androidx.annotation.StringRes
import androidx.collection.ArrayMap
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigClientException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigServerException
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.models.BaseResponse
import net.broachcutter.vendorapp.util.BCResponse
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * Google common error codes: https://developers.google.com/android/reference/com/google/android/gms/common/api/CommonStatusCodes
 * Google sign in error codes: https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInStatusCodes.html
 */
// http errors
const val HTTP_401_UNAUTHORIZED = 401
const val HTTP_403_FORBIDDEN = 403
const val HTTP_404_NOT_FOUND = 404
const val HTTP_500_INTERNAL_ERROR = 500
const val HTTP_502_BAD_GATEWAY = 502
const val USER_LOGIN_FAILED = 451

// general exceptions
const val UNKNOWN_ERROR = 1000
const val NO_ACTIVE_CONNECTION = 1001
const val TIMEOUT = 1002
const val UNKNOWN_HTTP_EXCEPTION = 1006
const val MISSING_DATA = 1007
const val CANT_CONNECT_TO_SERVER = 1008
const val MISSING_JSON_DATA = 1009
const val MALFORMED_JSON = 1010
const val NULL_ACTIVITY = 1011

// user errors
const val INCORRECT_PASSWORD = 1100
const val INVALID_PHONE_NUMBER = 1101
const val BUSINESS_PARTNER_NOT_FOUND = 1103

// google and firebase errors
const val GENERIC_FIREBASE_ERROR = 1300
const val DEVELOPER_SETUP_ERROR = 1314
const val NULL_POINTER = 1315
const val SSL_HANDSHAKE = 1316
const val PLAY_SERVICES_UPDATE = 1318
const val FIREBASE_DATABASE_ERROR = 1321
const val GOOGLE_GENERIC_ERROR = 1329
const val INVALID_CREDENTIALS = 1330
const val TOO_MANY_REQUESTS = 1331
const val USER_COLLISION = 1332
const val INVALID_OTP = 1333
const val REMOTE_CONFIG_FETCH_EXCEPTION = 1334
const val REMOTE_CONFIG_FETCH_THROTTLED = 1335
const val ERROR_INVALID_MULTI_FACTOR_SESSION = 1336
const val NULL_MULTI_FACTOR_SESSION = 1337
const val RECENT_LOGIN_REQUIRED = 1338
const val USER_DISABLED = 1339
const val USER_NOT_FOUND = 1340
const val USER_TOKEN_EXPIRED = 1341
const val INVALID_USER_TOKEN = 1342

// Item and GroupNumber exceptions
const val ITEM_NOT_FOUND = 1401
const val GROUP_NUMBER_NOT_FOUND = 1402

// custom error
const val RESPONSE_BODY_IS_NULL = 1502
const val PARAMETER_VALUE_VALIDATION_ERROR = 1503
const val INVOICE_NOT_FOUND = 1504
const val ORDER_HISTORY_NOT_FOUND = 1505
const val PAYMENT_TERMS_ID_NOT_FOUND = 1506
const val USER_IS_NOT_REGISTERED = 1507
const val NO_PAYMENT_TERM = 1508

// Coupon
const val MIN_QUANTITY_NOT_AVAILABLE_IN_CART = 1601
const val OFFER_EXPIRED = 1602
const val COUPON_ON_NON_CREDIT = 1603
const val NO_COUPON_AVAILABILITY = 1604

class AppException : RuntimeException {

    companion object {
        // TODO: 05/01/18 convert to map of int - string, so that we can use string passed in by server
        private val errorCodeStringMap = ArrayMap<Int, Int>()
        val allowedErrors = listOf(
            REMOTE_CONFIG_FETCH_EXCEPTION,
            NO_ACTIVE_CONNECTION,
            INVALID_OTP,
            INCORRECT_PASSWORD,
            INVALID_PHONE_NUMBER,
            MIN_QUANTITY_NOT_AVAILABLE_IN_CART,
            OFFER_EXPIRED,
            COUPON_ON_NON_CREDIT,
            NO_COUPON_AVAILABILITY
        )
    }

    init {
        errorCodeStringMap[HTTP_401_UNAUTHORIZED] = R.string.unauthorized_error
        errorCodeStringMap[UNKNOWN_ERROR] = R.string.unknown_error
        errorCodeStringMap[NO_ACTIVE_CONNECTION] = R.string.error_no_internet_available
        errorCodeStringMap[TIMEOUT] = R.string.error_timeout
        errorCodeStringMap[HTTP_403_FORBIDDEN] = R.string.internal_error
        errorCodeStringMap[HTTP_404_NOT_FOUND] = R.string.internal_error
        errorCodeStringMap[HTTP_500_INTERNAL_ERROR] = R.string.internal_error
        errorCodeStringMap[HTTP_502_BAD_GATEWAY] = R.string.internal_error
        errorCodeStringMap[UNKNOWN_HTTP_EXCEPTION] = R.string.internal_error
        errorCodeStringMap[USER_LOGIN_FAILED] = R.string.internal_error
        errorCodeStringMap[CANT_CONNECT_TO_SERVER] = R.string.internal_error
        errorCodeStringMap[DEVELOPER_SETUP_ERROR] = R.string.login_setup_error
        errorCodeStringMap[NULL_POINTER] = R.string.internal_error
        errorCodeStringMap[SSL_HANDSHAKE] = R.string.error_no_internet_available
        errorCodeStringMap[PLAY_SERVICES_UPDATE] = R.string.play_services_update
        errorCodeStringMap[FIREBASE_DATABASE_ERROR] = R.string.internal_error
        errorCodeStringMap[MISSING_DATA] = R.string.internal_error
        errorCodeStringMap[MISSING_JSON_DATA] = R.string.internal_error
        errorCodeStringMap[MALFORMED_JSON] = R.string.internal_error
        errorCodeStringMap[GOOGLE_GENERIC_ERROR] = R.string.internal_error
        errorCodeStringMap[INVALID_CREDENTIALS] = R.string.invalid_credentials
        errorCodeStringMap[TOO_MANY_REQUESTS] = R.string.unusual_activity
        errorCodeStringMap[USER_COLLISION] = R.string.account_already_linked
        errorCodeStringMap[INVALID_OTP] = R.string.invalid_otp
        errorCodeStringMap[INCORRECT_PASSWORD] = R.string.login_auth_failure
        errorCodeStringMap[INVALID_PHONE_NUMBER] = R.string.invalid_phone_number
        errorCodeStringMap[REMOTE_CONFIG_FETCH_THROTTLED] = R.string.internal_error_try_again
        errorCodeStringMap[NULL_ACTIVITY] = R.string.internal_error_try_again
        errorCodeStringMap[BUSINESS_PARTNER_NOT_FOUND] = R.string.internal_error_try_again
        errorCodeStringMap[RESPONSE_BODY_IS_NULL] = R.string.internal_error_try_again
        errorCodeStringMap[ITEM_NOT_FOUND] = R.string.internal_error_try_again
        errorCodeStringMap[GROUP_NUMBER_NOT_FOUND] = R.string.internal_error_try_again
        errorCodeStringMap[ERROR_INVALID_MULTI_FACTOR_SESSION] = R.string.invalid_session
        errorCodeStringMap[PARAMETER_VALUE_VALIDATION_ERROR] = R.string.internal_error
        errorCodeStringMap[NULL_MULTI_FACTOR_SESSION] = R.string.internal_error
        errorCodeStringMap[INVOICE_NOT_FOUND] = R.string.internal_error
        errorCodeStringMap[ORDER_HISTORY_NOT_FOUND] = R.string.internal_error
        errorCodeStringMap[PAYMENT_TERMS_ID_NOT_FOUND] = R.string.internal_error
        errorCodeStringMap[USER_IS_NOT_REGISTERED] = R.string.internal_error
        errorCodeStringMap[USER_DISABLED] = R.string.internal_error
        errorCodeStringMap[USER_NOT_FOUND] = R.string.internal_error
        errorCodeStringMap[USER_TOKEN_EXPIRED] = R.string.internal_error
        errorCodeStringMap[RECENT_LOGIN_REQUIRED] = R.string.invalid_session
        errorCodeStringMap[OFFER_EXPIRED] = R.string.offer_expired
        errorCodeStringMap[COUPON_ON_NON_CREDIT] = R.string.coupon_on_non_credit
        errorCodeStringMap[NO_COUPON_AVAILABILITY] = R.string.no_coupon_available
        errorCodeStringMap[NO_PAYMENT_TERM] = R.string.no_payment_term_error
    }

    var errorCode: Int = 0
    private var throwable: Throwable? = null
    private var errorMessage: String? = null
    private val context: Application

    constructor(errorCode: Int) : super() {
        context = DealerApplication.INSTANCE
        this.errorCode = errorCode
        errorMessage = if (getErrorCodeMessageRes() == null)
            super.message
        else
            context.getString(getErrorCodeMessageRes()!!)
    }

    constructor(errorCode: Int, message: String) : super() {
        context = DealerApplication.INSTANCE
        this.errorCode = errorCode
        this.errorMessage = message
    }

    /**
     * Using type "in Nothing" here because we only read from baseResponse. We can't write to this
     * object, we're a consumer.
     */
    constructor(baseResponse: BaseResponse<in Nothing>) : super() {
        context = DealerApplication.INSTANCE
        this.errorCode = baseResponse.statusCode
        val errorMessageRes = getErrorCodeMessageRes()
        errorMessage = if (errorMessageRes != null) {
            // if we know the error code and have our own string defined, use that
            context.getString(errorMessageRes)
        } else {
            // otherwise use the server message
            baseResponse.message
        }
    }

    constructor(rawResponse: ResponseBody) : super() {
        context = DealerApplication.INSTANCE
        val responseString = rawResponse.string()
        try {
            val gson = Gson()
            val bcResponse =
                gson.fromJson<BCResponse<in Nothing>>(responseString, BCResponse::class.java)
            this.errorCode = bcResponse.status
            val errorMessageRes = getErrorCodeMessageRes()
            errorMessage = if (errorMessageRes != null) {
                // if we know the error code and have our own string defined, use that
                context.getString(errorMessageRes)
            } else {
                // otherwise use the server message
                bcResponse.message
            }
        } catch (ex: Exception) {
            Timber.i("Couldn't parse response body: $responseString")
            this.errorCode = UNKNOWN_ERROR
            val errorMessageRes = getErrorCodeMessageRes()
            errorMessage = errorMessageRes?.let { context.getString(it) }
        }
    }

    /**
     * Using type "in Nothing" here because we only read from bcResponse. We can't write to this
     * object, we're a consumer.
     */
    constructor(bcResponse: BCResponse<in Nothing>) : super() {
        context = DealerApplication.INSTANCE
        this.errorCode = bcResponse.status
        val errorMessageRes = getErrorCodeMessageRes()
        errorMessage = if (errorMessageRes != null) {
            // if we know the error code and have our own string defined, use that
            context.getString(errorMessageRes)
        } else {
            // otherwise use the server message
            bcResponse.message
        }
    }

    @Suppress("MagicNumber")
    constructor(throwable: Throwable?) : super(throwable) {
        context = DealerApplication.INSTANCE
        this.throwable = throwable

        /**
         * Higher priority exceptions should be placed higher in the when statement. If an exception
         * is a subclass of multiple cases, the first case would be selected.
         */
        when (throwable) {
            is AppException -> {
                this.errorCode = throwable.errorCode
                this.errorMessage = throwable.errorMessage
                this.throwable = throwable.throwable
            }
            is ApiException -> when (throwable.statusCode) {
                CommonStatusCodes.NETWORK_ERROR -> errorCode = NO_ACTIVE_CONNECTION
                CommonStatusCodes.TIMEOUT -> errorCode = TIMEOUT
                CommonStatusCodes.ERROR -> {
                    errorCode = UNKNOWN_ERROR
                    errorCode = DEVELOPER_SETUP_ERROR
                }
                CommonStatusCodes.DEVELOPER_ERROR -> errorCode = DEVELOPER_SETUP_ERROR
            }
            is HttpException -> errorCode = when (throwable.code()) {
                401 -> HTTP_401_UNAUTHORIZED
                403 -> HTTP_403_FORBIDDEN
                404 -> HTTP_404_NOT_FOUND
                500 -> HTTP_500_INTERNAL_ERROR
                502 -> HTTP_502_BAD_GATEWAY
                else -> UNKNOWN_HTTP_EXCEPTION
            }
            is FirebaseNetworkException, is UnknownHostException -> errorCode = NO_ACTIVE_CONNECTION
            is NullPointerException -> // if a stacktrace shows that an API call leads to this,
                // we have received a null "data" object in the response, despite it being successful
                // (having "success" in the status).
                errorCode = NULL_POINTER
            is JsonDataException -> errorCode = MISSING_JSON_DATA
            is JsonEncodingException -> errorCode = MISSING_JSON_DATA
            is JsonSyntaxException, is MalformedJsonException -> errorCode = MALFORMED_JSON
            is SSLHandshakeException -> errorCode = SSL_HANDSHAKE
            is SocketTimeoutException -> errorCode = TIMEOUT
            is SocketException -> errorCode = TIMEOUT
            is ConnectException -> errorCode = CANT_CONNECT_TO_SERVER
            is FirebaseAuthInvalidUserException -> {
                val firebaseException: FirebaseAuthInvalidUserException = throwable
                errorCode = when (firebaseException.errorCode) {
                    "ERROR_USER_DISABLED" -> USER_DISABLED
                    "ERROR_USER_NOT_FOUND" -> USER_NOT_FOUND
                    "ERROR_USER_TOKEN_EXPIRED" -> USER_TOKEN_EXPIRED
                    "ERROR_INVALID_USER_TOKEN" -> INVALID_USER_TOKEN
                    else -> {
                        Timber.e("New errorCode: ${firebaseException.errorCode}")
                        INVALID_CREDENTIALS
                    }
                }
            }
            is FirebaseAuthInvalidCredentialsException -> {
                val firebaseException: FirebaseAuthInvalidCredentialsException = throwable
                errorCode = when (firebaseException.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> INCORRECT_PASSWORD
                    "ERROR_INVALID_VERIFICATION_CODE" -> INVALID_OTP
                    "ERROR_INVALID_PHONE_NUMBER" -> INVALID_PHONE_NUMBER
                    "ERROR_INVALID_MULTI_FACTOR_SESSION" -> ERROR_INVALID_MULTI_FACTOR_SESSION
                    else -> {
                        Timber.e("New errorCode: ${firebaseException.errorCode}")
                        INVALID_CREDENTIALS
                    }
                }
            }
            is EOFException -> {
                errorCode = RESPONSE_BODY_IS_NULL
            }
            is FirebaseAuthRecentLoginRequiredException -> {
                errorCode = RECENT_LOGIN_REQUIRED
                errorMessage = throwable.message
            }
            is FirebaseTooManyRequestsException -> {
                errorCode = TOO_MANY_REQUESTS
                errorMessage = throwable.message
            }
            is FirebaseAuthUserCollisionException -> errorCode = USER_COLLISION
            is FirebaseRemoteConfigClientException, is FirebaseRemoteConfigServerException -> {
                errorCode = REMOTE_CONFIG_FETCH_EXCEPTION
                errorMessage = throwable.message
            }
            is FirebaseRemoteConfigFetchThrottledException -> {
                errorCode = REMOTE_CONFIG_FETCH_THROTTLED
            }
            is FirebaseException -> {
                val firebaseException: FirebaseException = throwable
                errorCode = GENERIC_FIREBASE_ERROR
                // grab the message given by firebase
                errorMessage = firebaseException.message
                return
            }
            else -> errorCode = UNKNOWN_ERROR
        }

        /*
        Priority of messages:
        1. Our mapped messages
        2. Server messages
        3. Default "unknown error" message.
         */
        val errorCodeMessageRes = getErrorCodeMessageRes()
        if (errorCodeMessageRes != null) {
            errorMessage = context.getString(errorCodeMessageRes)
        } else if (errorMessage == null) {
            errorMessage = context.getString(R.string.unknown_error)
        }
    }

    override val cause: Throwable?
        get() = throwable

    @StringRes
    private fun getErrorCodeMessageRes(): Int? {
        return errorCodeStringMap[errorCode]
    }

    override val message: String
        get() = String.format(
            context.getString(R.string.error_message_format),
            errorMessage,
            errorCode
        )
}
