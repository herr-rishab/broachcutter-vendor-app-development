package net.broachcutter.vendorapp.screens.my_order_history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.valartech.loadinglayout.LoadingLayout
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.groupiex.plusAssign
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentConfirmedOrderBinding
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.util.ViewModelFactory
import javax.inject.Inject

class OrderFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MyOrderHistoryViewModel>
    private val viewModel: MyOrderHistoryViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MyOrderHistoryViewModel::class.java)
    }

    companion object {
        const val ORDER_TYPE = "order-type"

        fun getInstance(key: UpdatedOrderStatus): OrderFragment {
            val fragment = OrderFragment()
            val b = Bundle()
            b.putParcelable(ORDER_TYPE, key)
            fragment.arguments = b
            return fragment
        }
    }

    val confirmedOrderAdapter = GroupieAdapter()

    private var _binding: FragmentConfirmedOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentConfirmedOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        registerObserver()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    @Suppress("LongMethod")
    private fun registerObserver() {
        when (arguments?.getSerializable(ORDER_TYPE)) {
            UpdatedOrderStatus.PENDING -> {
                viewModel.pendingOrders.observe(
                    viewLifecycleOwner
                ) { orders ->
                    if (orders.isNotEmpty()) {
                        binding.confirmedOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                        addConfirmedOrder(orders)
                    } else {
                        binding.confirmedOrderLoadingLayout.setState(LoadingLayout.EMPTY)
                        binding.tvNoOrders.text = getString(R.string.no_pending_orders)
                    }
                }
            }

            UpdatedOrderStatus.AWAITING_PAYMENT -> {
                viewModel.awaitingPaymentOrders.observe(
                    viewLifecycleOwner,
                    { orders ->
                        if (orders.isNotEmpty()) {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                            addConfirmedOrder(orders)
                        } else {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.EMPTY)
                            binding.tvNoOrders.text = getString(R.string.no_awaiting_payment_orders)
                        }
                    }
                )
            }

            UpdatedOrderStatus.PROCESSING -> {
                viewModel.processingOrders.observe(
                    viewLifecycleOwner,
                    { orders ->
                        if (orders.isNotEmpty()) {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                            addConfirmedOrder(orders)
                        } else {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.EMPTY)
                            binding.tvNoOrders.text = getString(R.string.no_processing_orders)
                        }
                    }
                )
            }

            UpdatedOrderStatus.AWAITING_DISPATCH -> {
                viewModel.awaitingDispatchOrders.observe(
                    viewLifecycleOwner,
                    { orders ->
                        if (orders.isNotEmpty()) {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                            addConfirmedOrder(orders)
                        } else {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.EMPTY)
                            binding.tvNoOrders.text =
                                getString(R.string.no_awaiting_payment_dispatch_order)
                        }
                    }
                )
            }

            UpdatedOrderStatus.DISPATCHED -> {
                viewModel.dispatchedOrders.observe(
                    viewLifecycleOwner,
                    { dispatchedOrder ->
                        if (dispatchedOrder.isNotEmpty()) {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                            addConfirmedOrder(dispatchedOrder)
                        } else {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.EMPTY)
                            binding.tvNoOrders.text = getString(R.string.no_dispatched_orders)
                        }
                    }
                )
            }

            UpdatedOrderStatus.CANCELLED -> {
                viewModel.cancelledOrders.observe(
                    viewLifecycleOwner,
                    { orders ->
                        if (orders.isNotEmpty()) {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                            addConfirmedOrder(orders)
                        } else {
                            binding.confirmedOrderLoadingLayout.setState(LoadingLayout.EMPTY)
                            binding.tvNoOrders.text = getString(R.string.no_cancelled_orders)
                        }
                    }
                )
            }
        }
    }

    private fun addConfirmedOrder(orders: List<UpdatedOrder>) {
        confirmedOrderAdapter.clear()
        orders.forEach {
            confirmedOrderAdapter += Section().apply {
                add(
                    OrderRecyclerItem(it) {
                        viewModel.onOrderSelected(it)
                    }
                )
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvConfirmedOrders.adapter = confirmedOrderAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
