package net.broachcutter.vendorapp.screens.splash

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.network.NO_ACTIVE_CONNECTION
import net.broachcutter.vendorapp.network.REMOTE_CONFIG_FETCH_EXCEPTION
import net.broachcutter.vendorapp.network.TIMEOUT

abstract class BaseUpdateDialogFragment : DialogFragment() {

    internal lateinit var listener: UpdateDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as UpdateDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(("$context must implement UpdateDialogListener"))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
    }
}

class RequiredUpdateDialogFragment : BaseUpdateDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle(R.string.app_update_needed)
                .setMessage(R.string.required_update_message)
                .setPositiveButton(R.string.update_app) { _, _ ->
                    listener.onUpdateClick(this)
                }
            // Create the AlertDialog object and return it
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class RecommendedUpdateDialogFragment : BaseUpdateDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle(R.string.app_update_available)
                .setMessage(R.string.recommended_update_message)
                .setPositiveButton(R.string.update_app) { _, _ ->
                    listener.onUpdateClick(this)
                }
                .setNegativeButton(
                    R.string.skip_for_now
                ) { _, _ ->
                    listener.onSkipClick(this)
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // we can let the user skip through
        isCancelable = true
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onSkipClick(this)
    }
}

class ErrorUpdateDialogFragment(val exception: AppException) : BaseUpdateDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val errorMessage =
                if (exception.errorCode == NO_ACTIVE_CONNECTION ||
                    exception.errorCode == TIMEOUT ||
                    exception.errorCode == REMOTE_CONFIG_FETCH_EXCEPTION
                ) {
                    getString(R.string.active_internet_needed)
                } else {
                    exception.message
                }

            builder
                .setTitle(R.string.error_encountered)
                .setMessage(errorMessage)
                .setNeutralButton(
                    R.string.try_again
                ) { _, _ ->
                    listener.onRetryClick(this)
                }
            // Create the AlertDialog object and return it
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

interface UpdateDialogListener {
    fun onUpdateClick(dialog: DialogFragment)
    fun onSkipClick(dialog: DialogFragment)
    fun onRetryClick(dialog: DialogFragment)
}

sealed class UpdateDialog {
    object Required : UpdateDialog()
    object Recommended : UpdateDialog()
    class Error(val exception: AppException) : UpdateDialog()
}
