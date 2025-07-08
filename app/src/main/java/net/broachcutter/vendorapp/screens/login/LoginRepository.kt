package net.broachcutter.vendorapp.screens.login

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.google.firebase.auth.PhoneMultiFactorInfo
import com.onesignal.OneSignal
import com.valartech.commons.network.google.Resource
import com.valartech.commons.utils.extensions.isValidEmail
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.sentry.Sentry
import io.sentry.protocol.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.network.BCAuthHeaderInterceptor
import net.broachcutter.vendorapp.network.MISSING_DATA
import net.broachcutter.vendorapp.network.NULL_ACTIVITY
import net.broachcutter.vendorapp.network.NULL_MULTI_FACTOR_SESSION
import net.broachcutter.vendorapp.network.USER_IS_NOT_REGISTERED
import net.broachcutter.vendorapp.network.google.AppExecutors
import net.broachcutter.vendorapp.screens.login.LoginRepository.LoginListener
import net.broachcutter.vendorapp.util.Constants
import net.broachcutter.vendorapp.util.DI.INDUS_API
import net.broachcutter.vendorapp.util.DynamicBaseUrl
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Named

private const val OTP_WAIT_TIMEOUT_SEC = 30L
private const val OTP_LENGTH = 6
private const val MIN_PASSWORD_LENGTH = 6
private const val DISABLED_USER_ERROR_CODE = "ERROR_USER_DISABLED"
private const val FIREBASE_USER_PHONE_PROVIDER = "phone"

inline class VerificationState(val isVerified: Boolean)

/**
 * Login codes for username/password.
 */
enum class LoginCodes {
    LOCAL_VALIDATION_SUCCESS,
    SUCCESS_FIRST_LOGIN,
    SUCCESS,
    OTP_SENT,
    EMPTY_EMAIL,
    INVALID_EMAIL,
    EMPTY_PASSWORD,
    INVALID_USER,
    DISABLED_USER,
    AUTH_FAILURE,
    TOKEN_RETRIEVAL_FAILURE,
    NETWORK_ERROR,
    DEVELOPER_ERROR,
    INVALID_PHONE_NUMBER,
    TOO_MANY_ATTEMPTS,
    RELOGIN_REQUIRED,
    UNKNOWN_ERROR
}

enum class NewPasswordCodes {
    SUCCESS,
    EMPTY_PASSWORD1,
    EMPTY_PASSWORD2,
    PASSWORD_MISMATCH,
    PASSWORD_REQUIREMENTS,
    RELOGIN_REQUIRED,
    UNKNOWN_ERROR
}

enum class ResetPasswordCodes {
    SUCCESS, INVALID_EMAIL
}

/**
 * We want to have users that have both their email and phone numbers verified.
 *
 * Sign up flow:
 * 1. User is given a preset password to login with using their email.
 * 2.
 *
 *
 * Sign up test cases:
 * 1. Sign up with whitelisted phone number + autoverified + resend
 * 2. Sign up with real number + resend
 *
 * Login test cases:
 * 1. Login with real number + resend
 * 2. Login with whitelisted number + autoverified + resend
 *
 * todo get rid of [LoginListener] to streamline MVP and MVVM approaches, cut down method count
 */
@Suppress("TooManyFunctions", "LargeClass", "LongParameterList")
class LoginRepository(
    val appExecutors: AppExecutors,
    val application: Application,
    private val authHeaderInterceptor: BCAuthHeaderInterceptor,
    val sharedPreferences: SharedPreferences,
    @Named(INDUS_API) val broachCutterApi: BroachCutterApi,
    val firebaseHolder: FirebaseHolder,
    val analytics: Analytics
) {
    var storedVerificationId: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    var phoneNumber: String? = null
    private lateinit var multiFactorResolver: MultiFactorResolver
    private var multiFactorLoginTask: Task<AuthResult>? = null

    /**
     * this is initialized during signup flow and reuse in resend OTP in signup flow
     */
    private var multiFactorSessionTask: Task<MultiFactorSession>? = null

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)
    }

    // @HunterDebugImpl
    fun attemptLogin(
        email: String,
        password: String,
        loginListener: LoginListener,
        activityRef: WeakReference<Activity>
    ) {
        Timber.i("attemptLogin")
        setUserEmail(email)
        // uncomment for testing with fake phone numbers
//        FirebaseAuth.getInstance()
//            .firebaseAuthSettings
//            .setAppVerificationDisabledForTesting(true)
        val localValidationResult = validateUsernamePasswordLocally(email, password)
        if (localValidationResult == LoginCodes.LOCAL_VALIDATION_SUCCESS) {
            // Local validation passed, try validating against server
            val auth = firebaseHolder.auth
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && !isSignUpIncomplete(user)) {
                        // this shouldn't happen, multi factor auth is mandatory after signup
                        Timber.e("signInWithEmailAndPassword success without multi factor")
                    }
                    retrieveToken(loginListener, activityRef, task)
                } else {
                    // server login failed
                    if (task.exception is FirebaseAuthMultiFactorException) {
                        // this is normal flow
                        multiFactorLogin(task, activityRef, loginListener)
                    } else {
                        loginListener.onLoginAttemptComplete(getLoginError(task.exception))
                    }
                }
            }
        } else {
            // local validation error
            loginListener.onLoginAttemptComplete(localValidationResult)
        }
    }

    private fun setUserEmail(email: String) {
        val user = User()
        user.email = email
        Sentry.setUser(user)
    }

    private fun getLoginError(exception: Exception?): LoginCodes {
        Timber.i("getLoginError")
        return when (exception) {
            is FirebaseAuthInvalidUserException -> {
                if (exception.errorCode == DISABLED_USER_ERROR_CODE) {
                    LoginCodes.DISABLED_USER
                } else {
                    LoginCodes.INVALID_USER
                }
            }

            is FirebaseAuthInvalidCredentialsException -> LoginCodes.AUTH_FAILURE
            is FirebaseTooManyRequestsException -> LoginCodes.TOO_MANY_ATTEMPTS
            is FirebaseNetworkException -> LoginCodes.NETWORK_ERROR
            else -> {
                Timber.e(exception, "Unknown firebase exception")
                LoginCodes.UNKNOWN_ERROR
            }
        }
    }

    // @HunterDebugImpl
    private fun retrieveToken(
        loginListener: LoginListener,
        activityRef: WeakReference<Activity>,
        authTask: Task<AuthResult>
    ) {
        Timber.i("retrieveToken")
        val user = firebaseHolder.auth.currentUser
        user?.getIdToken(false)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveUserId(user.uid)
                // we're logged in and have a token
                val token = task.result?.token
                token?.let { saveToken(it) } ?: Timber.e("Token fetch failed")

                if (isSignUpIncomplete(user)) {
                    Timber.i("First email login complete")
                    loginListener.onLoginAttemptComplete(LoginCodes.SUCCESS_FIRST_LOGIN)
                }
            } else if (task.exception is FirebaseAuthMultiFactorException) {
                Timber.i("Non-first email login complete")
                multiFactorLogin(authTask, activityRef, loginListener)
            } else {
                Timber.e(task.exception)
                loginListener.onLoginAttemptComplete(LoginCodes.TOKEN_RETRIEVAL_FAILURE)
            }
        } ?: loginListener.onLoginAttemptComplete(LoginCodes.TOKEN_RETRIEVAL_FAILURE)
    }

    /**
     * Client-side validation.
     *
     * @return [LoginCodes]
     */
    private fun validateUsernamePasswordLocally(username: String, password: String): LoginCodes =
        when {
            username.isBlank() -> LoginCodes.EMPTY_EMAIL
            password.isBlank() -> LoginCodes.EMPTY_PASSWORD
            !username.isValidEmail() -> LoginCodes.INVALID_EMAIL
            else -> LoginCodes.LOCAL_VALIDATION_SUCCESS
        }

    private fun multiFactorLogin(
        task: Task<AuthResult>,
        activityRef: WeakReference<Activity>,
        loginListener: LoginListener
    ) {
        Timber.i("multiFactorLogin")
        val e = task.exception as FirebaseAuthMultiFactorException
        val multiFactorResolver = e.resolver
        val selectedHint = multiFactorResolver.hints[0]
        val multiFactorInfo = selectedHint as PhoneMultiFactorInfo
        this.phoneNumber = multiFactorInfo.phoneNumber
        // Send the SMS verification code.
        activityRef.get()?.let {
            PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder()
                    .setActivity(it)
                    .requireSmsValidation(true)
                    .setMultiFactorSession(multiFactorResolver.session)
                    .setMultiFactorHint(multiFactorInfo)
                    .setCallbacks(RegularLoginOtpHandler(loginListener))
                    .setTimeout(OTP_WAIT_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .build()
            )
        } ?: Timber.e("Activity is destroyed, can't send OTP")
        this.multiFactorResolver = multiFactorResolver
        this.multiFactorLoginTask = task
    }

    /**
     * The user is prompted to set a new password the first time they log in.
     */
    fun attemptToSetPassword(
        password1: String,
        password2: String,
        listener: (NewPasswordCodes) -> Unit
    ) {
        Timber.i("attemptToSetPassword")
        val localValidationResult = validateNewPasswordLocally(password1, password2)
        if (localValidationResult == NewPasswordCodes.SUCCESS) {
            val firebaseUser = firebaseHolder.auth.currentUser
            firebaseUser?.let { user ->
                user.updatePassword(password1).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Timber.d("Password set successfully")
                        listener.invoke(NewPasswordCodes.SUCCESS)
                    } else {
                        Timber.i("Password set error")
                        /**
                         *The user has attempted to perform a security sensitive operation but
                         * too much time has elapsed since signing in to the app.
                         * (If it has been a few hours since the current user signed in, however,
                         * the operation will fail with a FirebaseAuthRecentLoginRequiredException exception.)
                         */
                        if (it.exception is FirebaseAuthRecentLoginRequiredException) {
                            listener.invoke(NewPasswordCodes.RELOGIN_REQUIRED)
                        } else {
                            Timber.e(it.exception)
                            listener.invoke(NewPasswordCodes.UNKNOWN_ERROR)
                        }
                    }
                }
            } ?: Timber.e("attemptToSetPassword => Firebase user is null")
        } else {
            listener.invoke(localValidationResult)
        }
    }

    /**
     * @return [NewPasswordCodes]
     */
    private fun validateNewPasswordLocally(password1: String, password2: String): NewPasswordCodes =
        when {
            password1.isBlank() -> NewPasswordCodes.EMPTY_PASSWORD1
            password2.isBlank() -> NewPasswordCodes.EMPTY_PASSWORD2
            password1 != password2 -> NewPasswordCodes.PASSWORD_MISMATCH
            password1.length < MIN_PASSWORD_LENGTH -> NewPasswordCodes.PASSWORD_REQUIREMENTS
            else -> NewPasswordCodes.SUCCESS
        }

    /**
     * In case the user didn't get a message the first time around. Called during login and sign up
     * flows.
     */
    @Suppress("ReturnCount", "LongMethod")
    fun resendOtp(
        inSignUpFlow: Boolean,
        activityRef: WeakReference<Activity>,
        phoneNumber: String,
    ): LiveData<Resource<VerificationState>> {
        Timber.i("resendOtp")
        val sendOtpState = MutableLiveData<Resource<VerificationState>>()
        sendOtpState.postValue(Resource.loading(null))
        val token = resendToken

        if (inSignUpFlow) {
            if (token == null) {
                Timber.e("Task or token are null | Token: $token")
                val ex = AppException(MISSING_DATA)
                sendOtpState.postValue(Resource.error(ex.message))
                return sendOtpState
            }

            if (multiFactorSessionTask == null) {
                Timber.e("MultiFactor Session is null")
                val ex = AppException(NULL_MULTI_FACTOR_SESSION)
                sendOtpState.postValue(Resource.error(ex.message))
                return sendOtpState
            }

            multiFactorSessionTask?.let { multiFactorSession ->
                activityRef.get()?.let {
                    val optionsBuilder = PhoneAuthOptions.newBuilder()
                        .requireSmsValidation(true)
                        .setMultiFactorSession(multiFactorSession.result)
                        .setPhoneNumber(Constants.COUNTRY_PREFIX + phoneNumber) // Phone number to verify
                        .setTimeout(OTP_WAIT_TIMEOUT_SEC, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(it) // Activity (for callback binding)
                        .setCallbacks(
                            OTPHandler(
                                sendOtpState,
                                inSignUpFlow
                            )
                        ) // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token) // callback's ForceResendingToken
                    Timber.i("PhoneAuthProvider.verifyPhoneNumber")
                    PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
                } ?: kotlin.run {
                    Timber.e("Activity is destroyed, can't resend OTP")
                    val appException = AppException(NULL_ACTIVITY)
                    sendOtpState.postValue(Resource.error(appException.message, null))
                }
            }
        } else {
            val task = multiFactorLoginTask
            if (task == null || token == null) {
                Timber.e("Task or token are null. Task: $task | Token: $token")
                val ex = AppException(MISSING_DATA)
                sendOtpState.postValue(Resource.error(ex.message))
                return sendOtpState
            }
            val e = task.exception as FirebaseAuthMultiFactorException
            val multiFactorResolver = e.resolver
            val selectedHint = multiFactorResolver.hints[0]

            // Send the SMS verification code.
            activityRef.get()?.let {
                Timber.i("PhoneAuthProvider.verifyPhoneNumber")
                PhoneAuthProvider.verifyPhoneNumber(
                    PhoneAuthOptions.newBuilder()
                        .setActivity(it)
                        .requireSmsValidation(true)
                        .setMultiFactorSession(multiFactorResolver.session)
                        .setMultiFactorHint(selectedHint as PhoneMultiFactorInfo)
                        .setCallbacks(OTPHandler(sendOtpState, inSignUpFlow))
                        .setTimeout(OTP_WAIT_TIMEOUT_SEC, TimeUnit.SECONDS)
                        .setForceResendingToken(token)
                        .build()
                )
            } ?: kotlin.run {
                Timber.e("Activity is destroyed, can't send OTP")
                val appException = AppException(NULL_ACTIVITY)
                sendOtpState.postValue(Resource.error(appException.message, null))
            }
        }
        return sendOtpState
    }

    // @HunterDebugImpl
    private fun linkAutoVerifiedPhoneAuthCredential(
        credential: PhoneAuthCredential,
        sendOtpState: MutableLiveData<Resource<VerificationState>>
    ) {
        Timber.i("linkAutoVerifiedPhoneAuthCredential")
        // we should have a valid user since we've done a email/password login already
        val user = firebaseHolder.auth.currentUser
        user?.linkWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d("linkAutoVerifiedPhoneAuthCredential:success")
                sendOtpState.postValue(Resource.success(VerificationState(true)))
            } else {
                Timber.w("linkAutoVerifiedPhoneAuthCredential:failure")
                Timber.w(task.exception)
                val exception = AppException(task.exception)
                sendOtpState.postValue(Resource.error(exception.message, null))
            }
        } ?: sendOtpState.postValue(Resource.error("Auth error", null))
    }

    /**
     * Register the Firebase user against the SAP backend.
     */
    @DelicateCoroutinesApi
    private fun registerUser(
        user: FirebaseUser,
        verifyOtpState: MutableLiveData<Resource<Unit>>
    ) {
        Timber.i("registerUser")
        GlobalScope.launch {
            try {
                val email = user.email
                if (email.isNullOrEmpty()) {
                    throw IllegalStateException("User email should have been registered at this point!")
                }
                val endPoint = DynamicBaseUrl.registerUser()
                val request = net.broachcutter.vendorapp.models.User(email.lowercase(), user.uid)
                val response = withContext(Dispatchers.IO) {
                    broachCutterApi.registerUserAsync(endPoint, request).await()
                }
                if (response.isSuccessful && response.body()?.isSuccessful() == true) {
                    verifyOtpState.postValue(Resource.success(null))
                    fetchAndSaveAuthToken(user)
                } else {
                    // roll back adding phone number in case the sap user registration fails
                    // This helps us make sure our sign up check is correct
                    Timber.e("User register call failed | ${response.code()} : ${response.message()}")
                    unregisterPhoneNumber()
                    val exception = AppException(USER_IS_NOT_REGISTERED)
                    verifyOtpState.postValue(Resource.error(exception.message, null, exception))
                }
            } catch (ex: Exception) {
                Timber.e(ex, "User register call had an exception")
                // roll back adding phone number in case the sap user registration fails
                // This helps us make sure our sign up check is correct
                unregisterPhoneNumber()
                val exception = AppException(USER_IS_NOT_REGISTERED)
                verifyOtpState.postValue(Resource.error(exception.message, null, exception))
            }
        }
    }

    private suspend fun unregisterPhoneNumber() {
        Timber.i("unregisterPhoneNumber")
        val user = firebaseHolder.auth.currentUser
        if (user?.multiFactor?.enrolledFactors?.isNotEmpty() == true) {
            val factor = user.multiFactor.enrolledFactors[0]
            try {
                user.multiFactor.unenroll(factor).await()
                Timber.i("User multi factor un-enrolled")
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
        signOut()
    }

    // @HunterDebugImpl
    @DelicateCoroutinesApi
    fun verifyOtp(otpCode: String, inSignUpFlow: Boolean): LiveData<Resource<Unit>> {
        Timber.i("verifyOtp, inSignUpFlow: $inSignUpFlow")
        val verifyOtpState: MutableLiveData<Resource<Unit>> = MutableLiveData()
        verifyOtpState.postValue(Resource.loading())
        val user = firebaseHolder.auth.currentUser
        storedVerificationId?.let { verificationId ->
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationId, otpCode)
            val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
            if (isValidOtp(otpCode)) {
                if (inSignUpFlow) {
                    user?.multiFactor?.enroll(
                        multiFactorAssertion,
                        "phoneNumber"
                    )?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Timber.i("Multi factor signup successful")
                            registerUser(user, verifyOtpState)
                        } else {
                            verifyOtpState.postValue(
                                task.exception?.message?.let { message ->
                                    Timber.i(message)
                                    Resource.error(message)
                                }
                            )
                        }
                    }
                } else {
                    multiFactorResolver
                        .resolveSignIn(multiFactorAssertion)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                fetchAndSaveAuthToken(task.result?.user!!)
                                verifyOtpState.postValue(Resource.success(Unit))
                            } else {
                                Timber.i("Otp Verification ${task.exception?.message}")
                                verifyOtpState.postValue(
                                    task.exception?.message?.let { message ->
                                        Resource.error(message)
                                    }
                                )
                            }
                        }
                }
            }
        } ?: run {
            Timber.e("Verification code not sent")
            verifyOtpState.postValue(
                Resource.error("Problem encounter while sending verification code, try to login after sometime")
            )
        }
        return verifyOtpState
    }

    private fun isValidOtp(otpCode: String): Boolean {
        return otpCode.isDigitsOnly() && otpCode.length == OTP_LENGTH
    }

    // @HunterDebugImpl
    private fun fetchAndSaveAuthToken(user: FirebaseUser) {
        Timber.i("fetchAndSaveAuthToken")
        user.getIdToken(false).addOnCompleteListener { task ->
            saveUserId(user.uid)
            val token = task.result?.token
            token?.let { saveToken(it) } ?: Timber.e("Token fetch failed")
        }
    }

    /**
     * Phone number being set on an account is the sign for successful sign up.
     */
    private fun isSignUpIncomplete(user: FirebaseUser) = user.multiFactor.enrolledFactors.isEmpty()

    private fun saveUserId(uid: String) {
        Timber.i("saveUserId")
        firebaseHolder.crashlytics.setUserId(uid)
        analytics.userSessionActive(uid)
        OneSignal.login(uid)

        val user = User()
        user.id = uid
        Sentry.setUser(user)
    }

    // @HunterDebugImpl
    fun checkUserLogin(isUserLoggedInCallback: (Boolean) -> Unit) {
        Timber.i("checkUserLogin")
        val user = firebaseHolder.auth.currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // we're logged in and have a token
                Timber.i("Logged in and have a token")
                saveUserId(user.uid)
                // check if the user has completed sign up
                if (isSignUpIncomplete(user)) {
                    Timber.i("User signup is incomplete")
                    isUserLoggedInCallback.invoke(false)
                    return@addOnCompleteListener
                }

                val token = task.result?.token
                token?.let { saveToken(it) } ?: Timber.e("Token fetch failed")
                // user is logged in, go to main
                isUserLoggedInCallback.invoke(true)
            } else {
                // firebase token retrieval failed for some reason, ask for a relog
                Timber.e(task.exception)
                isUserLoggedInCallback.invoke(false)
            }
        } ?: run {
            Timber.i("Firebase user is null, login required")
            isUserLoggedInCallback.invoke(false) // current user is null, ask for relog
        }
    }

    /**
     * @return [ResetPasswordCodes].
     */
    fun resetPassword(email: String): Observable<ResetPasswordCodes> {
        Timber.i("resetPassword")
        val codeSingle: PublishSubject<ResetPasswordCodes> = PublishSubject.create()
        if (!email.isValidEmail()) {
            codeSingle.onNext(ResetPasswordCodes.INVALID_EMAIL)
            codeSingle.onComplete()
        } else {
            val auth = firebaseHolder.auth
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    codeSingle.onNext(ResetPasswordCodes.SUCCESS)
                } else {
                    task.exception?.let { codeSingle.onError(it) }
                }
                codeSingle.onComplete()
            }
        }
        return codeSingle
    }

    /**
     * to enroll for multifactorialAuth
     */
    fun enrollMultiFactorAuth(
        phoneNumber: String,
        activityRef: WeakReference<Activity>
    ): LiveData<Resource<VerificationState>> {
        Timber.i("enrollMultiFactorAuth")
        val sendOtpState = MutableLiveData<Resource<VerificationState>>()
        sendOtpState.postValue(Resource.loading())
        val user = firebaseHolder.auth.currentUser
        multiFactorSessionTask = user?.multiFactor?.session

        if (multiFactorSessionTask == null) {
            Timber.e("MultiFactor Session is null.")
            val ex = AppException(NULL_MULTI_FACTOR_SESSION)
            sendOtpState.postValue(Resource.error(ex.message))
            return sendOtpState
        }
        multiFactorSessionTask?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                activityRef.get()?.let { activity ->
                    task.result?.let { multiFactorSession ->
                        val options = PhoneAuthOptions.newBuilder()
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(OTP_WAIT_TIMEOUT_SEC, TimeUnit.SECONDS)
                            .setMultiFactorSession(multiFactorSession)
                            .setCallbacks(OTPHandler(sendOtpState, true))
                            .requireSmsValidation(true)
                            .setActivity(activity)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    } ?: kotlin.run {
                        Timber.e("Activity is destroyed, can't send OTP")
                        val appException = AppException(NULL_ACTIVITY)
                        sendOtpState.postValue(Resource.error(appException.message, null))
                    }
                }
            } else {
                val appException = AppException(task.exception)
                sendOtpState.postValue(Resource.error(appException.message, null, appException))
            }
        }
        return sendOtpState
    }

    /**
     * For regular login.
     */
    private fun loginWithPhoneCredentials(
        credential: PhoneAuthCredential,
        loginListener: LoginListener
    ) {
        Timber.i("loginWithPhoneCredentials")
        loginListener.onLoginProcessing()
        // login with phone creds since we logged out the email/password session earlier
        val auth = firebaseHolder.auth
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("loginWithPhoneCredentials:success")
                fetchAndSaveAuthToken(auth.currentUser!!)
                loginListener.onLoginAttemptComplete(LoginCodes.SUCCESS)
            } else {
                Timber.w("loginWithPhoneCredentials:failure")
                Timber.w(task.exception)
                loginListener.onLoginAttemptComplete(getLoginError(task.exception))
            }
        }
    }

    // @HunterDebugImpl
    fun signOut() {
        Timber.i("signOut")
        firebaseHolder.auth.signOut()
        Sentry.configureScope { scope ->
            scope.user = null
        }
        clearToken()
        OneSignal.logout()
    }

    // @HunterDebugImpl
    private fun saveToken(token: String) {
        Timber.i("saveToken")
        sharedPreferences.edit {
            putString(Constants.PREFS_TOKEN, token)
            putString(Constants.EMAIL, firebaseHolder.auth.currentUser?.email)
        }
        authHeaderInterceptor.authToken = token
    }

    // @HunterDebugImpl
    private fun clearToken() {
        Timber.i("clearToken")
        sharedPreferences.edit(commit = true) {
            putString(Constants.PREFS_TOKEN, null)
        }
        authHeaderInterceptor.authToken = null
    }

    /**
     * Handles
     * 1. OTP verification during sign up
     * 2. Resent OTP verification during sign up
     * 3. Resent OTP verification during regular login
     *
     * todo consolidate this and [RegularLoginOtpHandler]
     */
    private inner class OTPHandler(
        val sendOtpState: MutableLiveData<Resource<VerificationState>>,
        val inSignUpFlow: Boolean
    ) :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Timber.i("onAutoVerificationCompleted")

            if (inSignUpFlow) {
                // link phone number to user when signing up
                linkAutoVerifiedPhoneAuthCredential(credential, sendOtpState)
            } else {
                sendOtpState.postValue(Resource.success(VerificationState(true)))
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Timber.w("onVerificationFailed: $e")

            when (e) {
                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                    Timber.i(e)
                    sendOtpState.postValue(Resource.error(AppException(e).message, null))
                }

                is FirebaseAuthRecentLoginRequiredException -> {
                    Timber.i(e)
                    val appException = AppException(e)
                    sendOtpState.postValue(Resource.error(appException.message, null, appException))
                }

                else -> {
                    Timber.w(e)
                    sendOtpState.postValue(Resource.error(AppException(e).message, null))
                }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Timber.d("onCodeSent: $verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token

            sendOtpState.postValue(Resource.success(VerificationState(false)))
        }
    }

    private inner class RegularLoginOtpHandler(private val loginListener: LoginListener) :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Timber.i("onAutoVerificationCompleted")
            loginWithPhoneCredentials(credential, loginListener)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Timber.w("onVerificationFailed: $e")

            when (e) {
                is FirebaseAuthInvalidCredentialsException -> loginListener.onLoginAttemptComplete(
                    LoginCodes.INVALID_PHONE_NUMBER
                )

                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                    Timber.i(e)
                    loginListener.onLoginAttemptComplete(LoginCodes.TOO_MANY_ATTEMPTS)
                }

                is FirebaseAuthException -> {
                    Timber.i(e)
                    loginListener.onLoginAttemptComplete(LoginCodes.DEVELOPER_ERROR)
                }

                is FirebaseAuthRecentLoginRequiredException -> {
                    Timber.i(e)
                    loginListener.onLoginAttemptComplete(LoginCodes.RELOGIN_REQUIRED)
                }

                else -> {
                    Timber.e(e)
                    loginListener.onLoginAttemptComplete(LoginCodes.UNKNOWN_ERROR)
                }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Timber.d("onCodeSent: $verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token

            // log in the user when they verify their number
            loginListener.onLoginAttemptComplete(LoginCodes.OTP_SENT, phoneNumber)
        }
    }

//    private fun removeExternalUserId() {
//        OneSignal.removeExternalUserId(object : OSExternalUserIdUpdateCompletionHandler {
//            override fun onSuccess(results: JSONObject) {
//                // The results will contain push and email success statuses
//                OneSignal.onesignalLog(
//                    OneSignal.LOG_LEVEL.VERBOSE,
//                    "Remove external user id done with results: $results"
//                )
//                Timber.i("Remove external user id done with results: $results")
//                // Push can be expected in almost every situation with a success status, but
//                // as a pre-caution its good to verify it exists
//                if (results.has("push") && results.getJSONObject("push").has("success")) {
//                    val isPushSuccess = results.getJSONObject("push").getBoolean("success")
//                    OneSignal.onesignalLog(
//                        OneSignal.LOG_LEVEL.VERBOSE,
//                        "Remove external user id for push status: $isPushSuccess"
//                    )
//                    Timber.i("Remove external user id for push status: $isPushSuccess")
//                }
//            }
//
//            override fun onFailure(error: OneSignal.ExternalIdError?) {
//                OneSignal.onesignalLog(
//                    OneSignal.LOG_LEVEL.VERBOSE,
//                    "remove external user id done with error: $error"
//                )
//                Timber.e("remove external user id done with error: $error")
//            }
//        })
//    }

//    private fun setExternalUserId(uid: String) {
//        OneSignal.setExternalUserId(
//            uid,
//            object : OSExternalUserIdUpdateCompletionHandler {
//                override fun onSuccess(results: JSONObject) {
//                    Timber.i("setExternalUserId onSuccess $results")
//                    try {
//                        if (results.has("push") && results.getJSONObject("push").has("success")) {
//                            val isPushSuccess = results.getJSONObject("push").getBoolean("success")
//                            OneSignal.onesignalLog(
//                                OneSignal.LOG_LEVEL.VERBOSE,
//                                "Set external user id for push status: $isPushSuccess"
//                            )
//                            Timber.i("Set external user id for push status: $isPushSuccess")
//                        } else {
//                            Timber.e("Onesignal setExternalId push failed for $uid")
//                        }
//                    } catch (e: JSONException) {
//                        Timber.e(e, "external user id response parse error")
//                    }
//                }
//
//                override fun onFailure(error: OneSignal.ExternalIdError) {
//                    /** The results will contain channel failure statuses
//                    Use this to detect if external_user_id was not set and retry
//                    when a better network connection is made **/
//                    OneSignal.onesignalLog(
//                        OneSignal.LOG_LEVEL.ERROR,
//                        "Set external user id done with error: $error"
//                    )
//                    Timber.e("Set external user id done with error: $error")
//                }
//            }
//        )
//    }

    interface LoginListener {
        fun onLoginProcessing()

        fun onLoginAttemptComplete(loginCode: LoginCodes, phoneNumber: String? = null)
    }
}
