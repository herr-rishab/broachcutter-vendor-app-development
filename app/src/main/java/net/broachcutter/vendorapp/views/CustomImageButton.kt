package net.broachcutter.vendorapp.views

import android.content.Context
import android.util.AttributeSet

class CustomImageButton : androidx.appcompat.widget.AppCompatImageButton {

    constructor(context: Context) : super(context)
    constructor(
        context: Context,
        attrs: AttributeSet
    ) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun performClick(): Boolean {
        return super.performClick()
    }
}
