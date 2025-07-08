package net.broachcutter.vendorapp.screens.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.network.google.Status
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentHome4Binding
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.screens.coupon.slide.CouponOfferDialogFragment
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.setSafeOnClickListener
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHome4Binding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    companion object {
        const val TAG = "HomeFragment"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HomeViewModel>

    @Inject
    lateinit var router: Router

    private lateinit var couponList: List<Coupon>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHome4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickListener()
        registerObserver()
    }

    private fun initViews() {
        binding.cvOfferScheme.visibility = View.GONE
    }

    private fun registerObserver() {
        model.userDetail.observe(
            viewLifecycleOwner
        ) {
            setName(it.name)
        }

        model.allCouponList.observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    resource.data?.let {
                        couponList = it
                        if (couponList.isEmpty()) {
                            binding.cvOfferScheme.visibility = View.GONE
                        } else {
                            binding.cvOfferScheme.visibility = View.VISIBLE
                            showBanner(couponList)
                        }
                    }
                }

                Status.ERROR -> {
                }

                Status.LOADING -> {
                    // show loading animation
                }

                else -> {}
            }
        }
    }

    private fun showBanner(couponList: List<Coupon>) {
        val newCoupons = model.compareCoupon(couponList)
        Timber.i("New coupons: $newCoupons")
        val couponListFromPref = model.getCouponListFromPref()
        Timber.i("couponListFromPref: $couponListFromPref")
        if (newCoupons.isNotEmpty() || couponListFromPref.isEmpty()) {
            model.saveNewCouponToSharedPref(newCoupons)
            CouponOfferDialogFragment.newInstance(newCoupons)
                .show(requireActivity().supportFragmentManager, CouponOfferDialogFragment.TAG)
        }
    }

    private fun clickListener() {
        binding.machinesLayout.setSafeOnClickListener { model.onMachinesClick() }
        binding.annularCuttersLayout.setSafeOnClickListener { model.onCuttersClick() }
        binding.sparesLayout.setSafeOnClickListener { model.onSparesClick() }
        binding.accessoriesLayout.setSafeOnClickListener { model.onAccessoriesClick() }
        binding.solidDrillsLayout.setSafeOnClickListener { model.onSolidDrillsClick() }
        binding.holesawsLayout.setSafeOnClickListener { model.onHolesawsClick() }
        binding.homeOrderHistory.setSafeOnClickListener { model.onOrderHistoryClick() }
        binding.cvOfferScheme.setSafeOnClickListener {
            if (::couponList.isInitialized) {
                model.onOfferSchemeClick(couponList, TAG)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setName(name: String?) {
        if (binding.homeTitle.text.isBlank()) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            binding.homeTitle.startAnimation(animation)
        }
        binding.homeTitle.text = name
    }
}
