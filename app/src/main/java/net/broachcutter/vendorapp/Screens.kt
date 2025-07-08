package net.broachcutter.vendorapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.valartech.commons.base.FragmentScreen
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.cart.PlaceOrderResponse
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.screens.accessories.AccessoriesFragment
import net.broachcutter.vendorapp.screens.accessories.adapter_spec.home.AdapterSpecHomeFragment
import net.broachcutter.vendorapp.screens.accessories.arbor_extensions.home.ArborExtensionHomeFragment
import net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.home.ArborRadialSpecHomeFragment
import net.broachcutter.vendorapp.screens.accessories.commons.AccessoriesCommonFragment
import net.broachcutter.vendorapp.screens.account.AccountInfoFragment
import net.broachcutter.vendorapp.screens.annular.home.AnnularCuttersHomeFragment
import net.broachcutter.vendorapp.screens.cart.view.CartFragment
import net.broachcutter.vendorapp.screens.cart.view.CheckoutConfirmationFragment
import net.broachcutter.vendorapp.screens.coupon.CouponDetailFragment
import net.broachcutter.vendorapp.screens.coupon.CouponListFragment
import net.broachcutter.vendorapp.screens.holesaws.home.HolesawsHomeFragment
import net.broachcutter.vendorapp.screens.home.HomeFragment
import net.broachcutter.vendorapp.screens.login.LoginActivity
import net.broachcutter.vendorapp.screens.login.first_login.FirstLoginFragment
import net.broachcutter.vendorapp.screens.login.forgot_password.ForgotPasswordFragment
import net.broachcutter.vendorapp.screens.login.forgot_password.ForgotPasswordSuccessFragment
import net.broachcutter.vendorapp.screens.login.main.LoginFragment
import net.broachcutter.vendorapp.screens.login.otp.SignupOTPFragment
import net.broachcutter.vendorapp.screens.login.phone_number.LinkPhoneNumberFragment
import net.broachcutter.vendorapp.screens.login.relog_otp.RelogOTPFragment
import net.broachcutter.vendorapp.screens.main.MainActivity
import net.broachcutter.vendorapp.screens.my_order_history.MyOrderHistoryFragment
import net.broachcutter.vendorapp.screens.order_history.detail.OrderDetailFragment
import net.broachcutter.vendorapp.screens.pilot_pins.home.PilotPinsHomeFragment
import net.broachcutter.vendorapp.screens.product_list.ProductListFragment
import net.broachcutter.vendorapp.screens.search.SearchActivity
import net.broachcutter.vendorapp.screens.settings.SettingsFragment
import net.broachcutter.vendorapp.screens.settings.WebViewFragment
import net.broachcutter.vendorapp.screens.solid_drills.home.SolidDrillsHomeFragment
import net.broachcutter.vendorapp.screens.spares.SparesFragment
import net.broachcutter.vendorapp.screens.splash.SplashActivity
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    class Splash : ActivityScreen<SplashActivity>(SplashActivity::class.java)

    class Login : ActivityScreen<LoginActivity>(LoginActivity::class.java)
    class LoginMain : FragmentScreen(LoginFragment.newInstance())
    class LoginNewPassword : FragmentScreen(FirstLoginFragment.newInstance())
    class LoginForgotPassword : FragmentScreen(ForgotPasswordFragment())
    class LoginForgotPasswordSuccess(email: String) :
        FragmentScreen(ForgotPasswordSuccessFragment.newInstance(email))

    class LoginLinkPhoneNumber : FragmentScreen(LinkPhoneNumberFragment())
    class SignUpOtp(phoneNumber: String) :
        FragmentScreen(SignupOTPFragment.newInstance(phoneNumber))

    class RelogOtp(phoneNumber: String) : FragmentScreen(RelogOTPFragment.newInstance(phoneNumber))

    class Main : ActivityScreen<MainActivity>(MainActivity::class.java)

    /**
     * Use this to transition from other activities to [MainActivity] and navigate to a [FragmentScreen].
     */
    class MainTransition(private val bundle: Bundle) : SupportAppScreen() {

        override fun getActivityIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }
    }

    class Home : FragmentScreen(HomeFragment())
    class Search : ActivityScreen<SearchActivity>(SearchActivity::class.java)

    class ProductResultList(bundle: Bundle) :
        FragmentScreen(ProductListFragment.newInstance(bundle))

    // Annular Cutter
    class AnnularCutterHome : FragmentScreen(AnnularCuttersHomeFragment())

    class PilotPinsHome : FragmentScreen(PilotPinsHomeFragment())

    class HolesawsHome : FragmentScreen(HolesawsHomeFragment())

    // search
    class Spares : FragmentScreen(SparesFragment())

    class AccessoriesHome : FragmentScreen(AccessoriesFragment())

    class AccessoriesCommon : FragmentScreen(AccessoriesCommonFragment())

    class ArborExtensionHome : FragmentScreen(ArborExtensionHomeFragment())

    class AdapterSpecHome : FragmentScreen(AdapterSpecHomeFragment())

    class ArborRadialSpecHome : FragmentScreen(ArborRadialSpecHomeFragment())

    class Cart : FragmentScreen(CartFragment())

    class CheckoutOrderConfirmation(placeOrderResponse: PlaceOrderResponse) :
        FragmentScreen(CheckoutConfirmationFragment.newInstance(placeOrderResponse))

    // class OrderHistorySummary : FragmentScreen(OrderHistoryFragment())

    class OrderHistorySummary : FragmentScreen(MyOrderHistoryFragment())

    class OrderHistoryDetail(order: UpdatedOrder) : FragmentScreen(OrderDetailFragment.newInstance(order))

    class AccountInfo : FragmentScreen(AccountInfoFragment())

    class SolidDrills : FragmentScreen(SolidDrillsHomeFragment())

    class Settings : FragmentScreen(SettingsFragment())

    class WebView(url: String) : FragmentScreen(WebViewFragment.newInstance(url))

    class CouponDetails(coupon: Coupon) :
        FragmentScreen(CouponDetailFragment.newInstance(coupon))

    class CouponListScreen(coupon: List<Coupon>, fragmentName: String) :
        FragmentScreen(CouponListFragment.newInstance(coupon, fragmentName))
}

abstract class ActivityScreen<T : AppCompatActivity>(private val activityClassToken: Class<T>) :
    SupportAppScreen() {

    override fun getActivityIntent(context: Context): Intent? {
        return Intent(context, activityClassToken)
    }
}
