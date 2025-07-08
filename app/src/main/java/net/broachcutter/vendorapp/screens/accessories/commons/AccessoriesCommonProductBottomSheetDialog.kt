package net.broachcutter.vendorapp.screens.accessories.commons

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.databinding.AccessoriesCommonBottomsheetDialogBinding
import net.broachcutter.vendorapp.models.Product

class AccessoriesCommonProductBottomSheetDialog : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            product: Product,
            addToCart: (Product, Int) -> Unit
        ): AccessoriesCommonProductBottomSheetDialog {
            val dialog = AccessoriesCommonProductBottomSheetDialog()
            dialog.product = product
            dialog.addToCartCallback = addToCart
            return dialog
        }
    }

    var product: Product? = null
    var addToCartCallback: ((Product, Int) -> Unit)? = null

    private var _binding: AccessoriesCommonBottomsheetDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            if (bottomSheet != null)
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AccessoriesCommonBottomsheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.accessoriesCommonProductTitle.text = product?.name
        binding.accessoriesCommonProductPartNumber.text =
            getString(R.string.part_number_placeholder, product?.partNumber)
        binding.closeButton.setOnClickListener { dismiss() }
        binding.dialogAddToCart.setOnClickListener {
            val qty = binding.productNumberButton.quantity
            product?.let { addToCartCallback?.invoke(it, qty) }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
