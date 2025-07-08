package net.broachcutter.vendorapp.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.ItemFailedOrderBinding
import net.broachcutter.vendorapp.databinding.ItemFailedOrderHeaderBinding
import net.broachcutter.vendorapp.databinding.ItemSuccessOrderBinding
import net.broachcutter.vendorapp.databinding.ItemSuccessOrderHeaderBinding
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse
import net.broachcutter.vendorapp.util.inr

class SuccessOrderItem(
    private val itemList: List<PlaceOrderResponse.SuccessfulOrder.OrderItem>,
    private val context: Context,
    private val orderNumber: String,
    private val total: Double
) : BindableItem<ItemSuccessOrderBinding>() {

    override fun bind(viewHolder: ItemSuccessOrderBinding, position: Int) {
        viewHolder.tvOrder.text = "Order #$orderNumber"
        viewHolder.tvTotal.text = "${total.inr()}"
        val inflater = LayoutInflater.from(context)
        viewHolder.llItem.removeAllViews()
        itemList.forEachIndexed { index, item ->
            val customLayout: View = inflater.inflate(R.layout.item_order, null, false)

            val rootLayout: ConstraintLayout = customLayout.findViewById(R.id.rootLayout)

            if (index % 2 == 0) {
                rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white_two))
            }

            val tvProductName: TextView = customLayout.findViewById(R.id.tvProductName)
            val tvQuantity: TextView = customLayout.findViewById(R.id.tvQuantity)
            tvProductName.text = item.item.name
            tvQuantity.text = "${context.getString(R.string.quantity)} : ${item.totalQuantity}"
            viewHolder.llItem.addView(customLayout)
        }
    }

    override fun getLayout() = R.layout.item_success_order
    override fun initializeViewBinding(view: View): ItemSuccessOrderBinding {
        return ItemSuccessOrderBinding.bind(view)
    }
}

class SuccessOrderHeader(
    private val title: String,
    private val listSize: Int,
    private val orderType: UpdatedOrderStatus
) : BindableItem<ItemSuccessOrderHeaderBinding>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(binding: ItemSuccessOrderHeaderBinding, position: Int) {
        binding.tvTitle.text = "$title($listSize)"

        when (orderType) {
            UpdatedOrderStatus.PENDING -> {
                binding.ivExpandableIcon.apply {
                    setImageResource(R.drawable.ic_up_arrow_grey)
                }
            }

            UpdatedOrderStatus.AWAITING_PAYMENT -> {
                binding.ivExpandableIcon.apply {
                    setImageResource(R.drawable.ic_up_arrow_grey)
                }
            }

            UpdatedOrderStatus.PROCESSING -> {
                binding.ivExpandableIcon.apply {
                    setImageResource(R.drawable.ic_up_arrow_grey)
                }
            }

            else -> {}
        }

        binding.rootLayoutSuccessOrder.setOnClickListener {
            expandableGroup.onToggleExpanded()
            when (orderType) {
                UpdatedOrderStatus.PENDING -> {
                    setupDropDownIcon(
                        binding,
                        R.drawable.ic_up_arrow_grey,
                        R.drawable.ic_down_arrow_grey
                    )
                }

                UpdatedOrderStatus.AWAITING_PAYMENT -> {
                    setupDropDownIcon(
                        binding,
                        R.drawable.ic_up_arrow_grey,
                        R.drawable.ic_down_arrow_grey
                    )
                }

                UpdatedOrderStatus.PROCESSING -> {
                    setupDropDownIcon(
                        binding,
                        R.drawable.ic_up_arrow_grey,
                        R.drawable.ic_down_arrow_grey
                    )
                }

                else -> {}
            }
        }
    }

    private fun setupDropDownIcon(
        viewHolder: ItemSuccessOrderHeaderBinding,
        icUpArrow: Int,
        icDownArrow: Int
    ) {
        viewHolder.ivExpandableIcon.apply {
            setImageResource(
                if (expandableGroup.isExpanded)
                    icUpArrow
                else
                    icDownArrow
            )
        }
    }

    override fun getLayout() = R.layout.item_success_order_header
    override fun initializeViewBinding(view: View): ItemSuccessOrderHeaderBinding {
        return ItemSuccessOrderHeaderBinding.bind(view)
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }
}

class FailedOrderHeader(
    private val title: String,
    private val message: String,
    private val listSize: Int
) : BindableItem<ItemFailedOrderHeaderBinding>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ItemFailedOrderHeaderBinding, position: Int) {
        viewHolder.tvTitle.text = "$title($listSize)"
        viewHolder.tvMessage.text = message

        viewHolder.rootLayoutFailedOrder.setOnClickListener {
            expandableGroup.onToggleExpanded()
            setupDropDownIcon(viewHolder)
        }
    }

    private fun setupDropDownIcon(viewHolder: ItemFailedOrderHeaderBinding) {
        viewHolder.ivExpandableIcon.apply {
            setImageResource(
                if (expandableGroup.isExpanded) R.drawable.ic_up_arrow_red
                else R.drawable.ic_down_arrow_red
            )
        }
    }

    override fun getLayout() = R.layout.item_failed_order_header
    override fun initializeViewBinding(view: View): ItemFailedOrderHeaderBinding {
        return ItemFailedOrderHeaderBinding.bind(view)
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }
}

class FailedOrderItem(
    private val cartItemList: List<PlaceOrderResponse.FailedOrder.Items>,
    private val context: Context
) : BindableItem<ItemFailedOrderBinding>() {

    override fun bind(viewHolder: ItemFailedOrderBinding, position: Int) {
        val inflater = LayoutInflater.from(context)
        viewHolder.llFailedItem.removeAllViews()

        cartItemList.forEachIndexed { index, item ->
            val customLayout: View = inflater.inflate(R.layout.item_order, null, false)

            val rootLayout: ConstraintLayout = customLayout.findViewById(R.id.rootLayout)

            if (index % 2 == 0) {
                rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white_two))
            }

            val tvProductName: TextView = customLayout.findViewById(R.id.tvProductName)
            val tvQuantity: TextView = customLayout.findViewById(R.id.tvQuantity)
            tvProductName.text = item.cartItem.name
            tvQuantity.text = context.getString(R.string.quantity_no, item.cartItem.cartQuantity)
            viewHolder.llFailedItem.addView(customLayout)
        }
    }

    override fun getLayout() = R.layout.item_failed_order
    override fun initializeViewBinding(view: View): ItemFailedOrderBinding {
        return ItemFailedOrderBinding.bind(view)
    }
}
