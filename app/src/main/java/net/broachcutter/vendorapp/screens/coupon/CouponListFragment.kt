package net.broachcutter.vendorapp.screens.coupon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentCouponListBinding
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.models.coupon.CouponType
import net.broachcutter.vendorapp.util.Constants
import net.broachcutter.vendorapp.util.ViewModelFactory
import org.jetbrains.anko.support.v4.longToast
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class CouponListFragment : BaseFragment() {

    private lateinit var couponList: List<Coupon>
    private lateinit var navigatedFrom: String
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var listViewModelFactory: ViewModelFactory<CouponListViewModel>

    private val modelList: CouponListViewModel by lazy {
        ViewModelProvider(this, listViewModelFactory).get(CouponListViewModel::class.java)
    }

    private var _binding: FragmentCouponListBinding? = null
    private val binding get() = _binding!!

    companion object {
        /**
         * This bundle needs to contain [Coupon]!
         */
        fun newInstance(couponList: List<Coupon>, fragmentName: String): CouponListFragment {
            val bundle = bundleOf(
                Constants.COUPON_LIST to couponList,
                Constants.FRAGMENT_NAME to fragmentName
            )
            val fragment = CouponListFragment()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCouponListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        couponList =
            arguments?.getParcelableArrayList<Coupon>(Constants.COUPON_LIST) as List<Coupon>
        navigatedFrom = arguments?.getString(Constants.FRAGMENT_NAME) as String
        initView()
    }

    private fun initView() {
        groupAdapter.clear()
        groupAdapter.apply {
            for (coupon in couponList) {
                add(
                    ItemCoupon(
                        navigatedFrom, coupon,
                        object : OnClickListener {
                            override fun onViewDetailsClick(coupon: Coupon) {
                                router.navigateTo(Screens.CouponDetails(coupon))
                                // model.clearCouponFromPref()
                            }

                            override fun onApplyClick(coupon: Coupon) {
                                longToast(getString(R.string.coupon_applied))
                                modelList.saveCouponToPref(coupon)
                            }

                            override fun onApplyAndAddItemsClick(coupon: Coupon) {
                                /**
                                 * apply coupon
                                 */
                                modelList.saveCouponToPref(coupon)
                                /**
                                 * add items to card
                                 */

                                when (coupon.couponType) {
                                    CouponType.BUYXGETY -> {
                                        val xQuantity: Int = coupon.xQuantity ?: 0
                                        val yQuantity = coupon.yQuantity ?: 0
                                        if (xQuantity > 0) {
                                            coupon.xProduct?.let { addToCart(it, xQuantity) }
                                        }

                                        if (yQuantity > 0) {
                                            coupon.yProduct?.let { addToCart(it, yQuantity) }
                                        }
                                    }

                                    CouponType.PERCENTAGE -> {
                                        coupon.percentageProduct?.let {
                                            addToCart(it, coupon.percentMinQty)
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        }
                    )
                )
            }
        }

        binding.rvCoupon.apply {
            adapter = groupAdapter
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
