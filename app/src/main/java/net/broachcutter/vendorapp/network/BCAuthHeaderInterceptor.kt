package net.broachcutter.vendorapp.network

import android.content.SharedPreferences
import net.broachcutter.vendorapp.util.Constants
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * https://stackoverflow.com/questions/43051558/dagger-retrofit-adding-auth-headers-at-runtime/43083639#43083639
 */
@Singleton
class BCAuthHeaderInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {

    var authToken: String? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        loadAuthToken() // load token from shared prefs if it's been cleared out of memory

        val request = chain.request()
        val requestBuilder = request.newBuilder()
        authToken?.let {
            requestBuilder.addHeader(Constants.AUTH_HEADER_KEY, it)
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun loadAuthToken() {
        if (authToken == null) {
            authToken = sharedPreferences.getString(Constants.PREFS_TOKEN, null)
        }
    }
}
