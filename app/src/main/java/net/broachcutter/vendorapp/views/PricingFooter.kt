package net.broachcutter.vendorapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.valartech.commons.utils.extensions.formatINRwithPrefix
import com.valartech.commons.utils.extensions.removeTrailingZeroes
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.PricingFooterBinding
import net.broachcutter.vendorapp.models.cart.CartPrice
import net.broachcutter.vendorapp.models.cart.TaxItem
import net.broachcutter.vendorapp.util.Constants

class PricingFooter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: PricingFooterBinding

    init {
        binding = PricingFooterBinding.inflate(LayoutInflater.from(context), this)
    }

    fun bind(cartPrice: CartPrice) {
        binding.subtotalAmt.text = cartPrice.itemSubTotal.formatINRwithPrefix(context)
        if (cartPrice.couponDiscount > 0.0) {
            binding.couponDiscountLine.visibility = View.VISIBLE
            binding.couponDiscountAmt.text = cartPrice.couponDiscount.formatINRwithPrefix(context)
        } else {
            binding.couponDiscountLine.visibility = View.GONE
        }

        binding.taxItemLabel.text = String.format(
            context.getString(R.string.tax_line_item),
            Constants.TAX_TYPE,
            Constants.TAX_PERCENTAGE
        )
        binding.taxItemAmt.text = cartPrice.totalTax.formatINRwithPrefix(context)
        binding.totalAmount.text = cartPrice.totalPrice.formatINRwithPrefix(context)
    }

    fun bind(taxItems: List<TaxItem>?, subtotal: Double?, total: Double?, couponDiscount: Double?) {
        binding.subtotalAmt.text = subtotal?.formatINRwithPrefix(context)
        // todo set discount here not subtotal
        if (couponDiscount != null && couponDiscount > 0.0) {
            binding.couponDiscountLine.visibility = View.VISIBLE
            binding.couponDiscountAmt.text = couponDiscount.formatINRwithPrefix(context)
        } else {
            binding.couponDiscountLine.visibility = View.GONE
        }

        binding.totalAmount.text = total?.formatINRwithPrefix(context)
        if (taxItems != null) {
            // item 1
            if (taxItems.isNotEmpty()) {
                binding.taxLine1.visibility = View.VISIBLE
                val taxItem1 = taxItems[0]
                binding.taxItemLabel.text = String.format(
                    context.getString(R.string.tax_line_item),
                    taxItem1.type,
                    taxItem1.percentage.removeTrailingZeroes()
                )
                binding.taxItemAmt.text = taxItem1.amount.formatINRwithPrefix(context)
            } else {
                binding.taxLine1.visibility = View.GONE
            }
            // item 2
            /*if (taxItems.size > 1) {
                taxLine2.visibility = View.VISIBLE
                val taxItem2 = taxItems[1]
                taxItem2Label.text = String.format(
                    context.getString(R.string.tax_line_item),
                    taxItem2.type,
                    taxItem2.percentage
                )
                taxItem2Amt.text = taxItem2.amount.formatINRwithPrefix(context)
            } else {
                taxLine2.visibility = View.GONE
            }
            // item 3
            if (taxItems.size > 2) {
                taxLine3.visibility = View.VISIBLE
                val taxItem3 = taxItems[2]
                taxItem3Label.text = String.format(
                    context.getString(R.string.tax_line_item),
                    taxItem3.type,
                    taxItem3.percentage
                )
                taxItem3Amt.text = taxItem3.amount.formatINRwithPrefix(context)
            } else {
                taxLine3.visibility = View.GONE
            }*/
        }
    }
}
