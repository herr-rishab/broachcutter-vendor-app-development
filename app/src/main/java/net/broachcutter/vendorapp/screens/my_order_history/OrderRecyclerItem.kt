package net.broachcutter.vendorapp.screens.my_order_history

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.OrderSummaryCardContentBinding
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.UpdatedOrderItem
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.util.inr
import org.threeten.bp.format.DateTimeFormatter

class OrderRecyclerItem(
    private val order: UpdatedOrder,
    private val listener: (order: UpdatedOrder) -> Unit
) : BindableItem<OrderSummaryCardContentBinding>() {

    @Suppress("LongMethod")
    override fun bind(viewBinding: OrderSummaryCardContentBinding, position: Int) {
        val context = viewBinding.root.context
        viewBinding.orderNumber.text = context.getString(R.string.order_num, order.orderId)
        viewBinding.orderDate.text = context.getString(
            R.string.order_placed_on,
            DateTimeFormatter.ofPattern("dd/MM/yyyy").format(order.orderDate)
        )

        viewBinding.orderAmount.text = order.total?.inr()
        viewBinding.orderDesc.text = getDescText(context, order.product)
        when (order.orderStatus) {
            UpdatedOrderStatus.PENDING -> {
                viewBinding.orderStatus.text = context.getString(
                    R.string.pending_footer, order.product.size
                )
                viewBinding.footerBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.pending_order_footer)
            }

            UpdatedOrderStatus.AWAITING_PAYMENT -> {
                viewBinding.orderStatus.text = context.getString(
                    R.string.awaiting_payment_footer, order.product.size
                )
                viewBinding.footerBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.awaiting_payment_order_footer)
            }

            UpdatedOrderStatus.PROCESSING -> {
                viewBinding.orderStatus.text =
                    context.getString(R.string.processing_footer, order.product.size)
                viewBinding.footerBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.processing_order_footer)
            }

            UpdatedOrderStatus.AWAITING_DISPATCH -> {
                viewBinding.orderStatus.text = context.getString(
                    R.string.awaiting_dispatch_footer, order.product.size
                )
                viewBinding.footerBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.awaiting_dispatch_order_footer)
            }

            UpdatedOrderStatus.DISPATCHED -> {
                order.dispatchDate?.let {
                    viewBinding.orderStatus.text = context.getString(
                        R.string.order_dispatched_on,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy").format(order.dispatchDate)
                    )
                }
                viewBinding.orderStatus.text = context.getString(
                    R.string.dispatched_footer, order.product.size
                )
                viewBinding.footerBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.dispatched_order_footer)
            }

            UpdatedOrderStatus.CANCELLED -> {
                viewBinding.orderStatus.text = context.getString(
                    R.string.cancelled_footer, order.product.size
                )
                viewBinding.footerBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.cancelled_order_footer)
            }

            else -> {}
        }
        viewBinding.root.setOnClickListener {
            listener(order)
        }
    }

    override fun getLayout(): Int = R.layout.order_summary_card_content

    override fun initializeViewBinding(view: View): OrderSummaryCardContentBinding {
        return OrderSummaryCardContentBinding.bind(view)
    }

    private fun getDescText(context: Context, orderItems: List<UpdatedOrderItem>?): CharSequence {
        var machineCount = 0
        var cutterCount = 0
        var spareCount = 0
        var accessoryCount = 0
        orderItems?.forEach {
            when (it.item?.productType) {
                ProductType.MACHINE -> machineCount++
                ProductType.CUTTER -> cutterCount++
                ProductType.SPARE -> spareCount++
                ProductType.ACCESSORY -> accessoryCount++
                ProductType.ARBOR -> accessoryCount++
                ProductType.ADAPTOR -> accessoryCount++
                else -> {
                }
            }
        }
        val builder = StringBuilder()
        // machine
        if (machineCount > 0) {
            builder.append(
                context.resources?.getQuantityString(
                    R.plurals.machinePlural,
                    machineCount,
                    machineCount
                )
            )
            appendCommaOrAnd(context, builder, cutterCount, spareCount, accessoryCount)
        }

        // cutter
        if (cutterCount > 0) {
            builder.append(
                context.resources?.getQuantityString(
                    R.plurals.cutterPlural,
                    cutterCount,
                    cutterCount
                )
            )
            appendCommaOrAnd(context, builder, spareCount, accessoryCount)
        }
        // spare
        if (spareCount > 0) {
            builder.append(
                context.resources?.getQuantityString(
                    R.plurals.sparePlural,
                    spareCount,
                    spareCount
                )
            )
            appendCommaOrAnd(context, builder, accessoryCount)
        }
        // accessory
        if (accessoryCount > 0) {
            builder.append(
                context.resources?.getQuantityString(
                    R.plurals.accessoryPlural,
                    accessoryCount,
                    accessoryCount
                )
            )
        }
        return builder.toString()
    }

    private fun appendCommaOrAnd(context: Context, builder: StringBuilder, vararg counts: Int) {
        val greaterThan0Count = greaterThan0(counts)
        if (greaterThan0Count >= 2) {
            builder.append(context.getString(R.string.comma_sp))
        } else if (greaterThan0Count == 1) {
            builder.append(context.getString(R.string.sp_and_sp))
        }
    }

    private fun greaterThan0(counts: IntArray): Int {
        var greaterThan0Count = 0
        counts.forEach {
            if (it > 0) {
                greaterThan0Count++
            }
        }
        return greaterThan0Count
    }
}
