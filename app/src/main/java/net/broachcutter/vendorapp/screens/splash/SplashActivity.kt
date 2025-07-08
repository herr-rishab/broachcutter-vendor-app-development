package net.broachcutter.vendorapp.screens.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import net.broachcutter.vendorapp.BuildConfig
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.databinding.ActivitySplashBinding
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.util.Constants
import net.broachcutter.vendorapp.util.ViewModelFactory
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import timber.log.Timber
import javax.inject.Inject

class SplashActivity : BaseActivity(), UpdateDialogListener {

    companion object {
        const val ARG_UNAUTHENTICATED_USER = "ARG_UNAUTHENTICATED_USER"
    }

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SplashViewModel::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SplashViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getApplicationComponent().inject(this)
        binding.version.text = BuildConfig.VERSION_NAME
//        dynamicBaseUrl.CheckForBaseUrl()
        intent.extras?.let {
            val coupon: Coupon? = it.getParcelable(Constants.COUPON)
            if (coupon != null) {
                viewModel.coupon = it.getParcelable(Constants.COUPON)
                Timber.i("Coupon Id : ${viewModel.coupon?.id}")
            }
        }
        viewModel.runChecks()
        viewModel.showDialog.observe(
            this
        ) {
            when (it) {
                is UpdateDialog.Required -> RequiredUpdateDialogFragment().show(
                    supportFragmentManager,
                    RequiredUpdateDialogFragment::class.java.canonicalName
                )

                is UpdateDialog.Recommended -> {
                    RecommendedUpdateDialogFragment().show(
                        supportFragmentManager,
                        RecommendedUpdateDialogFragment::class.java.canonicalName
                    )
                }

                is UpdateDialog.Error -> ErrorUpdateDialogFragment(it.exception).show(
                    supportFragmentManager,
                    ErrorUpdateDialogFragment::class.java.canonicalName
                )
            }
        }
    }

    /**
     * This is to get the notification if the app is killed
     */
    override fun onNewIntent(intent: Intent) {
        intent.extras?.let {
            val coupon: Coupon? = it.getParcelable(Constants.COUPON)
            if (coupon != null) {
                viewModel.coupon = it.getParcelable(Constants.COUPON)
            }
        }
        super.onNewIntent(intent)
    }

    override fun onUpdateClick(dialog: DialogFragment) {
        // open play store
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (ex: ActivityNotFoundException) {
            // play store isn't installed?!
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    override fun onSkipClick(dialog: DialogFragment) {
        viewModel.runChecks(true)
    }

    override fun onRetryClick(dialog: DialogFragment) {
        viewModel.runChecks(false)
    }

    override val navigator = object : SupportAppNavigator(this, 0) {}
}
