package net.broachcutter.vendorapp.screens.search

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import com.valartech.loadinglayout.LoadingLayout.Companion.COMPLETE
import com.valartech.loadinglayout.LoadingLayout.Companion.LOADING
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.databinding.ProductDetailDialogBinding
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.screens.cart.view.OverduePaymentDialog.Companion.newInstance
import net.broachcutter.vendorapp.screens.search.ProductDetailDialogFragment.Companion.newInstance
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.views.ProductCardView.Companion.maxQuantity
import org.jetbrains.anko.image
import javax.inject.Inject

/**
 * Use [newInstance] to make an instance of this dialog.
 */
class ProductDetailDialogFragment : DialogFragment() {

    private var _binding: ProductDetailDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductDetailViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ProductDetailViewModel::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<ProductDetailViewModel>

    private val selectedQuantity by lazy { binding.cardDetailContent.productNumberButton.quantity }

    companion object {
        const val TAG = "ProductDetailDialog"
        private const val ARGS_PRODUCT = "ARGS_PRODUCT"
        private const val IMAGE_ALPHA = 0.3f

        fun newInstance(product: Product): ProductDetailDialogFragment {
            val dialogFragment = ProductDetailDialogFragment()
            val args = bundleOf(ARGS_PRODUCT to product)
            dialogFragment.arguments = args
            return dialogFragment
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
    ): View? {
        _binding = ProductDetailDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // making sure that dialog layout params actually get applied
        val params = dialog!!.window!!.attributes
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as android.view.WindowManager.LayoutParams

        // todo figure out how to get rid of the little white corners
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog?.window?.setBackgroundDrawable(null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.product_detail_dialog, null)
        val dialog = Dialog(requireActivity(), R.style.DialogFragment)
        dialog.setContentView(view)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments?.get(ARGS_PRODUCT) as Product
        setupUI(requireActivity())
        bind(product, requireActivity())
    }

    private fun setupUI(context: Context) {
        val quantityList = ArrayList<String>()
        for (i in 1..maxQuantity) {
            quantityList.add(i.toString())
        }

        val quantityAdapter =
            ArrayAdapter(context, R.layout.quantity_spinner_item, R.id.quantity, quantityList)
        quantityAdapter.setDropDownViewResource(R.layout.quantity_spinner_dropdown_item)
        binding.cardDetailContent.quantitySpinner.adapter = quantityAdapter
        binding.dialogClose.setOnClickListener { dismiss() }
    }

    private fun bind(product: Product, context: Context) {

        binding.cardDetailContent.name.text = product.name
//        description.text = product.description
        binding.cardDetailContent.articleNumber.text =
            String.format(context.getString(R.string.article_number), product.partNumber)

        // set image
        if (!product.imageUrl.isNullOrEmpty()) {
            binding.cardDetailContent.productImage.alpha = IMAGE_ALPHA
            Picasso.get().load(product.imageUrl).into(binding.cardDetailContent.productImage)
        } else {
            binding.cardDetailContent.productImage.apply {
                alpha = 1f
                val imageRes = when (product.productType) {
                    ProductType.MACHINE -> null
                    ProductType.CUTTER -> null
                    ProductType.SPARE -> R.drawable.cogs_bg
                    ProductType.ACCESSORY -> null
                    ProductType.ARBOR -> null
                    ProductType.ADAPTOR -> null
                    else -> null
                }
                if (imageRes == null) {
                    image = null
                } else {
                    setImageResource(imageRes)
                }
            }
        }

        binding.cardDetailContent.addToCart.setOnClickListener {
            viewModel.addToCart(product, selectedQuantity).observe(
                viewLifecycleOwner
            ) {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.productDetailLoadingLayout.setState(COMPLETE)
                        toast(getString(R.string.added_to_cart))
                        dismiss()
                    }

                    Status.ERROR -> {
                        binding.productDetailLoadingLayout.setState(COMPLETE)
                        longToast(getString(R.string.error_adding_cart, it.message))
                    }

                    Status.LOADING -> binding.productDetailLoadingLayout.setState(LOADING)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
