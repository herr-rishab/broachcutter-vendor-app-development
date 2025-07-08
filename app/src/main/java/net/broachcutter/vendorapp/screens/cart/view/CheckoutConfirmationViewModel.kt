package net.broachcutter.vendorapp.screens.cart.view

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.android.play.core.review.ReviewManagerFactory
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse
import timber.log.Timber
import javax.inject.Inject

class CheckoutConfirmationViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {
    val pendingOrderList = arrayListOf<PlaceOrderResponse.SuccessfulOrder>()
    val awaitingPaymentOrderList = arrayListOf<PlaceOrderResponse.SuccessfulOrder>()
    val processingOrderList = arrayListOf<PlaceOrderResponse.SuccessfulOrder>()
    val failedOrderList = arrayListOf<PlaceOrderResponse.FailedOrder.Items>()
    lateinit var failedMessage: String

    private val manager = ReviewManagerFactory.create(app)

    fun separateOrders(placeOrderResponse: PlaceOrderResponse, requireActivity: FragmentActivity) {

        /**
         * Separating out Confirmed,Pending Order and Failed Order
         */
        for (order in placeOrderResponse.successfulOrder) {
            when (order.status) {
                UpdatedOrderStatus.PENDING.name -> pendingOrderList.add(order)
                UpdatedOrderStatus.AWAITING_PAYMENT.name -> awaitingPaymentOrderList.add(order)
                UpdatedOrderStatus.PROCESSING.name -> processingOrderList.add(order)
            }
        }

        for (order in placeOrderResponse.failedOrder.items) {
            failedOrderList.add(order)
        }

        failedMessage = placeOrderResponse.failedOrder.message

        showInAppReviewDialog(requireActivity)
    }

    private fun showInAppReviewDialog(requireActivity: FragmentActivity) {
        // call to cache reviewInfo object
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                // We got the ReviewInfo object
                Timber.i("reviewInfo object ready")
                val reviewInfo = result.result
                val flow = manager.launchReviewFlow(requireActivity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    Timber.i("showAppInReview success")
                }
            } else {
                // There was some problem, continue regardless of the result.
                Timber.e(result.exception)
            }
        }
    }
}
