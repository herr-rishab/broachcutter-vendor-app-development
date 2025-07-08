package net.broachcutter.vendorapp.screens.order_history.detail

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.valartech.commons.utils.extensions.dpToPx
import com.valartech.commons.utils.extensions.formatINRwithPrefix
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.OrderDetailCardCollapsedBinding
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.UpdatedOrderItem
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class OrderItemCardView : CardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        const val cardRadiusDp = 5f
        const val cardElevationDp = 4f
    }

    private var orderItem: UpdatedOrderItem? = null
    private var orderItemListener: OrderItemListener? = null
    private val binding: OrderDetailCardCollapsedBinding

    init {
        binding = OrderDetailCardCollapsedBinding.inflate(LayoutInflater.from(context), this, true)
        binding.collapsedConstraintParent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        radius = cardRadiusDp.dpToPx()
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.white_two))
        cardElevation = cardElevationDp.dpToPx()
    }

    @Suppress("LongMethod")
    fun bind(
        orderItem: UpdatedOrderItem,
        orderStatus: UpdatedOrderStatus?,
        dispatched: ZonedDateTime?,
        orderItemListener: OrderItemListener,
        updatedOrder: UpdatedOrder
    ) {
        this.orderItemListener = orderItemListener
        this.orderItem = orderItem
        orderItem.run {
            val titleText = context.getString(R.string.name_quant, item?.name, totalQuantity)
            binding.orderItemTitle.text = titleText

            val amountText = itemSubtotal?.formatINRwithPrefix(context)
            binding.orderItemAmount.text = amountText
            when (orderStatus) {
                UpdatedOrderStatus.PENDING -> {
                    binding.imgCopy.visibility = GONE
                    binding.tvTracking.visibility = GONE
                    binding.orderItemSubtitle.text =
                        context.getString(R.string.pending_orders_no, totalQuantity)
                }

                UpdatedOrderStatus.AWAITING_PAYMENT -> {
                    binding.imgCopy.visibility = GONE
                    binding.tvTracking.visibility = GONE
                    binding.orderItemSubtitle.text =
                        context.getString(R.string.awaiting_payment_no, totalQuantity)
                }

                UpdatedOrderStatus.AWAITING_DISPATCH -> {
                    binding.imgCopy.visibility = GONE
                    binding.tvTracking.visibility = GONE
                    binding.orderItemSubtitle.text =
                        context.getString(R.string.awaiting_dispatch_no, totalQuantity)
                }

                UpdatedOrderStatus.DISPATCHED -> {
                    binding.imgCopy.visibility = VISIBLE
                    binding.tvTracking.visibility = VISIBLE
                    dispatched?.let {
                        binding.orderItemSubtitle.text = context.getString(
                            R.string.dispatched_on, totalQuantity,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dispatched)
                        )
                    }
                    updatedOrder.trackingDetails?.let {
                        if (updatedOrder.trackingDetails.courierName != null) {
                            binding.tvTracking.text = context.getString(
                                R.string.tracking_number,
                                updatedOrder.trackingDetails.courierName,
                                updatedOrder.trackingDetails.trackingId
                            )
                        } else {
                            binding.tvTracking.text = context.getString(
                                R.string.tracking_number_no_name,
                                updatedOrder.trackingDetails.trackingId
                            )
                        }
                    }
                }

                UpdatedOrderStatus.CANCELLED -> {
                    binding.imgCopy.visibility = GONE
                    binding.tvTracking.visibility = GONE
                    binding.orderItemSubtitle.text =
                        context.getString(R.string.cancelled_on, totalQuantity)
                }

                UpdatedOrderStatus.PROCESSING -> {
                    binding.imgCopy.visibility = GONE
                    binding.tvTracking.visibility = GONE
                    binding.orderItemSubtitle.text =
                        context.getString(R.string.items_processing, totalQuantity)
                }

                else -> {}
            }
            binding.imgCopy.setOnClickListener {
                orderItemListener.onTrackingNumCopy(updatedOrder.trackingDetails?.trackingId)
            }
        }
    }

    interface OrderItemListener {
        fun onTrackingNumCopy(trackingNumber: String?)
    }
}
