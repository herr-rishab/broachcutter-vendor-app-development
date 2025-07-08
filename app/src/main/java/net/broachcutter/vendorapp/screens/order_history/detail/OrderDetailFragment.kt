package net.broachcutter.vendorapp.screens.order_history.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import com.valartech.commons.utils.extensions.dpToPx
import com.valartech.commons.utils.extensions.toast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseVMFragment
import net.broachcutter.vendorapp.databinding.FragmentOrderDetailBinding
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.screens.order_history.detail.OrderDetailFragment.Companion.newInstance
import net.broachcutter.vendorapp.views.PricingFooter
import org.jetbrains.anko.backgroundColorResource
import org.threeten.bp.format.DateTimeFormatter

/**
 * Use [newInstance] to create a new instance.
 */
class OrderDetailFragment :
    BaseVMFragment<OrderDetailViewModel>(),
    OrderItemCardView.OrderItemListener {

    companion object {
        private const val ARG_ORDER = "ARG_ORDER"
        const val spacingHeight = 20

        fun newInstance(order: UpdatedOrder?): OrderDetailFragment {
            val bundle = bundleOf(ARG_ORDER to order)
            val fragment = OrderDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override val vmClassToken: Class<OrderDetailViewModel>
        get() = OrderDetailViewModel::class.java

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order: UpdatedOrder? = arguments?.getParcelable(ARG_ORDER)
        order?.run {
            binding.orderNumberText.text = getString(R.string.order_num, orderId)
            val formattedOrderDate =
                DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm a").format(orderDate)
                    .replace("am", "AM")
                    .replace("pm", "PM")
                    .replace("-", "at")
            binding.tvOrderTime.text = getString(R.string.order_placed_on, formattedOrderDate)
            orderStatus(orderStatus, this)
            val context = context
            context?.let {
                // add in order items
                product.forEach { orderItem ->
                    val orderItemCard = OrderItemCardView(context)
                    orderItemCard.bind(
                        orderItem,
                        orderStatus,
                        dispatchDate,
                        this@OrderDetailFragment,
                        this@run
                    )
                    binding.orderItemLayout.addView(orderItemCard)
                    val space = Space(context)
                    val layoutParams = LinearLayout.LayoutParams(
                        WRAP_CONTENT,
                        spacingHeight.dpToPx()
                    )
                    space.layoutParams = layoutParams
                    binding.orderItemLayout.addView(space)
                }
                // add in pricing footer
                val pricingFooter = PricingFooter(context)
                pricingFooter.bind(
                    order.taxItems,
                    order.subtotal,
                    order.total,
                    order.couponDiscount
                )
                binding.orderItemLayout.addView(pricingFooter)
            }
        }
    }

    @Suppress("LongMethod")
    private fun orderStatus(
        orderStatus: UpdatedOrderStatus?,
        updatedOrder: UpdatedOrder
    ) {
        when (orderStatus) {
            UpdatedOrderStatus.PENDING -> {
                binding.paymentTitle.text = getString(R.string.your_order_pending)
                binding.paymentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.warm_grey_two
                    )
                )
                binding.backgroundWaveTop.setImageResource(R.drawable.grey_wave_top)
                binding.backgroundBottom.backgroundColorResource = R.color.brownish_grey_two
            }

            UpdatedOrderStatus.AWAITING_PAYMENT -> {
                binding.paymentTitle.text = getString(R.string.your_order_awaiting_payment)
                binding.paymentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_two
                    )
                )
                binding.backgroundWaveTop.setImageResource(R.drawable.black_two_wave_top)
                binding.backgroundBottom.backgroundColorResource = R.color.black_two
            }

            UpdatedOrderStatus.PROCESSING -> {
                binding.paymentTitle.text = getString(R.string.your_order_processing)
                binding.paymentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dark_bluey_grey
                    )
                )
                binding.backgroundWaveTop.setImageResource(R.drawable.dark_bluey_grey_wave_top)
                binding.backgroundBottom.backgroundColorResource = R.color.dark_bluey_grey
            }

            UpdatedOrderStatus.AWAITING_DISPATCH -> {
                updatedOrder.docDueDate?.let {
                    binding.tvConfirmationDate.visibility = View.VISIBLE
                    binding.tvConfirmationDate.text = getString(
                        R.string.order_due_date,
                        DateTimeFormatter.ofPattern("dd/MM/yy").format(it)
                    )
                }
                binding.paymentTitle.text = getString(R.string.your_order_awaiting_dispatch)
                binding.paymentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bluey_grey
                    )
                )
                binding.backgroundWaveTop.setImageResource(R.drawable.blue_wave_top)
                binding.backgroundBottom.backgroundColorResource = R.color.marine
            }

            UpdatedOrderStatus.DISPATCHED -> {
                updatedOrder.dispatchDate?.let {
                    binding.paymentTitle.text =
                        getString(
                            R.string.order_dispatched_date,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy").format(it)
                        )
                    binding.paymentTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grassy_green
                        )
                    )
                }
                updatedOrder.docDueDate?.let {
                    binding.tvConfirmationDate.visibility = View.VISIBLE
                    binding.tvConfirmationDate.text =
                        getString(
                            R.string.order_due_date,
                            DateTimeFormatter.ofPattern("dd/MM/yy")
                                .format(it)
                        )
                }
                binding.backgroundWaveTop.setImageResource(R.drawable.blue_wave_top)
                binding.backgroundBottom.backgroundColorResource = R.color.marine
            }

            UpdatedOrderStatus.CANCELLED -> {
                binding.paymentTitle.text = getString(R.string.your_order_cancelled)
                binding.paymentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.burnt_red
                    )
                )
                binding.backgroundWaveTop.setImageResource(R.drawable.radish_wave_top)
                binding.backgroundBottom.backgroundColorResource = R.color.dark_radish
            }

            else -> {}
        }
    }

    override fun onTrackingNumCopy(trackingNumber: String?) {
        trackingNumber?.let {
            copyToClipboard(it)
            toast(R.string.number_copied)
        }
    }

    private fun copyToClipboard(text: String) {
        val context = context
        context?.let {
            val clipboard = getSystemService(context, ClipboardManager::class.java)
            val clip = ClipData.newPlainText(getString(R.string.label), text)
            clipboard?.setPrimaryClip(clip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
