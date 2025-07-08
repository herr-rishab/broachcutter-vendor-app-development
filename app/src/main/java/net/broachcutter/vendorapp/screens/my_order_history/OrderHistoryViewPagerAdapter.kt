package net.broachcutter.vendorapp.screens.my_order_history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.broachcutter.vendorapp.models.UpdatedOrderStatus

class OrderHistoryViewPagerAdapter(fragment: FragmentActivity) :
    FragmentStateAdapter(fragment) {
    companion object {
        const val TABS_COUNT = 6
    }

    override fun getItemCount(): Int {
        return TABS_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        lateinit var fragment: Fragment
        when (position) {
            UpdatedOrderStatus.PENDING.position -> {
                fragment = OrderFragment.getInstance(UpdatedOrderStatus.PENDING)
            }
            UpdatedOrderStatus.AWAITING_PAYMENT.position -> {
                fragment = OrderFragment.getInstance(UpdatedOrderStatus.AWAITING_PAYMENT)
            }
            UpdatedOrderStatus.PROCESSING.position -> {
                fragment = OrderFragment.getInstance(UpdatedOrderStatus.PROCESSING)
            }
            UpdatedOrderStatus.AWAITING_DISPATCH.position -> {
                fragment = OrderFragment.getInstance(UpdatedOrderStatus.AWAITING_DISPATCH)
            }
            UpdatedOrderStatus.DISPATCHED.position -> {
                fragment = OrderFragment.getInstance(UpdatedOrderStatus.DISPATCHED)
            }
            UpdatedOrderStatus.CANCELLED.position -> {
                fragment = OrderFragment.getInstance(UpdatedOrderStatus.CANCELLED)
            }
        }
        return fragment
    }
}
