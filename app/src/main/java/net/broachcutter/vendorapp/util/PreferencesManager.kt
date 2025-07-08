package net.broachcutter.vendorapp.util

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Wrapper class on top of SharedPreferences to make retrieval with defaults easier.
 */
class PreferencesManager @Inject constructor(private val sharedPreferences: SharedPreferences) {
    val email: String? = sharedPreferences.getString(Constants.EMAIL, "")

    fun couponsEnabled(): Boolean {
        return if (email?.domain == "mailinator.com" ||
            email?.domain == "valartech.com" ||
            email?.domain == "broachcutter.net"
        ) {
            true
        } else {
            sharedPreferences.getBoolean(SharedPreferenceKeys.COUPONS_ENABLED, false)
        }
    }
}
