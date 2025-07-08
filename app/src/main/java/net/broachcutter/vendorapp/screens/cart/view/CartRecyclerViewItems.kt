package net.broachcutter.vendorapp.screens.cart.view

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.valartech.commons.utils.extensions.formatINRwithPrefix
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.CartItemUpdatedBinding
import net.broachcutter.vendorapp.databinding.ItemCouponAppliedTitleBinding
import net.broachcutter.vendorapp.databinding.ItemCouponAvailableTitleBinding
import net.broachcutter.vendorapp.databinding.ListItemCartFooterBinding
import net.broachcutter.vendorapp.databinding.ListItemCartHeaderBinding
import net.broachcutter.vendorapp.databinding.ListItemCartHeaderWarningBinding
import net.broachcutter.vendorapp.databinding.ListItemCartSpacerBinding
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.models.cart.CartUiModel
import net.broachcutter.vendorapp.models.cart.PaymentTerm
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.views.ElegantNumberButton
import net.broachcutter.vendorapp.views.ProductCardView.Companion.maxQuantity

class CartWarningBannerItem(val context: Context, @StringRes private val warningMessage: Int) :
    BindableItem<ListItemCartHeaderWarningBinding>() {
    override fun bind(binding: ListItemCartHeaderWarningBinding, position: Int) {
        binding.warningText.setText(warningMessage)
    }

    override fun getLayout() = R.layout.list_item_cart_header_warning
    override fun initializeViewBinding(view: View): ListItemCartHeaderWarningBinding {
        return ListItemCartHeaderWarningBinding.bind(view)
    }
}

class CreditBannerItem(val context: Context, private val exceededCredit: Double) :
    BindableItem<ListItemCartHeaderWarningBinding>() {
    override fun bind(binding: ListItemCartHeaderWarningBinding, position: Int) {
        binding.warningText.text = String.format(
            context.getString(R.string.credit_warning),
            exceededCredit.formatINRwithPrefix(context)
        )
    }

    override fun getLayout() = R.layout.list_item_cart_header_warning

    override fun initializeViewBinding(view: View): ListItemCartHeaderWarningBinding {
        return ListItemCartHeaderWarningBinding.bind(view)
    }
}

class MinBannerItem(val context: Context, private val minRupeeAmountRequired: Double) :
    BindableItem<ListItemCartHeaderWarningBinding>() {
    override fun bind(binding: ListItemCartHeaderWarningBinding, position: Int) {
        binding.warningText.text = String.format(
            context.getString(R.string.cutter_min_warning),
            minRupeeAmountRequired.formatINRwithPrefix(context)
        )
    }

    override fun getLayout() = R.layout.list_item_cart_header_warning

    override fun initializeViewBinding(view: View): ListItemCartHeaderWarningBinding {
        return ListItemCartHeaderWarningBinding.bind(view)
    }
}

class TitleItem(private val listener: TitleInteractionListener) :
    BindableItem<ListItemCartHeaderBinding>() {

    override fun bind(binding: ListItemCartHeaderBinding, position: Int) {
        binding.deliveryAddress.setOnClickListener {
            listener.onDeliveryAddressClick()
        }
    }

    override fun getLayout() = R.layout.list_item_cart_header

    override fun initializeViewBinding(view: View): ListItemCartHeaderBinding {
        return ListItemCartHeaderBinding.bind(view)
    }

    interface TitleInteractionListener {
        fun onDeliveryAddressClick()
    }
}

class CouponAvailableTitleItem(
    private val availableCoupons: Int,
    private val listener: () -> Unit
) : BindableItem<ItemCouponAvailableTitleBinding>() {

    override fun bind(binding: ItemCouponAvailableTitleBinding, position: Int) {
        binding.tvSubTitle.text = "$availableCoupons available offers"
        binding.clCouponTitle.setOnClickListener {
            listener.invoke()
        }
    }

    override fun getLayout() = R.layout.item_coupon_available_title

    override fun initializeViewBinding(view: View): ItemCouponAvailableTitleBinding {
        return ItemCouponAvailableTitleBinding.bind(view)
    }
}

class CouponAppliedTitleItem(
    private val couponCode: String,
    private val listener: CouponClickListener
) : BindableItem<ItemCouponAppliedTitleBinding>() {

    override fun bind(binding: ItemCouponAppliedTitleBinding, position: Int) {
        binding.tvTitle.text = "'$couponCode Applied'"
        binding.clCouponTitle.setOnClickListener {
            listener.onAppliedCouponClick()
        }
        binding.imgDeleteCoupon.setOnClickListener {
            listener.onCouponDelete()
        }
    }

    override fun getLayout() = R.layout.item_coupon_applied_title

    override fun initializeViewBinding(view: View): ItemCouponAppliedTitleBinding {
        return ItemCouponAppliedTitleBinding.bind(view)
    }
}

interface CouponClickListener {

    fun onAppliedCouponClick()

    fun onCouponDelete()
}

class CartLineItem(
    val context: Context,
    private val cartItem: CartItem,
    private val listener: CartItemUpdateListener
) : BindableItem<CartItemUpdatedBinding>() {
    companion object {
        val cartColorList = arrayListOf(
            R.color.black_two,
            R.color.greyish_brown_two,
            R.color.greyish_brown_three,
            R.color.brownish_grey_three,
            R.color.warm_grey_three,
            R.color.warm_grey_four,
            R.color.warm_grey_five,
            R.color.warm_grey_six,
            R.color.warm_grey_seven,
            R.color.warm_grey_eight
        )
        const val DROPDOWN_THRESHOLD = 1000
        private val quantityList = ArrayList<String>()

        private const val PAYMENT_TERM_ID_LENGTH = 5 // Like P0070

        init {
            for (i in 0..maxQuantity) {
                quantityList.add(i.toString())
            }
        }
    }

    interface CartItemUpdateListener {

        fun onItemExpandedStateChange(cartItem: CartItem, isExpanded: Boolean)

        fun onUpdate(
            cartItem: CartItem,
            newQuantity: Int,
            newTerms: PaymentTerm,
            updatePaymentTerm: Boolean
        )

        fun onDelete(cartItem: CartItem)

        fun showSelectPaymentTermsError()
    }

    private val paymentTermsList = ArrayList<String>()
    private var quantity = cartItem.quantity
    var paymentTerm: PaymentTerm? = null

    init {
        cartItem.paymentTerms?.forEach {
            val string = "${it.id} with ${it.discountPercent}% Discount"
            paymentTermsList.add(string)
        }
    }

    override fun bind(binding: CartItemUpdatedBinding, position: Int) {
        binding.run {
            val bgColor = ContextCompat.getColor(
                context,
                cartColorList[position % cartColorList.size]
            )
            parentCard.setCardBackgroundColor(bgColor)

            binding.tvName.text = cartItem.product.name

            // pricing
            val discountText: String?
            val pricing = cartItem.getLineItemPrice()
            val priceText = if (pricing != null) {
                val discountAmt =
                    (pricing.basePrice - pricing.finalPrice).formatINRwithPrefix(context)
                discountText = context.getString(
                    R.string.discount_applied,
                    discountAmt
                )
                pricing.finalPrice.formatINRwithPrefix(context)
            } else {
                discountText = ""
                ""
            }

            // new cart Ui
            tvAmount.text = priceText
            tvDiscountType.text = discountText
            tvArticleNumber.text =
                context.getString(R.string.article_number, cartItem.partNumber)

            imgDeleteProduct.setOnClickListener {
                // remove from cart
                listener.onDelete(cartItem)
            }
            paymentTerm = cartItem.selectedPaymentTerm
            // setup payment terms spinner
            setSpinnerItem(binding)
            if (paymentTerm != null) {
                spinnerPaymentTerm.setText(getSpinnerText())
            } else {
                spinnerPaymentTerm.setText(context.getString(R.string.please_select))
            }
            setProductQuantity(binding)
        }
    }

    private fun getSpinnerText(): String? {
        paymentTermsList.forEachIndexed { _, s ->
            if (paymentTerm?.let { s.contains(it.id) } == true) {
                return s
            }
        }
        return null
    }

    private fun setSpinnerItem(binding: CartItemUpdatedBinding) {
        val paymentTermsAdapter = ArrayAdapter(
            context,
            R.layout.payment_terms_spinner_dropdown_item,
            R.id.terms,
            paymentTermsList
        )
        binding.spinnerPaymentTerm.setOnClickListener {
            binding.spinnerPaymentTerm.showDropDown()
        }
        binding.imgPaymentTermDropdown.setOnClickListener {
            binding.spinnerPaymentTerm.showDropDown()
        }
        binding.spinnerPaymentTerm.threshold = DROPDOWN_THRESHOLD
        binding.spinnerPaymentTerm.setAdapter(paymentTermsAdapter)
        binding.spinnerPaymentTerm.setOnItemClickListener { _, _, i, _ ->
            val newTerms = getSelectedPaymentTerms(paymentTermsList[i])
            newTerms?.let {
                listener.onUpdate(cartItem, quantity, newTerms, true)
            }
        }
    }

    private fun setProductQuantity(binding: CartItemUpdatedBinding) {
        binding.cartQtyButton.quantity = Integer.parseInt(cartItem.quantity.toString())
        binding.cartQtyButton.setOnValueChangeListener(object :
                ElegantNumberButton.OnValueChangeListener {
                override fun onValueChange(view: ElegantNumberButton?, oldValue: Int, newValue: Int) {
                    if (paymentTerm != null) {
                        listener.onUpdate(cartItem, newValue, paymentTerm!!, false)
                    } else {
                        listener.showSelectPaymentTermsError()
                    }
                    notifyChanged()
                }
            })
    }

    private fun getSelectedPaymentTerms(termString: String): PaymentTerm? {
        val selectedTermId = termString.substring(0, PAYMENT_TERM_ID_LENGTH)
        cartItem.paymentTerms?.forEach {
            if (it.id == selectedTermId) {
                return it
            }
        }
        return null
    }

    // can be used in future set the footer text
    /*private fun getPaymentTermsFooterString(paymentTerm: PaymentTerm): String {
        val builder = StringBuilder()
            .append(context.getString(R.string.payment_terms_footer1)).append(paymentTerm.id)
            .append(context.getString(R.string.payment_terms_footer2))
            .append(paymentTerm.discountPercent)
            .append(context.getString(R.string.payment_terms_footer3))
        return builder.toString()
    }*/

    override fun getLayout() = R.layout.cart_item_updated
    override fun initializeViewBinding(view: View): CartItemUpdatedBinding {
        return CartItemUpdatedBinding.bind(view)
    }
}

class SpacingItem : BindableItem<ListItemCartSpacerBinding>() {
    override fun bind(viewBinding: ListItemCartSpacerBinding, position: Int) {
        // No binding logic needed for a spacer item
    }

    override fun initializeViewBinding(view: View): ListItemCartSpacerBinding {
        return ListItemCartSpacerBinding.bind(view)
    }

    override fun getLayout() = R.layout.list_item_cart_spacer
}

class PricingFooterItem(
    val context: Context,
    val cart: CartUiModel,
    private val appliedCoupon: Coupon?,
    val isCouponFeatureEnabled: Boolean
) : BindableItem<ListItemCartFooterBinding>() {
    override fun bind(binding: ListItemCartFooterBinding, position: Int) {
        if (cart.cartPrice != null) {
            binding.pricingFooterView.bind(cart.cartPrice)
        }

        if (appliedCoupon != null && isCouponFeatureEnabled) {
            binding.tvCouponApplied.text = context.getString(
                R.string.coupon_code_applied, appliedCoupon.id
            )
            binding.clCouponApplied.visibility = View.VISIBLE
        } else {
            binding.clCouponApplied.visibility = View.INVISIBLE
        }
    }

    override fun getLayout() = R.layout.list_item_cart_footer

    override fun initializeViewBinding(view: View): ListItemCartFooterBinding {
        return ListItemCartFooterBinding.bind(view)
    }
}
