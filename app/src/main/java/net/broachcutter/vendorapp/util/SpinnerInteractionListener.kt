package net.broachcutter.vendorapp.util

import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView

/**
 * Fucking spinners
 * https://stackoverflow.com/a/28466764/3460025
 */
class SpinnerInteractionListener(val selectionAction: (View?, Int) -> Unit) :
    AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private var userSelect = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        userSelect = true
        return false
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if (userSelect) {
            selectionAction(view, pos)
            userSelect = false
        }
    }
}
