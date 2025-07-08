package net.broachcutter.vendorapp.screens.coupon.slide

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.ItemCouponOfferBannerBinding
import net.broachcutter.vendorapp.models.coupon.Coupon

class ItemCouponOfferBanner(
    val coupon: Coupon
) : BindableItem<ItemCouponOfferBannerBinding>() {

    override fun bind(viewBinding: ItemCouponOfferBannerBinding, position: Int) {
        viewBinding.tvCouponCode.text = coupon.id
        viewBinding.tvCouponShortDesc.text = coupon.shortDesc
    }

    override fun getLayout(): Int = R.layout.item_coupon_offer_banner

    override fun initializeViewBinding(view: View): ItemCouponOfferBannerBinding {
        return ItemCouponOfferBannerBinding.bind(view)
    }
}
