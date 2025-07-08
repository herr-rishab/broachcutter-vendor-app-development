package net.broachcutter.vendorapp.screens.coupon.slide

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.databinding.FragmentCouponOfferDialogBinding
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.models.coupon.PercentApplyTo
import net.broachcutter.vendorapp.screens.coupon.CouponListViewModel
import net.broachcutter.vendorapp.util.Constants
import net.broachcutter.vendorapp.util.ViewModelFactory
import org.jetbrains.anko.support.v4.longToast
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class CouponOfferDialogFragment : DialogFragment() {

    private var _binding: FragmentCouponOfferDialogBinding? = null
    private val binding get() = _binding!!

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var couponList: List<Coupon>
    private val snapHelper: SnapHelper = LinearSnapHelper()
    private var snapItemSelectedPos: Int = 0

    @Inject
    lateinit var listViewModelFactory: ViewModelFactory<CouponListViewModel>

    private val modelList: CouponListViewModel by lazy {
        ViewModelProvider(this, listViewModelFactory).get(CouponListViewModel::class.java)
    }

    @Inject
    lateinit var router: Router

    companion object {
        const val TAG = "CouponOfferDialogFragment"
        fun newInstance(couponList: List<Coupon>): CouponOfferDialogFragment {
            val bundle = bundleOf(Constants.COUPON_LIST to couponList)
            val fragment = CouponOfferDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCouponOfferDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        couponList =
            arguments?.getParcelableArrayList<Coupon>(Constants.COUPON_LIST) as List<Coupon>
        initViews()
        clickListener()
    }

    private fun clickListener() {
        binding.tvViewDetails.setOnClickListener {
            router.navigateTo(Screens.CouponDetails(couponList[snapItemSelectedPos]))
            dismiss()
        }

        binding.tvViewDetailsCouponProductType.setOnClickListener {
            router.navigateTo(Screens.CouponDetails(couponList[snapItemSelectedPos]))
            dismiss()
        }
        binding.btnAddToCart.setOnClickListener {
            /**
             * add items to card
             */
            val coupon = couponList[snapItemSelectedPos]
            val xQuantity: Int = coupon.xQuantity ?: 0
            val yQuantity = coupon.yQuantity ?: 0
            if (xQuantity > 0) {
                coupon.xProduct?.let { xProduct -> addToCart(xProduct, xQuantity) }
            }

            if (yQuantity > 0) {
                coupon.yProduct?.let { yProduct -> addToCart(yProduct, yQuantity) }
            }

            if (coupon.percentMinQty > 0) {
                coupon.percentageProduct?.let { percentProduct ->
                    addToCart(
                        percentProduct,
                        coupon.percentMinQty
                    )
                }
            }
            /**
             * apply coupon
             */
            modelList.saveCouponToPref(coupon)
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun initViews() {
        binding.rvCouponBanner.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    snapItemSelectedPos = position
                    setAddToCartView()
                }
            }
        )
        binding.rvCouponBanner.adapter = groupAdapter

        /**
         * Visibility Gone if there is only 1 coupon item for banner
         */
        if (couponList.size != 1) {
            binding.recyclerViewIndicator.attachToRecyclerView(binding.rvCouponBanner)
        }

        groupAdapter.apply {
            for (coupon in couponList) {
                add(
                    ItemCouponOfferBanner(coupon)
                )
            }
        }
        binding.rvCouponBanner.apply {
            adapter = groupAdapter
        }
        setAddToCartView()
    }

    private fun setAddToCartView() {
        val coupon = couponList[snapItemSelectedPos]
        if (coupon.percentApplyTo == PercentApplyTo.PRODUCT_TYPE) {
            binding.btnAddToCart.visibility = View.GONE
            binding.tvViewDetails.visibility = View.GONE
            binding.tvViewDetailsCouponProductType.visibility = View.VISIBLE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
            binding.tvViewDetails.visibility = View.VISIBLE
            binding.tvViewDetailsCouponProductType.visibility = View.GONE
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        val addToCartResult = modelList.addToCart(product, quantity)
        addToCartResult.observe(
            viewLifecycleOwner
        ) {
            when (it?.status) {
                Status.SUCCESS -> {
                    toast(getString(R.string.added_to_cart))
                    Timber.i("Added to cart")
                }

                Status.ERROR -> {
                    longToast(getString(R.string.error_adding_cart, it.message))
                }

                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
