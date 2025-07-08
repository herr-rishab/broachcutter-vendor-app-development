package net.broachcutter.vendorapp.di

import android.app.Application
import dagger.Component
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.RepositoryModule
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.db.CartTypeConverters
import net.broachcutter.vendorapp.db.OrderTypeConverters
import net.broachcutter.vendorapp.db.ProductTypeConverters
import net.broachcutter.vendorapp.screens.accessories.AccessoriesFragment
import net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.item_number.ArborRadialSpecItemNumberFragment
import net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.specification.ArborRadialSpecSpecificationFragment
import net.broachcutter.vendorapp.screens.accessories.commons.AccessoriesCommonFragment
import net.broachcutter.vendorapp.screens.account.AccountInfoFragment
import net.broachcutter.vendorapp.screens.annular.item_number.AnnularCuttingItemNumberFragment
import net.broachcutter.vendorapp.screens.annular.specifications.AnnularCutterSpecificationFragment
import net.broachcutter.vendorapp.screens.cart.repo.BaseRoomCartRepository
import net.broachcutter.vendorapp.screens.cart.repo.BroachCutterCartRepository
import net.broachcutter.vendorapp.screens.cart.view.CartFragment
import net.broachcutter.vendorapp.screens.cart.view.CheckoutConfirmationFragment
import net.broachcutter.vendorapp.screens.cart.view.SubmitOrderDialog
import net.broachcutter.vendorapp.screens.cart.view.UpdatePaymentTermDialog
import net.broachcutter.vendorapp.screens.coupon.CouponDetailFragment
import net.broachcutter.vendorapp.screens.coupon.CouponListFragment
import net.broachcutter.vendorapp.screens.coupon.slide.CouponOfferDialogFragment
import net.broachcutter.vendorapp.screens.cutters.part_number.CutterPartNumberFragment
import net.broachcutter.vendorapp.screens.cutters.pilot_pins.CutterPilotPinsFragment
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterSpecificationsFragment
import net.broachcutter.vendorapp.screens.holesaws.home.HolesawsHomeFragment
import net.broachcutter.vendorapp.screens.holesaws.item_number.HolesawsItemNumberFragment
import net.broachcutter.vendorapp.screens.holesaws.specifications.HolesawsSpecificationFragment
import net.broachcutter.vendorapp.screens.home.HomeFragment
import net.broachcutter.vendorapp.screens.home.repo.BroachcutterUserRepository
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.login.first_login.FirstLoginFragment
import net.broachcutter.vendorapp.screens.login.forgot_password.ForgotPasswordFragment
import net.broachcutter.vendorapp.screens.login.main.LoginFragment
import net.broachcutter.vendorapp.screens.login.otp.SignupOTPFragment
import net.broachcutter.vendorapp.screens.login.phone_number.LinkPhoneNumberFragment
import net.broachcutter.vendorapp.screens.login.relog_otp.RelogOTPFragment
import net.broachcutter.vendorapp.screens.main.MainActivity
import net.broachcutter.vendorapp.screens.my_order_history.MyOrderHistoryFragment
import net.broachcutter.vendorapp.screens.my_order_history.OrderFragment
import net.broachcutter.vendorapp.screens.order_history.detail.OrderDetailFragment
import net.broachcutter.vendorapp.screens.pilot_pins.item_number.PilotPinsItemNumberFragment
import net.broachcutter.vendorapp.screens.pilot_pins.specifications.PilotPinsSpecificationFragment
import net.broachcutter.vendorapp.screens.product_list.BroachCutterProductRepository
import net.broachcutter.vendorapp.screens.product_list.ProductListFragment
import net.broachcutter.vendorapp.screens.search.ProductDetailDialogFragment
import net.broachcutter.vendorapp.screens.search.SearchActivity
import net.broachcutter.vendorapp.screens.settings.SettingsFragment
import net.broachcutter.vendorapp.screens.solid_drills.drill_bits.DrillBitsFragment
import net.broachcutter.vendorapp.screens.solid_drills.home.SolidDrillsHomeFragment
import net.broachcutter.vendorapp.screens.solid_drills.item_number.SolidDrillItemNumberFragment
import net.broachcutter.vendorapp.screens.solid_drills.solid_drill.SolidDrillFragment
import net.broachcutter.vendorapp.screens.spares.SparesFragment
import net.broachcutter.vendorapp.screens.splash.SplashActivity
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

/**
 * This is a Application-scoped Dagger component.
 *
 * Even though Dagger allows annotating a [Component] as a singleton, the code
 * itself must ensure only one instance of the class is created. This is done in [ ].
 *
 * todo figure out a better way than piling on inject methods here
 */
@Suppress("TooManyFunctions")
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class, CacheModule::class])
interface AppComponent {

    fun getApplication(): Application

    fun getRouter(): Router

    fun getNavigatorHolder(): NavigatorHolder

    fun inject(baseActivity: BaseActivity)

    fun inject(cartTypeConverters: CartTypeConverters)

    fun inject(baseRoomCartRepository: BaseRoomCartRepository)

    fun inject(productListFragment: ProductListFragment)

    fun inject(cutterPartNumberFragment: CutterPartNumberFragment)

    fun inject(searchActivity: SearchActivity)

    fun inject(cartFragment: CartFragment)

    fun inject(cutterSpecificationsFragment: CutterSpecificationsFragment)

    fun inject(sparesFragment: SparesFragment)

    fun inject(cutterPilotPinsFragment: CutterPilotPinsFragment)

    fun inject(accessoriesFragment: AccessoriesFragment)

    fun inject(homeFragment: HomeFragment)

    fun inject(myOrderHistoryFragment: MyOrderHistoryFragment)

    fun inject(orderFragment: OrderFragment)

    fun inject(orderDetailFragment: OrderDetailFragment)

    fun inject(linkPhoneNumberFragment: LinkPhoneNumberFragment)

    fun inject(loginRepository: LoginRepository)

    fun inject(signupOtpFragment: SignupOTPFragment)

    fun inject(firstLoginFragment: FirstLoginFragment)

    fun inject(forgotPasswordFragment: ForgotPasswordFragment)

    fun inject(loginFragment: LoginFragment)

    fun inject(relogOTPFragment: RelogOTPFragment)

    fun inject(mainActivity: MainActivity)

    fun inject(splashActivity: SplashActivity)

    fun inject(accountInfoFragment: AccountInfoFragment)

    fun inject(broachCutterProductRepository: BroachCutterProductRepository)

    fun inject(productDetailDialogFragment: ProductDetailDialogFragment)

    fun inject(updatePaymentTermDialog: UpdatePaymentTermDialog)

    fun inject(submitDialog: SubmitOrderDialog)

    fun inject(broachcutterUserRepository: BroachcutterUserRepository)

    fun inject(orderTypeConverters: OrderTypeConverters)

    fun inject(productTypeConverters: ProductTypeConverters)

    fun inject(solidDrillsHomeFragment: SolidDrillsHomeFragment)

    fun inject(solidDrillFragment: SolidDrillFragment)

    fun inject(drillsBitsFFragment: DrillBitsFragment)

    fun inject(solidDrillItemNumberFragment: SolidDrillItemNumberFragment)

    fun inject(annularCutterSpecificationFragment: AnnularCutterSpecificationFragment)

    fun inject(accessoriesCommonFragment: AccessoriesCommonFragment)

    fun inject(arborRadialSpecSpecificationFragment: ArborRadialSpecSpecificationFragment)

    fun inject(annularCuttingItemNumberFragment: AnnularCuttingItemNumberFragment)

    fun inject(pilotPinsSpecificationFragment: PilotPinsSpecificationFragment)

    fun inject(pilotPinsItemNumberFragment: PilotPinsItemNumberFragment)

    fun inject(holesawsSpecificationFragment: HolesawsSpecificationFragment)

    fun inject(holesawsItemNumberFragment: HolesawsItemNumberFragment)

    fun inject(holesawsHomeFragment: HolesawsHomeFragment)

    fun inject(broachCutterCartRepository: BroachCutterCartRepository)

    fun inject(arborRadialSpecItemNumberFragment: ArborRadialSpecItemNumberFragment)

    fun inject(dealerApplication: DealerApplication)

    fun inject(baseFragment: BaseFragment)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(checkoutConfirmationFragment: CheckoutConfirmationFragment)

    fun inject(couponListFragment: CouponListFragment)

    fun inject(couponOfferDialogFragment: CouponOfferDialogFragment)

    fun inject(couponDetailFragment: CouponDetailFragment)

//    fun <VM: ViewModel> inject(baseVMFragment: BaseVMFragment<VM>)
}
