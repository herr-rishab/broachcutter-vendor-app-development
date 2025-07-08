package net.broachcutter.vendorapp.screens.coupon

import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.ListCouponItemBinding
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.models.coupon.PercentApplyTo
import net.broachcutter.vendorapp.screens.home.HomeFragment
import net.broachcutter.vendorapp.util.Constants
import org.threeten.bp.format.DateTimeFormatter

class ItemCoupon(
    private val navigatedFrom: String,
    val coupon: Coupon,
    private val onClickListener: OnClickListener
) : BindableItem<ListCouponItemBinding>() {

    override fun bind(binding: ListCouponItemBinding, position: Int) {
        binding.tvCouponId.text = coupon.id
        binding.tvShortDescription.text = coupon.shortDesc
        if (coupon.tillStockLast || coupon.validTill.year == Constants.MAX_DATE) {
            binding.tvValidity.text = binding.root.context.getString(
                R.string.offer_is_valid_till_stock_last,
            )
        } else {
            binding.tvValidity.text = binding.root.context.getString(
                R.string.valid_till,
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(coupon.validTill)
            )
        }
        if (position % 2 == 0) {
            binding.view.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.deep_brown
                )
            )
            binding.ivHeader.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.burnt_red
                )
            )
        } else {
            binding.view.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.water_blue
                )
            )
            binding.ivHeader.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.marine
                )
            )
        }

        if (navigatedFrom == HomeFragment.TAG) {
            binding.btnApplyAndAdd.text = binding.root.context.getString(
                R.string.apply_amp_add_items_to_cart
            )
            binding.btnApplyAndAdd.setOnClickListener {
                onClickListener.onApplyAndAddItemsClick(coupon)
            }
        } else {
            binding.btnApplyAndAdd.text =
                binding.root.context.getString(R.string.apply_coupon)
            binding.btnApplyAndAdd.setOnClickListener {
                onClickListener.onApplyClick(coupon)
            }
        }

        if (coupon.percentApplyTo == PercentApplyTo.PRODUCT_TYPE) {
            binding.btnApplyAndAdd.text =
                binding.root.context.getString(R.string.apply_coupon)
            binding.btnApplyAndAdd.setOnClickListener {
                onClickListener.onApplyClick(coupon)
            }
        } else {
            binding.btnApplyAndAdd.text = binding.root.context.getString(
                R.string.apply_amp_add_items_to_cart
            )
            binding.btnApplyAndAdd.setOnClickListener {
                onClickListener.onApplyAndAddItemsClick(coupon)
            }
        }

        binding.tvViewDetails.setOnClickListener {
            onClickListener.onViewDetailsClick(coupon)
        }
    }

    override fun getLayout(): Int = R.layout.list_coupon_item

    override fun initializeViewBinding(view: View): ListCouponItemBinding {
        return ListCouponItemBinding.bind(view)
    }
}

interface OnClickListener {
    fun onViewDetailsClick(coupon: Coupon)
    fun onApplyClick(coupon: Coupon)
    fun onApplyAndAddItemsClick(coupon: Coupon)
}
