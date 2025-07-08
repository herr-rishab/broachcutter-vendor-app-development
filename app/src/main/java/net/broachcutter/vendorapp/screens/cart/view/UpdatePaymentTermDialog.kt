package net.broachcutter.vendorapp.screens.cart.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.databinding.UpdateAllPaymentTermDialogBinding
import net.broachcutter.vendorapp.models.cart.PaymentTerm
import net.broachcutter.vendorapp.screens.cart.view.OverduePaymentDialog.Companion.newInstance
import net.broachcutter.vendorapp.screens.cart.view.SubmitOrderDialog.Companion.newInstance
import net.broachcutter.vendorapp.screens.cart.view.UpdatePaymentTermDialog.Companion.newInstance
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.PRODUCT_TYPE

/**
 * Use [newInstance] to make an instance of this dialog.
 */
class UpdatePaymentTermDialog : DialogFragment() {

    private var listener: UpdatePaymentTermDialogListener? = null
    lateinit var newTerm: PaymentTerm
    lateinit var productType: String

    private var _binding: UpdateAllPaymentTermDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "PaymentTermDialog"

        fun newInstance(listener: UpdatePaymentTermDialogListener): UpdatePaymentTermDialog {
            val dialogFragment = UpdatePaymentTermDialog()
            dialogFragment.listener = listener
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
    ): View {
        _binding = UpdateAllPaymentTermDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newTerm = arguments?.getParcelable(CartFragment.PAYMENT_TERM)!!
        productType = arguments?.getString(PRODUCT_TYPE).toString()
        binding.updatePaymentTermMessage.text = context?.getString(
            R.string.update_payment_term_detail,
            newTerm.id,
            productType,
            productType
        )
        binding.updatePaymentTermYesButton.setOnClickListener {
            dismiss()
            listener?.onUpdatePaymentTerm(newTerm, productType)
        }
        binding.updatePaymentTermNoButton.setOnClickListener {
            dismiss()
            listener?.onUpdatePaymentDialogDismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.update_all_payment_term_dialog, null)
        val dialog = Dialog(requireActivity(), R.style.DialogFragment)
        dialog.setContentView(view)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface UpdatePaymentTermDialogListener {
        fun onUpdatePaymentTerm(paymentTerm: PaymentTerm, productType: String)

        fun onUpdatePaymentDialogDismiss()
    }
}
