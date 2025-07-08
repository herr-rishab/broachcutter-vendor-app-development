package net.broachcutter.vendorapp.screens.coupon

import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.ListCouponItemBinding
import net.broachcutter.vendorapp.models.coupon.Coupon
import org.threeten.bp.format.DateTimeFormatter

class CouponItem(
    val coupon: Coupon,
    private val listener: (coupon: Coupon) -> Unit
) : BindableItem<ListCouponItemBinding>() {

    override fun bind(binding: ListCouponItemBinding, position: Int) {
        binding.tvCouponId.text = coupon.id
        binding.tvShortDescription.text = coupon.shortDesc
        binding.tvValidity.text = binding.root.context.getString(
            R.string.valid_till,
            DateTimeFormatter.ofPattern("dd/MM/yyyy").format(coupon.validTill)
        )

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

        binding.tvViewDetails.setOnClickListener {
            listener(coupon)
        }
    }

    override fun getLayout(): Int = R.layout.list_coupon_item

    override fun initializeViewBinding(view: View): ListCouponItemBinding {
        return ListCouponItemBinding.bind(view)
    }
}
