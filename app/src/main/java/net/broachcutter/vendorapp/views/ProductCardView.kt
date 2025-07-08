package net.broachcutter.vendorapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso
import com.valartech.commons.utils.extensions.dpToPx
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.ProductCardContentBinding
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.image
import org.jetbrains.anko.textColor

class ProductCardView : CardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        const val cardRadiusDp = 7.5f
        const val maxQuantity = 50
        const val productImageAlpha = 0.3f
    }

    val selectedQuantity
        get() = binding.productNumberButton.quantity

    val productNumberButton
        get() = binding.productNumberButton

    val addToCartButton
        get() = binding.addToCart

    var product: Product? = null
    var isModal: Boolean = false
        set(value) {
            field = value
            if (value) {
                binding.closeProductModal.visibility = View.VISIBLE
            }
        }

    private val binding: ProductCardContentBinding =
        ProductCardContentBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        radius = cardRadiusDp.dpToPx()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val quantityList = ArrayList<String>()
        for (i in 1..maxQuantity) {
            quantityList.add(i.toString())
        }

        val quantityAdapter =
            ArrayAdapter(context, R.layout.quantity_spinner_item, R.id.quantity, quantityList)
        quantityAdapter.setDropDownViewResource(R.layout.quantity_spinner_dropdown_item)
        binding.quantitySpinner.adapter = quantityAdapter
    }

    @Suppress("LongMethod")
    fun bind(product: Product) {
        this.product = product
        binding.name.text = product.name
        binding.articleNumber.text =
            String.format(context.getString(R.string.part_number_placeholder), product.partNumber)

        when (product.productType) {
            ProductType.MACHINE -> {
                binding.productRoot.backgroundColor =
                    ResourcesCompat.getColor(resources, R.color.marine, null)
                binding.productNumberButton.updateColors(
                    ResourcesCompat.getColor(resources, R.color.navy, null),
                    ResourcesCompat.getColor(resources, R.color.white, null)
                )
                binding.addToCart.textColor =
                    ResourcesCompat.getColor(resources, R.color.twilight_blue, null)
                binding.addToCart.setIconTintResource(R.color.twilight_blue)
                binding.addToCart.setStrokeColorResource(R.color.soft_blue_two)
            }

            ProductType.CUTTER -> {
                binding.productRoot.backgroundColor =
                    ResourcesCompat.getColor(resources, R.color.reddy_brown, null)
                binding.productNumberButton.updateColors(
                    ResourcesCompat.getColor(resources, R.color.deep_brown, null),
                    ResourcesCompat.getColor(resources, R.color.white, null)
                )
                binding.addToCart.textColor =
                    ResourcesCompat.getColor(resources, R.color.reddy_brown, null)
                binding.addToCart.setIconTintResource(R.color.reddy_brown)
                binding.addToCart.setStrokeColorResource(R.color.pale_red)
            }

            else -> {
                binding.productRoot.backgroundColor =
                    ResourcesCompat.getColor(resources, R.color.black_two, null)
                binding.productNumberButton.updateColors(
                    ResourcesCompat.getColor(resources, R.color.black, null),
                    ResourcesCompat.getColor(resources, R.color.white, null)
                )
                binding.addToCart.textColor =
                    ResourcesCompat.getColor(resources, R.color.black_two, null)
                binding.addToCart.setIconTintResource(R.color.black_two)
                binding.addToCart.setStrokeColorResource(R.color.brown_grey_three)
            }
        }

        // set image
        if (!product.imageUrl.isNullOrEmpty()) {
            binding.productImage.alpha = productImageAlpha
            Picasso.get().load(product.imageUrl).into(binding.productImage)
        } else {
            binding.productImage.apply {
                alpha = productImageAlpha
                val imageRes = when (product.productType) {
                    ProductType.MACHINE -> R.drawable.ic_combined_shape
                    ProductType.CUTTER -> R.drawable.home_tct
                    ProductType.SPARE -> R.drawable.gear
                    ProductType.ACCESSORY -> null
                    ProductType.ARBOR -> null
                    ProductType.ADAPTOR -> null
                    else -> R.drawable.cogs_bg
                }
                if (imageRes == null) {
                    image = null
                } else {
                    setImageResource(imageRes)
                }
            }
        }
    }
}
