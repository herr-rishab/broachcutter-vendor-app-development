package net.broachcutter.vendorapp.screens.home

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.launch
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.analytics.LandingSection.*
import net.broachcutter.vendorapp.models.UserDetail
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.screens.coupon.repo.CouponRepository
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.LIST_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.TITLE
import net.broachcutter.vendorapp.util.PreferencesManager
import org.threeten.bp.ZonedDateTime
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@Suppress("TooManyFunctions")
class HomeViewModel @Inject constructor(
    private val router: Router,
    userRepository: UserRepository,
    private val couponRepository: CouponRepository,
    private val context: Application,
    private val preferencesManager: PreferencesManager,
    private val analytics: Analytics
) : ViewModel() {

    private val currentMonth: Int
    private val currentYear: Int

    private val _allCouponList = MutableLiveData<Resource<List<Coupon>>>()

    val allCouponList: LiveData<Resource<List<Coupon>>>
        get() = _allCouponList

    val userDetail: LiveData<UserDetail> = userRepository.getUserUpdates()

    init {
        val now = ZonedDateTime.now()
        currentMonth = now.monthValue
        currentYear = now.year
        getAllCoupon()
    }

    fun onMachinesClick() {
        analytics.landingSectionClick(Machines)
        val args = bundleOf(
            LIST_TYPE to ListType.DRILLING_MACHINES,
            TITLE to context.getString(R.string.magnetic_drilling_machines_title)
        )
        router.navigateTo(Screens.ProductResultList(args))
    }

    fun onCuttersClick() {
        analytics.landingSectionClick(Cutters)
        router.navigateTo(Screens.AnnularCutterHome())
    }

    fun onSparesClick() {
        analytics.landingSectionClick(Spares)
        router.navigateTo(Screens.Spares())
    }

    fun onAccessoriesClick() {
        analytics.landingSectionClick(Accessories)
        router.navigateTo(Screens.AccessoriesHome())
    }

    fun onSolidDrillsClick() {
        analytics.landingSectionClick(SolidDrills)
        router.navigateTo(Screens.SolidDrills())
    }

    fun onHolesawsClick() {
        analytics.landingSectionClick(Holesaws)
        router.navigateTo(Screens.HolesawsHome())
    }

    fun onOrderHistoryClick() {
        analytics.landingSectionClick(OrderHistory)
        router.navigateTo(Screens.OrderHistorySummary())
    }

    fun onOfferSchemeClick(couponList: List<Coupon>, fragmentName: String) {
        analytics.landingSectionClick(OfferScheme)
        router.navigateTo(Screens.CouponListScreen(couponList, fragmentName))
    }

    private fun getAllCoupon() {
        if (preferencesManager.couponsEnabled()) {
            viewModelScope.launch {
                _allCouponList.value = couponRepository.getAllCoupons().value
            }
        }
    }

    fun compareCoupon(couponList: List<Coupon>): ArrayList<Coupon> {
        val couponListFromPref: List<Coupon> = couponRepository.getCouponListFromPref()
        return ArrayList(couponList.minus(couponListFromPref.toSet()))
    }

    fun saveNewCouponToSharedPref(newCoupons: List<Coupon>) {
        val updatedCoupons = ArrayList<Coupon>()
        updatedCoupons.addAll(newCoupons)
        updatedCoupons.addAll(couponRepository.getCouponListFromPref())
        couponRepository.saveCouponListToPref(updatedCoupons)
    }

    fun getCouponListFromPref(): List<Coupon> {
        var couponList: List<Coupon> = arrayListOf()
        if (preferencesManager.couponsEnabled()) {
            couponList = couponRepository.getCouponListFromPref()
        }
        return couponList
    }
}
