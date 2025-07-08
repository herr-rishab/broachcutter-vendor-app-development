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
import net.broachcutter.vendorapp.databinding.SubmitOrderDialogBinding
import net.broachcutter.vendorapp.screens.cart.view.OverduePaymentDialog.Companion.newInstance
import net.broachcutter.vendorapp.screens.cart.view.SubmitOrderDialog.Companion.newInstance

/**
 * Use [newInstance] to make an instance of this dialog.
 */
class SubmitOrderDialog : DialogFragment() {

    private var listener: SubmitDialogListener? = null

    private var _binding: SubmitOrderDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "SubmitOrderDialog"

        fun newInstance(listener: SubmitDialogListener): SubmitOrderDialog {
            val dialogFragment = SubmitOrderDialog()
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
        _binding = SubmitOrderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.yesButton.setOnClickListener {
            dismiss()
            listener?.onSubmitOrder()
        }
        binding.noButton.setOnClickListener {
            dismiss()
            listener?.onSubmitDialogDismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.update_all_payment_term_dialog, null)
        val dialog = Dialog(requireActivity(), R.style.DialogFragment)
        dialog.setContentView(view)
        return dialog
    }

    interface SubmitDialogListener {
        fun onSubmitOrder()

        fun onSubmitDialogDismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
