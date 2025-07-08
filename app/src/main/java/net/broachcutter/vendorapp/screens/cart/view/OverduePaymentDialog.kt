package net.broachcutter.vendorapp.screens.cart.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import net.broachcutter.vendorapp.databinding.OverdueDialogBinding
import net.broachcutter.vendorapp.screens.cart.view.OverduePaymentDialog.Companion.newInstance

/**
 * Use [newInstance] to make an instance of this dialog.
 */
class OverduePaymentDialog : DialogFragment() {

    private var listener: OverdueDialogListener? = null

    private var _binding: OverdueDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "OverduePaymentDialog"

        fun newInstance(listener: OverdueDialogListener): OverduePaymentDialog {
            val dialogFragment = OverduePaymentDialog()
            dialogFragment.listener = listener
            return dialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val inflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            _binding = OverdueDialogBinding.inflate(inflater)
            binding.closeButton.setOnClickListener {
                dismiss()
                listener?.onOverdueDialogDismiss()
            }

            val builder = AlertDialog.Builder(activity)
                .setView(binding.root)
                .setCancelable(false)

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface OverdueDialogListener {
        fun onOverdueDialogDismiss()
    }
}
