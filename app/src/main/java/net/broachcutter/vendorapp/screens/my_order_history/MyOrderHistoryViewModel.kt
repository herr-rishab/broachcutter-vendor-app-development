package net.broachcutter.vendorapp.screens.my_order_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.snakydesign.livedataextensions.liveDataOf
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class MyOrderHistoryViewModel @Inject constructor(
    private val router: Router,
    userRepository: UserRepository
) : ViewModel() {

    /**
     * Month, year, force refresh inputs
     */
    private val monthYearInput = MutableLiveData<Triple<Int, Int, Boolean>>()
    val ordersUiModel = monthYearInput.switchMap { orderHistoryRequest ->
        userRepository.getOrderHistoryUpdated(
            orderHistoryRequest.first,
            orderHistoryRequest.second,
            orderHistoryRequest.third,
            viewModelScope
        )
    }

    val pendingOrders: LiveData<ArrayList<UpdatedOrder>> =
        ordersUiModel.switchMap { response ->
            val orders: ArrayList<UpdatedOrder> = ArrayList()
            response.data?.updatedOrder?.forEach { updatedOrder ->
                if (updatedOrder.orderStatus == UpdatedOrderStatus.PENDING) {
                    orders.add(updatedOrder)
                }
            }
            liveDataOf(orders)
        }

    val awaitingPaymentOrders: LiveData<ArrayList<UpdatedOrder>> =
        ordersUiModel.switchMap { response ->
            val orders: ArrayList<UpdatedOrder> = ArrayList()
            response.data?.updatedOrder?.forEach { updatedOrder ->
                if (updatedOrder.orderStatus == UpdatedOrderStatus.AWAITING_PAYMENT) {
                    orders.add(updatedOrder)
                }
            }
            liveDataOf(orders)
        }

    val processingOrders: LiveData<ArrayList<UpdatedOrder>> =
        ordersUiModel.switchMap { response ->
            val orders: ArrayList<UpdatedOrder> = ArrayList()
            response.data?.updatedOrder?.forEach { updatedOrder ->
                if (updatedOrder.orderStatus == UpdatedOrderStatus.PROCESSING) {
                    orders.add(updatedOrder)
                }
            }
            liveDataOf(orders)
        }

    val awaitingDispatchOrders: LiveData<ArrayList<UpdatedOrder>> =
        ordersUiModel.switchMap { response ->
            val orders: ArrayList<UpdatedOrder> = ArrayList()
            response.data?.updatedOrder?.forEach { updatedOrder ->
                if (updatedOrder.orderStatus == UpdatedOrderStatus.AWAITING_DISPATCH) {
                    orders.add(updatedOrder)
                }
            }
            liveDataOf(orders)
        }

    val dispatchedOrders: LiveData<ArrayList<UpdatedOrder>> =
        ordersUiModel.switchMap { response ->
            val orders: ArrayList<UpdatedOrder> = ArrayList()
            response.data?.updatedOrder?.forEach { updatedOrder ->
                if (updatedOrder.orderStatus == UpdatedOrderStatus.DISPATCHED) {
                    orders.add(updatedOrder)
                }
            }
            liveDataOf(orders)
        }

    val cancelledOrders: LiveData<ArrayList<UpdatedOrder>> =
        ordersUiModel.switchMap { response ->
            val orders: ArrayList<UpdatedOrder> = ArrayList()
            response.data?.updatedOrder?.forEach { updatedOrder ->
                if (updatedOrder.orderStatus == UpdatedOrderStatus.CANCELLED) {
                    orders.add(updatedOrder)
                }
            }
            liveDataOf(orders)
        }

    fun onDateSet(month: Int, year: Int, forceRefresh: Boolean = false) {
        monthYearInput.postValue(Triple(month, year, forceRefresh))
    }

    fun onOrderSelected(order: UpdatedOrder) {
        router.navigateTo(Screens.OrderHistoryDetail(order))
    }
}
