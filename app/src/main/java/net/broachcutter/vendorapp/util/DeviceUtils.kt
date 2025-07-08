package net.broachcutter.vendorapp.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import com.valartech.commons.utils.extensions.getStatusBarHeight
import timber.log.Timber
import java.util.*

/**
 * Returns the notch height if one is present, or the status height, in pixels.
 */
fun getStatusBarOrNotchHeight(activity: Activity?, resources: Resources): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val safeInsetTop =
            activity?.window?.decorView?.rootWindowInsets?.displayCutout?.safeInsetTop
        safeInsetTop?.let {
            return it
        } ?: resources.getStatusBarHeight()
    } else resources.getStatusBarHeight()
}

/**
 * Note that this doesn't work for emulators.
 *
 * https://stackoverflow.com/a/29938139/3460025
 */
fun getNavBarHeight(context: Context): Int {
    val result = 0
    val hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

    if (!hasMenuKey && !hasBackKey) {
        // The device has a navigation bar
        val resources = context.resources

        val orientation = resources.configuration.orientation
        val resourceId: Int
        resourceId = if (isTablet(context)) {
            resources.getIdentifier(
                if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height"
                else "navigation_bar_height_landscape",
                "dimen",
                "android"
            )
        } else {
            resources.getIdentifier(
                if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height"
                else "navigation_bar_width",
                "dimen",
                "android"
            )
        }

        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
    }
    return result
}

/**
 * Returns whether this device is a tablet or not.
 */
fun isTablet(context: Context): Boolean {
    return context.resources.configuration.screenLayout and
        Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

/**
 * Derived from https://github.com/simon-heinen/SimpleUi/blob/master/SimpleUI/srcAndroid/simpleui/util/DeviceInformation.java
 */
fun getDeviceDebugInfo(a: Activity): String {
    var s = ""
    try {
        val pInfo = a.packageManager.getPackageInfo(
            a.packageName, PackageManager.GET_META_DATA
        )
        s += "APP Package Name: ${a.packageName} "
        s += "APP Version Name: ${pInfo.versionName} "
        s += "APP Version Code: ${pInfo.versionCode} "
        s += "\n"
    } catch (e: PackageManager.NameNotFoundException) {
        Timber.w(e)
    }
    s += "OS Version: ${System.getProperty("os.version")} (${Build.VERSION.INCREMENTAL}) \n"
    s += "OS API Level: ${Build.VERSION.SDK_INT} \n"
    s += "Device: ${Build.DEVICE} \n"
    s += "Model and Product: ${Build.MODEL} (${Build.PRODUCT}) \n"
    // more from
    // http://developer.android.com/reference/android/os/Build.html :
    s += "Manufacturer: ${Build.MANUFACTURER} \n"
    s += "Other TAGS: ${Build.TAGS} \n"
    s += ("\n screenWidth: ${a.window.windowManager.defaultDisplay.width}")
    s += ("\n screenHeigth: ${a.window.windowManager.defaultDisplay.height}")
    return s
}
