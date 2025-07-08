package net.broachcutter.vendorapp.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.valartech.commons.network.google.Resource
import com.valartech.commons.network.google.Status
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.SearchResults
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat

fun TextView.setRedBlueTextGradient() {
    // todo doesn't work
    val tomato = ContextCompat.getColor(context, R.color.tomato)
    val twilightBlue = ContextCompat.getColor(context, R.color.twilight_blue)
    val width = this.paint.measureText(this.text.toString())
    val textShader = LinearGradient(
        0f, 0f, width, this.textSize,
        intArrayOf(tomato, twilightBlue),
        null, Shader.TileMode.CLAMP
    )
    this.paint.shader = textShader
}

fun AssetManager.getProximaNovaRegTypeface(): Typeface {
    return Typeface.createFromAsset(
        this,
        "fonts/proxima_nova_alt_regular_file.ttf"
    )
}

/**
 * For convenient mapping of our raw response type to our UI type [SearchResults].
 */
fun LiveData<Resource<List<Product>>>.mapToSearchResults(): LiveData<Resource<SearchResults>> {
    return map {
        when (it.status) {
            Status.SUCCESS -> Resource.success(SearchResults(results = it.data))
            Status.ERROR -> Resource.error(
                msg = it.message!!,
                throwable = it.throwable,
                retrofitResponse = it.retrofitResponse
            )
            Status.LOADING -> Resource.loading()
        }
    }
}

/**
 * Unwrapping [BCResponse]
 */
fun <T> LiveData<Resource<BCResponse<T>>>.unwrap(): LiveData<Resource<T>> {
    return map {
        when (it.status) {
            Status.SUCCESS -> Resource.success(it.data?.data)
            Status.ERROR -> Resource.error(
                msg = it.message!!,
                throwable = it.throwable,
                retrofitResponse = it.retrofitResponse
            )
            Status.LOADING -> Resource.loading()
        }
    }
}

fun Double.inr(): String {
    return "Rs. ${DecimalFormat("##,##,##0").format(this)}"
}

/**
 * For avoiding accidental double-clicks.
 */
fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun String.isValidJson(): Boolean {
    try {
        JSONObject(this)
    } catch (ex: JSONException) {
        return false
    }
    return true
}

val String.domain: String?
    get() {
        val index = this.indexOf('@')
        return if (index == -1) null else this.substring(index + 1)
    }

@Suppress("ReturnCount", "NestedBlockDepth")
fun Context.isInternetConnected(): Boolean {
    var result = false
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return result
}
