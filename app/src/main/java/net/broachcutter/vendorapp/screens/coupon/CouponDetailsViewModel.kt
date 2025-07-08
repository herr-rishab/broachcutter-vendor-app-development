package net.broachcutter.vendorapp.screens.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.screens.cart.repo.BroachCutterCartRepository
import net.broachcutter.vendorapp.screens.coupon.repo.CouponRepository
import timber.log.Timber
import javax.inject.Inject

class CouponDetailsViewModel @Inject constructor(
    private val cartRepository: BroachCutterCartRepository,
    private val couponRepository: CouponRepository
) : ViewModel() {

    fun saveCouponToPref(coupon: Coupon) {
        couponRepository.saveCouponToPref(coupon)
    }

    fun addToCart(product: Product, selectedQuantity: Int): LiveData<Resource<Any>> {
        Timber.i("addToCart: ${product.partNumber} $selectedQuantity")
        return cartRepository.addToOrUpdateCart(product, selectedQuantity)
    }
}
