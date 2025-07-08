package net.broachcutter.vendorapp.screens.cart.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.groupiex.plusAssign
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentCheckoutConfirmationBinding
import net.broachcutter.vendorapp.models.FailedOrderHeader
import net.broachcutter.vendorapp.models.FailedOrderItem
import net.broachcutter.vendorapp.models.SuccessOrderHeader
import net.broachcutter.vendorapp.models.SuccessOrderItem
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.setSafeOnClickListener
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class CheckoutConfirmationFragment : BaseFragment() {

    private val groupAdapter = GroupieAdapter()
    private lateinit var groupLayoutManager: LinearLayoutManager
    private var _binding: FragmentCheckoutConfirmationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CheckoutConfirmationViewModel>

    private val model: CheckoutConfirmationViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CheckoutConfirmationViewModel::class.java)
    }

    companion object {
        private const val ARG_RESPONSE = "ARG_RESPONSE"

        fun newInstance(placeOrderResponse: PlaceOrderResponse): CheckoutConfirmationFragment {
            val bundle = bundleOf(ARG_RESPONSE to placeOrderResponse)
            val fragment = CheckoutConfirmationFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    @set:Inject
    lateinit var router: Router

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutConfirmationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeOrderResponse = arguments?.getParcelable<PlaceOrderResponse>(ARG_RESPONSE)!!

        initViews()
        initData(placeOrderResponse)
    }

    private fun initData(placeOrderResponse: PlaceOrderResponse) {
        model.separateOrders(placeOrderResponse, requireActivity())
        updateOrder()
    }

    @Suppress("NestedBlockDepth", "ComplexMethod", "LongMethod")
    private fun updateOrder() {

        val pendingOrderList = model.pendingOrderList
        val awaitingPaymentOrderList = model.awaitingPaymentOrderList
        val processingOrderList = model.processingOrderList
        val failedOrderList = model.failedOrderList
        val failedMessage = model.failedMessage

        /**
         * Inflate Pending Order
         */
        if (pendingOrderList.size > 0) {
            groupAdapter.apply {
                this += ExpandableGroup(
                    SuccessOrderHeader(
                        getString(R.string.pending_order_title),
                        pendingOrderList.size, UpdatedOrderStatus.PENDING
                    )
                ).apply {
                    for (order in pendingOrderList) {
                        add(
                            SuccessOrderItem(
                                order.orderItems,
                                requireContext(),
                                order.orderNumber,
                                order.total
                            )
                        )
                    }
                    isExpanded = true
                }
            }
        }

        /**
         * Inflate Confirmed AwaitingPayment Order
         */
        if (awaitingPaymentOrderList.size > 0) {
            groupAdapter.apply {
                this += ExpandableGroup(
                    SuccessOrderHeader(
                        getString(R.string.order_confirmed_awaiting_payment_title),
                        awaitingPaymentOrderList.size, UpdatedOrderStatus.AWAITING_PAYMENT
                    )
                ).apply {
                    for (order in awaitingPaymentOrderList) {
                        add(
                            SuccessOrderItem(
                                order.orderItems,
                                requireContext(),
                                order.orderNumber,
                                order.total
                            )
                        )
                    }
                    isExpanded = true
                }
            }
        }

        /**
         * Inflate Confirmed Processing Order
         */
        if (processingOrderList.size > 0) {
            groupAdapter.apply {
                this += ExpandableGroup(
                    SuccessOrderHeader(
                        getString(R.string.order_confirmed_processing_title),
                        processingOrderList.size, UpdatedOrderStatus.PROCESSING
                    )
                ).apply {
                    for (order in processingOrderList) {
                        add(
                            SuccessOrderItem(
                                order.orderItems,
                                requireContext(),
                                order.orderNumber,
                                order.total
                            )
                        )
                    }
                    isExpanded = true
                }
            }
        }

        /**
         * Inflate Failed Order
         */
        if (failedOrderList.size > 0) {
            groupAdapter.apply {
                this += ExpandableGroup(
                    FailedOrderHeader(
                        getString(R.string.failed_order_title),
                        failedMessage, failedOrderList.size
                    )
                ).apply {
                    add(FailedOrderItem(failedOrderList, requireContext()))
                    isExpanded = true
                }
            }
        }
    }

    private fun initViews() {

        binding.backHome.setSafeOnClickListener {
            router.newRootScreen(Screens.Home())
        }

        groupLayoutManager = LinearLayoutManager(requireActivity())

        binding.rvOrder.apply {
            layoutManager = groupLayoutManager
            adapter = groupAdapter
        }
    }
}
