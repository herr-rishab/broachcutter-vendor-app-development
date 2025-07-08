package net.broachcutter.vendorapp.util
//
// import android.content.Intent
// import com.google.gson.Gson
// import com.google.gson.GsonBuilder
// import com.onesignal.OSNotificationAction
// import com.onesignal.OSNotificationOpenedResult
// import com.onesignal.OneSignal
// import net.broachcutter.vendorapp.DealerApplication
// import net.broachcutter.vendorapp.models.coupon.Coupon
// import net.broachcutter.vendorapp.network.ZonedDateTimeTypeAdapter
// import net.broachcutter.vendorapp.screens.splash.SplashActivity
// import org.threeten.bp.ZonedDateTime
// import timber.log.Timber
//
// class NotificationOpenedHandler : OneSignal.OSNotificationOpenedHandler {
//
//    override fun notificationOpened(result: OSNotificationOpenedResult?) {
//        val actionId = result!!.action.actionId
//        val type: OSNotificationAction.ActionType = result.action.type // "ActionTaken" | "Opened"
//        val title = result.notification.title
//        val launchUrl: String? = result.notification.launchURL
//
//        val gsonBuilder =
//            GsonBuilder().registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeTypeAdapter())
//        val gson: Gson = gsonBuilder.create()
//        Timber.i("NotificationOpenedHandler $actionId $type $title $launchUrl")
//        if (launchUrl?.isNotEmpty() == true) {
//            if (launchUrl == DeepLinkUrl.COUPON_DETAILS || actionId == Constants.VIEW_DETAILS) {
//                goToCouponDetails(launchUrl, gson, result)
//            } else {
//                Timber.e("DeepLink Data is empty or null : $launchUrl")
//            }
//        } else {
//            Timber.i("Link is not exist : $launchUrl")
//        }
//    }
//
//    private fun goToCouponDetails(
//        launchUrl: String,
//        gson: Gson,
//        result: OSNotificationOpenedResult
//    ) {
//        if (launchUrl == DeepLinkUrl.COUPON_DETAILS) {
//            val rawCouponString = result.notification.additionalData.toString()
//            Timber.i("Raw Coupon data received: $rawCouponString")
//            val couponData: Coupon =
//                gson.fromJson(rawCouponString, Coupon::class.java)
//            val intent = Intent(DealerApplication.INSTANCE, SplashActivity::class.java)
//            intent.flags =
//                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.putExtra(Constants.COUPON, couponData)
//            DealerApplication.INSTANCE.startActivity(intent)
//        } else {
//            Timber.e("DeepLink Data is empty or null : $launchUrl")
//        }
//    }
// }
