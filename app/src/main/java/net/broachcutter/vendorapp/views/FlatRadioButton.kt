package net.broachcutter.vendorapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER
import net.broachcutter.vendorapp.R
import org.jetbrains.anko.backgroundResource

/**
 * A single button within a [FlatRadioButtonGroup].
 */
class FlatRadioButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.grey_background_selector)
        gravity = CENTER
    }

    fun setRounding(hasRoundedLeft: Boolean, hasRoundedRight: Boolean) {
        backgroundResource = if (hasRoundedLeft && !hasRoundedRight) {
            R.drawable.left_rounded_grey_background_selector
        } else if (!hasRoundedLeft && hasRoundedRight) {
            R.drawable.right_rounded_grey_background_selector
        } else if (hasRoundedLeft && hasRoundedRight) {
            R.drawable.dark_grey_rounded_rectangle5
        } else {
            R.drawable.grey_background_selector
        }
    }
}
