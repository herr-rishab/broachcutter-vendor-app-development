package net.broachcutter.vendorapp.screens.my_order_history

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.longToast
import com.valartech.loadinglayout.LoadingLayout
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentMyOrderHistoryBinding
import net.broachcutter.vendorapp.models.UpdatedOrderStatus
import net.broachcutter.vendorapp.util.ViewModelFactory
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.Collections
import java.util.Locale
import javax.inject.Inject

@Suppress("TooManyFunctions")
class MyOrderHistoryFragment : BaseFragment() {
    lateinit var adapter: OrderHistoryViewPagerAdapter
    var monthSpinner = ZonedDateTime.now().month.value

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MyOrderHistoryViewModel>

    private var _binding: FragmentMyOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyOrderHistoryViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MyOrderHistoryViewModel::class.java)
    }

    companion object {
        private val lastYearMonthsMap = LinkedHashMap<String, Month>()
        private val currentYearMonthsMap = LinkedHashMap<String, Month>()
        const val FINANCIAL_YEAR_START = 4
        const val LAST_YEAR = 1
        const val CURRENT_YEAR = 0
        const val TAB_LETTER_SPACING = 0.04f
        const val YEAR_START = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyOrderHistoryBinding.inflate(inflater, container, false)
        val months = ArrayList<Month>()
        val now = ZonedDateTime.now().month.value
        // setup months for dropdown
        months.addAll(Month.values())
        months.reverse()
        // only show months april onwards
        val filteredList =
            months.filter { it != Month.JANUARY && it != Month.FEBRUARY && it != Month.MARCH }
        filteredList.forEach {
            lastYearMonthsMap[it.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())] =
                it
        }
        months.reverse()
        val presentDate = ZonedDateTime.now()
        if (presentDate >= ZonedDateTime.of(
                LocalDate.of(presentDate.year, Month.APRIL, 1),
                LocalTime.of(0, 0),
                ZoneId.of("Asia/Kolkata")
            )
        ) {
            val currentYearFilterList = months.subList(0, now)
            currentYearFilterList.reverse()
            Collections.rotate(currentYearFilterList, now)
            val filteredList =
                currentYearFilterList.filter { it != Month.JANUARY && it != Month.FEBRUARY && it != Month.MARCH }
            filteredList.forEach {
                currentYearMonthsMap[
                    it.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    )
                ] =
                    it
            }
        } else {
            val currentYearFilterList = months.subList(0, now)
            currentYearFilterList.reverse()
            Collections.rotate(currentYearFilterList, now)
            currentYearFilterList.forEach {
                currentYearMonthsMap[
                    it.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    )
                ] =
                    it
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        setMonthAndYearSpinner()
        setUpViewPagerAdapter()
        registerObserver()
        setTabClickListener()
        setUpClickListener()
    }

    override fun onResume() {
        super.onResume()
        loadOrderHistoryData()
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            binding.swipeRefreshLayout.isRefreshing = true
            loadOrderHistoryData()
        }
    }

    private fun loadOrderHistoryData() {
        val year = (binding.spinnerYear.selectedItem as String).toInt()
        viewModel.onDateSet(monthSpinner, year, true)
    }

    private fun setUpClickListener() {
        binding.btnOrderSearch.setOnClickListener {
            onDateSelected(monthSpinner, binding.spinnerYear)
        }
    }

    private fun registerObserver() {
        viewModel.ordersUiModel.observe(viewLifecycleOwner) { resources ->
            when (resources.status) {
                Status.LOADING -> {
                    binding.myOrderLoadingLayout.setState(LoadingLayout.LOADING)
                }

                Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.myOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                }

                Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.myOrderLoadingLayout.setState(LoadingLayout.COMPLETE)
                    longToast(resources.message)
                }
            }
        }
    }

    private fun onDateSelected(month: Int, yearSpinner: Spinner) {
        val year = (yearSpinner.selectedItem as String).toInt()
        viewModel.onDateSet(month, year)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    private fun setTabClickListener() {
        binding.orderStatusTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.children?.forEach {
                    if (it is TextView) {
                        it.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.twilight_blue
                            )
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.children?.forEach {
                    if (it is TextView) {
                        it.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.unselected_tab_text_color
                            )
                        )
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    @Suppress("LongMethod")
    private fun setUpViewPagerAdapter() {
        adapter = OrderHistoryViewPagerAdapter(
            requireActivity()
        )
        binding.orderViewpager.adapter = adapter
        binding.orderViewpager.setCurrentItem(0, false)
        TabLayoutMediator(
            binding.orderStatusTab, binding.orderViewpager
        ) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                UpdatedOrderStatus.PENDING.position -> {
                    viewModel.pendingOrders.observe(
                        viewLifecycleOwner
                    ) {
                        tab.text =
                            "${
                            getString(R.string.pending_tab_title)
                            } (${it.size})"
                    }
                }

                UpdatedOrderStatus.AWAITING_PAYMENT.position -> {
                    viewModel.awaitingPaymentOrders.observe(
                        viewLifecycleOwner
                    ) {
                        val title = "${
                        getString(R.string.awaiting_payment_tab_title)
                        } (${it.size})"
                        customTabTitle(title, tab)
                    }
                }

                UpdatedOrderStatus.PROCESSING.position -> {
                    viewModel.processingOrders.observe(
                        viewLifecycleOwner
                    ) {
                        val title = "${
                        getString(R.string.processing_tab_title)
                        } (${it.size})"
                        customTabTitle(title, tab)
                    }
                }

                UpdatedOrderStatus.AWAITING_DISPATCH.position -> {
                    viewModel.awaitingDispatchOrders.observe(
                        viewLifecycleOwner
                    ) {
                        val title =
                            "${
                            getString(R.string.awaiting_dispatch_tab_title)
                            } (${it.size})"

                        customTabTitle(title, tab)
                    }
                }

                UpdatedOrderStatus.DISPATCHED.position -> {
                    viewModel.dispatchedOrders.observe(
                        viewLifecycleOwner
                    ) {
                        tab.text =
                            "${
                            getString(R.string.dispatched_tab_title)
                            } (${it.size})"
                    }
                }

                UpdatedOrderStatus.CANCELLED.position -> {
                    viewModel.cancelledOrders.observe(
                        viewLifecycleOwner
                    ) {
                        tab.text =
                            "${
                            getString(R.string.cancelled_tab_title)
                            } (${it.size})"
                    }
                }
            }
        }.attach()
    }

    /**
     * This is used for Awaiting Payment and Awaiting Dispatch
     * Because they are in multiline
     */
    // TODO Find some easy solution its hard to maintain
    private fun customTabTitle(title: String, tab: TabLayout.Tab) {
        tab.run {
            customView = null
            val tabTitle = TextView(requireContext())
            tabTitle.run {
                setTypeface(
                    ResourcesCompat.getFont(
                        requireActivity(),
                        R.font.proxima_nova_semibold
                    ),
                    Typeface.NORMAL
                )
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimensionPixelSize(R.dimen._14ssp).toFloat()
                )
                letterSpacing = TAB_LETTER_SPACING
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                text = title
                gravity = Gravity.CENTER
            }
            customView = tabTitle
        }
    }

    private fun setMonthAndYearSpinner() {
        // setup months spinner
        val lastYearMonthsAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.date_spinner_item,
            R.id.dateText,
            lastYearMonthsMap.keys.toList()
        )
        lastYearMonthsAdapter.setDropDownViewResource(R.layout.date_spinner_dropdown_item)

        val currentYearMonthsAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.date_spinner_item,
            R.id.dateText,
            currentYearMonthsMap.keys.toList()
        )
        currentYearMonthsAdapter.setDropDownViewResource(R.layout.date_spinner_dropdown_item)

        // setup year spinner
        val now = ZonedDateTime.now()
        val yearList = LinkedHashSet<Int>()
        if (now >= ZonedDateTime.of(
                LocalDate.of(now.year, Month.APRIL, 1),
                LocalTime.of(0, 0),
                ZoneId.of("Asia/Kolkata")
            )
        ) {
            yearList.add(now.year)
        } else {
            yearList.add(now.year)
            yearList.add(now.year - LAST_YEAR)
        }

        /**
         * Uncomment this for getting previous year in spinner
         */
        // yearList.add(now.year - 1)

        val yearsAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.date_spinner_item,
            R.id.dateText,
            yearList.map { it.toString() }
        )
        yearsAdapter.setDropDownViewResource(R.layout.date_spinner_dropdown_item)
        binding.spinnerYear.adapter = yearsAdapter
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == CURRENT_YEAR) {
                    binding.spinnerMonth.adapter = currentYearMonthsAdapter
                } else {
                    binding.spinnerMonth.adapter = lastYearMonthsAdapter
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val monthString = binding.spinnerMonth.selectedItem as String
                if (binding.spinnerMonth.adapter == currentYearMonthsAdapter) {
                    val month = currentYearMonthsMap[monthString]
                    monthSpinner = month!!.value
                } else if (binding.spinnerMonth.adapter == lastYearMonthsAdapter) {
                    val month = lastYearMonthsMap[monthString]
                    monthSpinner = month!!.value
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
