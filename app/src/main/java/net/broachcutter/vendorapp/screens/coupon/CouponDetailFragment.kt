package net.broachcutter.vendorapp.screens.coupon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.toast
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.coupon.Coupon
import net.broachcutter.vendorapp.models.coupon.CouponType
import net.broachcutter.vendorapp.models.coupon.PercentApplyTo
import net.broachcutter.vendorapp.util.Constants
import net.broachcutter.vendorapp.util.Constants.MAX_DATE
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.util.proxima_nova_alt_bold
import net.broachcutter.vendorapp.util.proxima_nova_alt_regular
import net.broachcutter.vendorapp.util.proxima_nova_semibold
import org.jetbrains.anko.support.v4.longToast
import org.threeten.bp.format.DateTimeFormatter
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class CouponDetailFragment : BaseFragment() {

    @Inject
    lateinit var listViewModelFactory: ViewModelFactory<CouponDetailsViewModel>

    @Inject
    lateinit var router: Router

    private val model: CouponDetailsViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            listViewModelFactory
        ).get(CouponDetailsViewModel::class.java)
    }

    companion object {
        /**
         * This bundle needs to contain [Coupon]!
         */
        fun newInstance(coupon: Coupon): CouponDetailFragment {
            val fragment = CouponDetailFragment()
            val bundle = bundleOf(Constants.COUPON to coupon)
            fragment.arguments = bundle
            return fragment
        }
    }

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
        val coupon = arguments?.getParcelable<Coupon>(Constants.COUPON)
        return ComposeView(requireContext()).apply {
            // Dispose the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    coupon?.let {
                        CouponDetailsScreen(it)
                    }
                }
            }
        }
    }

    @Suppress("MagicNumber", "LongMethod")
    @Composable
    fun CouponDetailsScreen(coupon: Coupon) {
        Scaffold(
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    CouponHeader(coupon.id)
                    CouponDescription(coupon.shortDesc)
                    CouponTermsCondition(coupon)
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(R.color.very_light_pink_three))
                                .padding(15.dp)
                        ) {
                            OutlinedButton(
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = colorResource(
                                        R.color.white
                                    )
                                ),

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .border(
                                        1.dp,
                                        color = colorResource(R.color.twilight_blue),
                                        RoundedCornerShape(8.dp)

                                    ),
                                onClick = {
                                    onApplyCouponAndAddItemsClick(coupon)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_coupon_cart),
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize),
                                    tint = colorResource(R.color.twilight_blue),
                                )
                                Spacer(modifier = Modifier.width(14.dp))

                                if (coupon.percentApplyTo == PercentApplyTo.PRODUCT_TYPE) {
                                    Text(
                                        text = stringResource(R.string.apply_coupon),
                                        color = colorResource(R.color.twilight_blue),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.sp,
                                        fontFamily = FontFamily(proxima_nova_alt_bold),
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.apply_coupon_and_add_items_to_cart),
                                        color = colorResource(R.color.twilight_blue),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.sp,
                                        fontFamily = FontFamily(proxima_nova_alt_bold),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }

    private fun onApplyCouponAndAddItemsClick(coupon: Coupon) {
        /**
         * apply coupon
         */
        model.saveCouponToPref(coupon)
        /**
         * add items to card
         */

        when (coupon.couponType) {
            CouponType.BUYXGETY -> {
                val xQuantity: Int =
                    coupon.xQuantity ?: 0
                val yQuantity = coupon.yQuantity ?: 0
                if (xQuantity > 0) {
                    coupon.xProduct?.let { addToCart(it, xQuantity) }
                }

                if (yQuantity > 0) {
                    coupon.yProduct?.let { addToCart(it, yQuantity) }
                }
            }

            CouponType.PERCENTAGE -> {
                coupon.percentageProduct?.let {
                    addToCart(it, coupon.percentMinQty)
                }
            }

            else -> {}
        }

        if (coupon.percentApplyTo == PercentApplyTo.PRODUCT_TYPE) {
            longToast(R.string.coupon_applied)
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        val addToCartResult = model.addToCart(product, quantity)
        addToCartResult.observe(
            viewLifecycleOwner
        ) {
            when (it?.status) {
                Status.SUCCESS -> {
                    toast(getString(R.string.added_to_cart))
                    Timber.i("Added to cart")
                }

                Status.ERROR -> {
                    longToast(getString(R.string.error_adding_cart, it.message))
                }

                else -> {}
            }
        }
    }
}

@Composable
fun CouponHeader(id: String) {
    return Card(
        shape = RoundedCornerShape(0.dp),
        backgroundColor = colorResource(R.color.reddy_brown_two),
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.ic_coupon_details_banner),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp),
            )
            Text(
                text = id,
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = FontFamily(proxima_nova_alt_bold),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(15.dp, 16.dp)
            )
        }
    }
}

@Composable
fun CouponDescription(shortDesc: String) {
    return Card(
        border = BorderStroke(1.dp, colorResource(R.color.warm_grey)),
        backgroundColor = colorResource(R.color.white),
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(R.string.coupon_description).uppercase(),
                fontSize = 12.sp,
                color = colorResource(R.color.twilight_blue),
                style = TextStyle(
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily(proxima_nova_alt_regular)
                ),
                modifier = Modifier.padding(10.dp, 10.dp)
            )
            Text(
                text = shortDesc,
                fontSize = 16.sp,
                style = TextStyle(fontFamily = FontFamily(proxima_nova_semibold)),
                color = colorResource(R.color.black),
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = 10.dp,
                    bottom = 20.dp
                )
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
fun CouponTermsCondition(coupon: Coupon) {
    val formattedValidTillDate =
        DateTimeFormatter.ofPattern("dd MMMM yyyy").format(coupon.validTill)
    return Card(
        border = BorderStroke(1.dp, colorResource(R.color.warm_grey)),
        backgroundColor = colorResource(R.color.white),
        modifier = Modifier.padding(15.dp)
    ) {

        Column {
            Text(
                text = stringResource(R.string.coupon_terms_conditions).uppercase(),
                fontSize = 12.sp,
                color = colorResource(R.color.twilight_blue),
                style = TextStyle(
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily(proxima_nova_alt_regular)
                ),
                modifier = Modifier.padding(10.dp, 10.dp)
            )
            if (coupon.tillStockLast || coupon.validTill.year == MAX_DATE) {
                Text(
                    text = stringResource(
                        R.string.offer_is_valid_till_stock_last,
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(fontFamily = FontFamily(proxima_nova_semibold)),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                )
            } else {
                Text(
                    text = stringResource(
                        R.string.offer_is_valid_till,
                        formattedValidTillDate
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(fontFamily = FontFamily(proxima_nova_semibold)),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                )
            }
            if (coupon.couponType == CouponType.BUYXGETY) {
                Text(
                    text = stringResource(
                        R.string.offer_is_only_valid_for_article_number,
                        "${coupon.xProduct?.partNumber}", "${coupon.yProduct?.partNumber}"
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = FontFamily(
                            proxima_nova_alt_regular
                        )
                    ),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(
                        R.string.x_quantity_and_y_quantity_together_for_the_coupon_to_be_applicable,
                        "${coupon.xQuantity}",
                        "${coupon.xProduct?.partNumber}",
                        "${coupon.yQuantity}",
                        "${coupon.yProduct?.partNumber}"
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = FontFamily(
                            proxima_nova_alt_regular
                        )
                    ),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.coupon_can_be_applied_multiple_times),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = FontFamily(
                            proxima_nova_alt_regular
                        )
                    ),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(
                        R.string.the_item_shall_be_discounted_at_a_discount_percentage,
                        "${coupon.yProduct?.partNumber}", "${coupon.yPercentDiscount}"
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = FontFamily(
                            proxima_nova_alt_regular
                        )
                    ),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.broachcutter_rights),
                    fontSize = 16.sp,
                    style = TextStyle(fontFamily = FontFamily(proxima_nova_alt_bold)),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
            } else if (coupon.couponType == CouponType.PERCENTAGE) {
                Text(
                    text = stringResource(
                        R.string.an_additional_discount_of_shall_be_applicable,
                        "${coupon.percentDisc}"
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = FontFamily(
                            proxima_nova_alt_regular
                        )
                    ),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )

                if (coupon.percentageProduct != null) {
                    Text(
                        text = stringResource(
                            R.string.offer_is_only_valid_for_article_number_percentage_type,
                            coupon.percentageProduct.partNumber,
                        ),
                        fontSize = 16.sp,
                        style = TextStyle(
                            fontFamily = FontFamily(
                                proxima_nova_alt_regular
                            )
                        ),
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .padding(10.dp, 10.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Text(
                        text = stringResource(
                            R.string.offer_is_valid_for_product_type_percentage_type,
                            "${coupon.percentProductType}"
                        ),
                        fontSize = 16.sp,
                        style = TextStyle(
                            fontFamily = FontFamily(
                                proxima_nova_alt_regular
                            )
                        ),
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .padding(10.dp, 10.dp)
                            .fillMaxWidth()
                    )
                }

                Text(
                    text = stringResource(
                        R.string.a_minimum_order_quantity_of_item_required,
                        "${coupon.percentMinQty}",
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = FontFamily(
                            proxima_nova_alt_regular
                        )
                    ),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(
                        R.string.non_credit_condition,
                        "${coupon.percentMinQty}",
                    ),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = FontFamily(
                            proxima_nova_alt_regular
                        )
                    ),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.broachcutter_rights),
                    fontSize = 16.sp,
                    style = TextStyle(fontFamily = FontFamily(proxima_nova_alt_bold)),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
