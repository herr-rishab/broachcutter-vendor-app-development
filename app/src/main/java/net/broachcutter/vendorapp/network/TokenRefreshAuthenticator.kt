package net.broachcutter.vendorapp.network

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import com.valartech.commons.utils.extensions.longToast
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.util.Constants
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reference :- https://www.lordcodes.com/articles/authorization-of-web-requests-for-okhttp-and-retrofit
 */
@Singleton
class TokenRefreshAuthenticator @Inject constructor(
    val firebaseHolder: FirebaseHolder,
    val sharedPreferences: SharedPreferences,
    val context: Application,
    val router: Router,
    val authHeaderInterceptor: BCAuthHeaderInterceptor
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? = when {
        response.retryCount > 1 -> response.handleUnauthorizedRequest()
        else -> response.createRequest()
    }

    private fun Response.createRequest(): Request? {
        try {
            return runBlocking {
                Timber.i("retry runBlocking")
                val accessToken = fetchAndSaveToken()
                return@runBlocking request.signWithToken(accessToken)
            }
        } catch (error: Throwable) {
            Timber.e(error, "Failed to refresh request")
            return null
        }
    }

    /**
     * return UnAuthenticator request after retrying more than 1 times
     */
    private fun Response.handleUnauthorizedRequest(): Request {
        return if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            clearToken() // don't let the user use the unauthorised token
            Handler(Looper.getMainLooper()).post {
                context.longToast(context.getString(R.string.please_relog))
                router.newRootScreen(Screens.Login())
            }
            request
        } else
            request
    }

    private fun clearToken() {
        sharedPreferences.edit(commit = true) {
            putString(Constants.PREFS_TOKEN, null)
        }
        authHeaderInterceptor.authToken = null
    }

    private suspend fun fetchAndSaveToken(): String {
        return try {
            firebaseHolder.auth.currentUser?.let { firebaseUser ->
                val result = firebaseUser.getIdToken(true).await()
                val token = result.token.toString()
                Timber.i("retry new token $token")
                saveToken(token)
                token
            } ?: ""
        } catch (e: Exception) {
            Timber.e(e, "fetchAndSaveToken")
            ""
        }
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit(true) {
            putString(Constants.PREFS_TOKEN, token)
        }
    }
}

fun Request.signWithToken(accessToken: String): Request {
    val newRequest = newBuilder().header(Constants.AUTH_HEADER_KEY, accessToken).build()
    Timber.i("retry newRequest $newRequest")
    return newRequest
}

private val Response.retryCount: Int
    get() {
        var currentResponse = priorResponse
        var result = 0
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
