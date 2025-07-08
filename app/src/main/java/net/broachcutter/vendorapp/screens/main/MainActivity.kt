@file:Suppress("TooManyFunctions")

package net.broachcutter.vendorapp.screens.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.valartech.commons.utils.extensions.formatINRwithPrefix
import com.valartech.commons.utils.extensions.longToast
import net.broachcutter.vendorapp.BuildConfig
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.databinding.ActivityMainBinding
import net.broachcutter.vendorapp.di.FirebaseHolder
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.screens.cart.view.CartFragment
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs
import net.broachcutter.vendorapp.util.Constants
import net.broachcutter.vendorapp.util.Miscellaneous
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.getDeviceDebugInfo
import net.broachcutter.vendorapp.views.NotificationDotDrawable
import org.jetbrains.annotations.NotNull
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MainViewModel>

    @set:Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    lateinit var analytics: Analytics

    @set:Inject
    lateinit var firebaseHolder: FirebaseHolder

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private var cartIndicator: NotificationDotDrawable? = null
    private var showCartIndicator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (BuildConfig.DEBUG) {
            binding.navMenu.inflateMenu(R.menu.debug_nav_menu)
        } else {
            binding.navMenu.inflateMenu(R.menu.nav_menu)
        }

        getApplicationComponent().inject(this)

        setupActionBar()
        setupDrawer()
        handleHamburgerMenuAndBackButton()

        // register callbacks for nav menu
        binding.navMenu.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            binding.drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.home -> router.newRootScreen(Screens.Home())
                R.id.order_history -> router.navigateTo(Screens.OrderHistorySummary())
                R.id.account_info -> router.navigateTo(Screens.AccountInfo())
                R.id.settings -> router.navigateTo(Screens.Settings())
                R.id.send_feedback -> sendFeedback()
                R.id.copy_token -> copyTokenToClipboard()
                R.id.sign_out -> viewModel.signOut()
            }

            true
        }
        binding.appVersion.text = BuildConfig.VERSION_NAME

        intent.extras?.let {
            val coupon: Coupon? = it.getParcelable(Constants.COUPON)
            if (it.getSerializable(ProductListArgs.LIST_TYPE) == ListType.SEARCH_DETAILS) {
                // we've gotten here from the search activity
                router.replaceScreen(Screens.ProductResultList(it))
            } else if (coupon != null) {
                // We have coupon id, navigate to CouponDetails screen
                router.replaceScreen(Screens.Home())
                router.navigateTo(Screens.CouponDetails(coupon))
            }
        } ?: if (savedInstanceState == null) {
            // add in "default" fragment
            router.replaceScreen(Screens.Home())
        } else {
        }
        viewModel.isCartEmpty.observe(
            this
        ) {
            showCartIndicator = !it
            cartIndicator?.setVisible(showCartIndicator, false)
        }
    }

    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Miscellaneous.SUPPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getSupportInfo())
        }
        startActivity(Intent.createChooser(intent, getString(R.string.email_chooser)))
    }

    private fun getSupportInfo(): String {
        var message = getDeviceDebugInfo(this)
        message += "\n--- DO NOT CHANGE ANY INFO ABOVE THIS LINE ---"
        message += "\nPlease add your feedback here:\n"
        return message
    }

    private fun copyTokenToClipboard() {
        val token = sharedPreferences.getString(Constants.PREFS_TOKEN, null)
        token?.let {
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Token", it)
            clipboard.setPrimaryClip(clip)
            longToast("Token copied to clipboard")
        } ?: longToast("Error copying token to clipboard")
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = ""

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    private fun setupDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbarLayout.toolbar,
            R.string.open_drawer,
            R.string.closed_drawer
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.setToolbarNavigationClickListener {
            if (!drawerToggle.isDrawerIndicatorEnabled) {
                // Back button is currently seen
                router.exit()
            }
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerOpened(drawerView: View) {
                analytics.onNavDrawerShown()
                // set dealer name and credit
                viewModel.userDetail?.let {
                    // TODO figure out how to render these
//                    binding.drawerLayout.dealerName?.text = it.name
                    val credit = it.credit.availableCredit.formatINRwithPrefix(this@MainActivity)
//                    dealerCredit?.text = getString(R.string.available_credit_limit, credit)
                }
            }
        })
    }

    /**
     * Set HamburgerMenu button to a top-level destination
     * Top level destination is HomeFragment
     */
    private fun handleHamburgerMenuAndBackButton() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                drawerToggle.isDrawerIndicatorEnabled = true
            } else {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                drawerToggle.isDrawerIndicatorEnabled = false
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            R.id.menu_search -> {
                analytics.searchIconClick()
                router.navigateTo(Screens.Search())
                true
            }

            R.id.menu_cart -> {
                val lastCommand = navigator.lastCommand
                if (lastCommand is Forward && lastCommand.screen is Screens.Cart) {
                    router.exit()
                } else {
                    router.navigateTo(Screens.Cart())
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val filter = menu.findItem(R.id.menu_cart)
        val icon = filter.icon as LayerDrawable
        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_badge)
        cartIndicator = if (reuse != null && reuse is BadgeDrawable) {
            reuse as NotificationDotDrawable
        } else {
            NotificationDotDrawable()
        }
        cartIndicator?.setVisible(showCartIndicator, false)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, cartIndicator)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onBackPressed() {
        router.exit()
    }

    override val navigator = MainNavigator()

    inner class MainNavigator : SupportAppNavigator(
        this,
        R.id.main_container
    ) {
        var lastCommand: Command? = null

        override fun setupFragmentTransaction(
            command: Command,
            currentFragment: Fragment?,
            nextFragment: Fragment?,
            fragmentTransaction: FragmentTransaction
        ) {
            if (nextFragment is CartFragment) {
                fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
            }
            // as per docs, command will be Forward or Replace only
            val screen = if (command is Forward) {
                command.screen as SupportAppScreen
            } else {
                (command as Replace).screen as SupportAppScreen
            }
            analytics.navigatedTo(this@MainActivity, screen)
        }

        override fun applyCommands(commands: Array<out @NotNull Command>) {
            super.applyCommands(commands)
            lastCommand = commands[0]
        }

        override fun applyCommand(command: Command) {
            super.applyCommand(command)
            lastCommand = command
            // todo delete if above analytics code works correctly
//            when(command) {
//                is Back -> {
//
//                }
//                is BackTo -> {
//                    //hasn't really been used in this app
//                    analytics.navigatedTo(this@MainActivity, command.screen!!)
//                }
//                is Forward -> {
//                    analytics.navigatedTo(this@MainActivity, command.screen)
//                }
//                is Replace -> {
//                    analytics.navigatedTo(this@MainActivity, command.screen)
//                }
//            }
        }
    }
}
