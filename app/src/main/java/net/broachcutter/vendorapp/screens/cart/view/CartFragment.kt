package net.broachcutter.vendorapp.screens.cart.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.formatINRwithPrefix
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import com.valartech.loadinglayout.LoadingLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentCartBinding
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.CREDIT_EXCEEDED
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.MIN_BALANCE
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.NONE
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.NON_CREDIT_PAYMENT_TERM
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.NO_CART_ITEMS
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.NO_CART_PRICE_CALCULATED
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.OVERDUE_PAYMENT
import net.broachcutter.vendorapp.models.cart.CartDisabledReason.PAYMENT_TERMS_NOT_SELECTED
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.models.cart.CartUiModel
import net.broachcutter.vendorapp.models.cart.PaymentTerm
import net.broachcutter.vendorapp.models.cart.convertToUiModel
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.network.NO_PAYMENT_TERM
import net.broachcutter.vendorapp.screens.cart.CartViewModel
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.PRODUCT_TYPE
import net.broachcutter.vendorapp.util.PreferencesManager
import net.broachcutter.vendorapp.util.StickyFooterItemDecoration
import net.broachcutter.vendorapp.util.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

/**
 */
@Suppress("TooManyFunctions")
class CartFragment :
    BaseFragment(),
    CartLineItem.CartItemUpdateListener,
    OverduePaymentDialog.OverdueDialogListener,
    SubmitOrderDialog.SubmitDialogListener,
    UpdatePaymentTermDialog.UpdatePaymentTermDialogListener,
    TitleItem.TitleInteractionListener,
    CouponClickListener {

    private val model: CartViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(CartViewModel::class.java)
    }

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CartViewModel>

    @Inject
    lateinit var preferencesManager: PreferencesManager

    val adapter = GroupAdapter<GroupieViewHolder>()
    private val itemDecoration = StickyFooterItemDecoration()
    private var cartUiModel: CartUiModel? = null
    private var cartSeen = false
    private var appliedCoupon: Coupon? = null

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val PAYMENT_TERM = "PAYMENT_TERM"
        const val TAG = "CartFragment"
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
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (preferencesManager.couponsEnabled()) {
            appliedCoupon = model.getAppliedCouponFromPref()
        }
        cartSeen = false
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.cartRecyclerView.adapter = adapter

        binding.checkout.setOnClickListener { showSubmitDialog() }

        binding.backButton.setOnClickListener { model.goBack() }

        getCartItems()

        model.showDeliveryAddress.observe(
            viewLifecycleOwner
        ) {
            showDeliveryDialog(it)
        }

        model.cartLineItem.observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    Timber.i("cartLineItem success")
                    binding.cartLoadingLayout.setState(LoadingLayout.COMPLETE)
                    updateAllCartItems(resource.convertToUiModel(appliedCoupon).data)
                }

                Status.ERROR -> {
                    binding.cartLoadingLayout.setState(LoadingLayout.COMPLETE)
                    resource.message?.let {
                        requireActivity().longToast(it)
                    }
                }

                Status.LOADING -> binding.cartLoadingLayout.setState(LoadingLayout.LOADING_OVERLAY)
                else -> {}
            }
        }
    }

    private fun getCartItems() {
        model.cartUiModel.observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    Timber.i("cartUiModel success")
                    binding.cartLoadingLayout.setState(LoadingLayout.COMPLETE)
                    updateAllCartItems(resource.data)
                }

                Status.ERROR -> {
                    binding.cartLoadingLayout.setState(LoadingLayout.COMPLETE)
                    resource.message?.let {
                        requireActivity().longToast(it)
                    }
                }

                Status.LOADING -> binding.cartLoadingLayout.setState(LoadingLayout.LOADING_OVERLAY)
                else -> {}
            }
        }
    }

    override fun onResume() {
        if (appliedCoupon != null) {
            updateAllCartItems()
        }
        super.onResume()
    }

    private fun showDeliveryDialog(address: String?) {
        val builder: AlertDialog.Builder? = activity?.let { AlertDialog.Builder(it) }
        builder?.apply {
            setMessage(address)
            setTitle("Delivery address")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    /**
     *if Banner has to be non-sticky uncomment
     * [CreditBannerItem], [CartWarningBannerItem], [MinBannerItem]
     * adapters
     */
    @Suppress("ComplexMethod", "LongMethod")
    private fun updateAllCartItems(cartModel: CartUiModel?) {
        Timber.i("updateAllCartItems $cartModel")
        cartUiModel = cartModel
        if (!cartSeen && cartModel != null) {
            // record only the first time the cart is seen in analytics
            cartSeen = true
            analytics.viewCart(cartModel)
        }

        binding.checkout.isEnabled = cartModel?.isCheckoutEnabled ?: false
        if (!binding.checkout.isEnabled) {
            Timber.i("Checkout disabled because of: ${cartModel?.checkoutDisabledReason}")
        }

        // populate the cart
        adapter.clear()
        binding.cartRecyclerView.removeItemDecoration(itemDecoration)
        var isPricingFooterAdded = false
        val cartItems = cartModel?.cartItems
        val context = this.context
        appliedCoupon = model.getAppliedCouponFromPref()
        if (!cartItems.isNullOrEmpty() && context != null) {
            // cart warning header
            when (cartModel.checkoutDisabledReason) {
                NONE -> {
                    // don't show any banner
                    binding.banner.root.visibility = View.GONE
                }

                CREDIT_EXCEEDED -> {
                    binding.banner.root.visibility = View.VISIBLE
                    binding.banner.warningText.text = String.format(
                        getString(R.string.credit_warning),
                        cartModel.exceededCredit.formatINRwithPrefix(context)
                    )

                    // adapter.add(CreditBannerItem(context, cartModel.exceededCredit))
                }

                OVERDUE_PAYMENT -> {
                    binding.banner.root.visibility = View.VISIBLE
                    binding.banner.warningText.text = getString(R.string.overdue_payment_detail)
                    // adapter.add(CartWarningBannerItem(context, R.string.overdue_payment_detail))
                    analytics.overduePaymentShown()
                }

                MIN_BALANCE -> {
                    binding.banner.root.visibility = View.VISIBLE
                    binding.banner.warningText.text = String.format(
                        getString(R.string.cutter_min_warning),
                        cartModel.minRupeeAmountRequired.formatINRwithPrefix(context)
                    )
                    // adapter.add(MinBannerItem(context, cartModel.minRupeeAmountRequired))
                }

                PAYMENT_TERMS_NOT_SELECTED -> {
                    binding.banner.root.visibility = View.VISIBLE
                    binding.banner.warningText.text = getString(R.string.select_payment_terms_all)
                    // adapter.add(CartWarningBannerItem(context, R.string.select_payment_terms_all))
                }

                NO_CART_ITEMS -> {
                    binding.banner.root.visibility = View.GONE
                    // do nothing
                }

                NO_CART_PRICE_CALCULATED -> {
                    binding.banner.root.visibility = View.GONE
                    // do nothing
                }

                NON_CREDIT_PAYMENT_TERM -> {
                    binding.banner.root.visibility = View.VISIBLE
                    binding.banner.warningText.text =
                        getString(R.string.choose_non_credit_payment_term)
                }
            }
            // title
            adapter.add(TitleItem(this))

            // Coupon title - shows available coupons and applied coupons
            if (preferencesManager.couponsEnabled()) {
                val couponList = model.getCouponListFromPref()
                if (couponList.isNotEmpty()) {
                    if (appliedCoupon == null) {
                        adapter.add(
                            CouponAvailableTitleItem(couponList.size) {
                                model.offersAvailableCLicked(couponList, TAG)
                            }
                        )
                    } else {
                        appliedCoupon?.id?.let {
                            adapter.add(
                                CouponAppliedTitleItem(it, this)
                            )
                        }
                    }
                }
            }

            // add cart items
            for (cartItem in cartItems) {
                adapter.add(CartLineItem(context, cartItem, this))
            }

            if (cartModel.cartPrice != null) {
                // model.fetchFinalCartPrice(cartModel.cartItems)
                adapter.add(
                    PricingFooterItem(
                        context,
                        cartModel,
                        appliedCoupon,
                        preferencesManager.couponsEnabled()
                    )
                )
                isPricingFooterAdded = true
            } else {
                adapter.add(SpacingItem())
            }

            /*if (cartModel.total != null) {
                // we have tax and total line items
                adapter.add(PricingFooterItem(context, cartModel, appliedCoupon))
                isPricingFooterAdded = true
            } else {
                // add in a spacer
                adapter.add(SpacingItem())
            }*/
//            showCreditWarningBanner(cartModel.exceededCredit)
        } else {
            binding.cartLoadingLayout.setState(LoadingLayout.EMPTY)
        }
        if (isPricingFooterAdded) {
            // we need to make sure the pricing footer is stuck to the bottom of the screen
            binding.cartRecyclerView.addItemDecoration(itemDecoration)
        }
        binding.cartRecyclerView.invalidateItemDecorations()
    }

//    private fun showCreditWarningBanner(exceededCredit: Float) {
//        if (exceededCredit > 0) {
//            creditWarningBanner.visibility = View.VISIBLE
//            creditWarning.text = String.format(
//                getString(R.string.credit_warning),
//                exceededCredit.formatINRwithPrefix(context)
//            )
//            adapter.add(SpacingItem())
//        } else {
//            creditWarningBanner.visibility = View.GONE
//        }
//    }

    private fun showOverdueDialog(overduePayment: Boolean?) {
        if (overduePayment == null || !overduePayment) {
            return
        }
        context?.let {
            val dialog = OverduePaymentDialog.newInstance(this)
            dialog.isCancelable = false
            dialog.show(childFragmentManager, OverduePaymentDialog.TAG)
        }
    }

    private fun showSubmitDialog() {
        val context = context
        if (context != null) {
            val dialog = SubmitOrderDialog.newInstance(this)
            dialog.isCancelable = false
            dialog.show(childFragmentManager, SubmitOrderDialog.TAG)
        }
    }

    private fun showPaymentTermDialog(paymentTerm: PaymentTerm, productType: ProductType) {
        val context = context
        if (context != null) {
            val dialog = UpdatePaymentTermDialog.newInstance(this)
            // Supply num input as an argument.
            val args = Bundle()
            args.putParcelable(PAYMENT_TERM, paymentTerm)
            args.putString(PRODUCT_TYPE, productType.name)
            dialog.arguments = args
            dialog.isCancelable = false
            dialog.show(childFragmentManager, UpdatePaymentTermDialog.TAG)
        }
    }

    override fun onUpdate(
        cartItem: CartItem,
        newQuantity: Int,
        newTerms: PaymentTerm,
        updatePaymentTerm: Boolean
    ) {
        Timber.i("onUpdate")
        if (updatePaymentTerm) {
            val noOfSameProductType =
                model.updateAllPaymentTerm(cartUiModel?.cartItems, cartItem, newTerms)
            if (noOfSameProductType > 1) {
                cartItem.product.productType.let { showPaymentTermDialog(newTerms, it) }
            }
            model.onCartItemUpdate(cartItem, newQuantity, newTerms)
        } else {
            model.onCartItemUpdate(cartItem, newQuantity, newTerms)
        }
    }

    override fun onDelete(cartItem: CartItem) {
        model.onDelete(cartItem)
    }

    override fun showSelectPaymentTermsError() {
        toast(getString(R.string.please_select_payment_terms))
    }

    override fun onItemExpandedStateChange(cartItem: CartItem, isExpanded: Boolean) {
        if (isExpanded) {
            binding.checkout.isEnabled = false
        }
    }

    override fun onOverdueDialogDismiss() {
        model.onPaymentOverdue()
    }

    override fun onDeliveryAddressClick() {
        model.onDeliveryAddressClick()
    }

    override fun onSubmitOrder() {
        val cartUiModel = cartUiModel ?: run {
            Timber.e("Cart UI model is null, can't place order")
            return
        }
        Timber.i("onSubmitOrder ${cartUiModel.printCartInfo()}")
        if (cartUiModel.isReadyCart()) {
            analytics.checkoutSubmitOrderConfirmed()
            model.submitOrder(cartUiModel, appliedCoupon).observe(
                viewLifecycleOwner
            ) { resource ->
                when (resource?.status) {
                    Status.SUCCESS -> {
                        binding.orderPlacedLoadingLayout.root.isVisible = false
                        resource.data?.let { model.onSubmitOrderSuccess(it) }
                    }

                    Status.ERROR -> {
                        binding.orderPlacedLoadingLayout.root.isVisible = false
                        resource.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        // we want to use a different loading screen for order submission, don't
                        // use LoadingLayout in this case.
                        binding.orderPlacedLoadingLayout.root.isVisible = true
                    }

                    else -> {}
                }
            }
        } else {
            val exception = AppException(NO_PAYMENT_TERM)
            Timber.w(exception)
            longToast(exception.message)
        }
    }

    override fun onSubmitDialogDismiss() {
        Timber.i("order submission cancelled")
    }

    override fun onUpdatePaymentTerm(paymentTerm: PaymentTerm, productType: String) {
        cartUiModel?.cartItems?.let {
            for (cart in it) {
                if (cart.product.productType.name == productType) {
                    model.onCartItemUpdate(cart, cart.quantity, paymentTerm)
                }
            }
        }
    }

    override fun onUpdatePaymentDialogDismiss() {
        Timber.i("update all items payment term cancelled")
    }

    override fun onAppliedCouponClick() {
        model.offersAvailableCLicked(model.getCouponListFromPref(), TAG)
    }

    override fun onCouponDelete() {
        Timber.i("onCouponDelete")
        model.clearAppliedCoupon()
        updateAllCartItems(cartUiModel)
        updateAllCartItems()
    }

    private fun updateAllCartItems() {
        cartUiModel?.cartItems?.forEach { cartItem ->
            cartItem.selectedPaymentTerm?.let { selectedPaymentTerm ->
                model.onCartItemUpdate(
                    cartItem,
                    cartItem.quantity,
                    selectedPaymentTerm
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
