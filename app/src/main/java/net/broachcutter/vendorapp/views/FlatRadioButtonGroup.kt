package net.broachcutter.vendorapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.core.view.forEach

/**
 * Layout for holding a horizontal line of one or more [FlatRadioButton]s.
 *
 * Don't set onClickListeners on this view or its children. Instead, set [buttonSelectListener].
 */
class FlatRadioButtonGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var selectedChild: FlatRadioButton? = null

    var buttonSelectListener: ButtonSelectListener? = null

    init {
        orientation = HORIZONTAL
        if (isInEditMode) {
            val block1 = FlatRadioButton(context)
            block1.let {
                it.text = "ONE"
                it.isSelected = false
            }
            val block2 = FlatRadioButton(context)
            block2.let {
                it.text = "TWO"
                it.isSelected = true
            }
            val block3 = FlatRadioButton(context)
            block3.let {
                it.text = "THREE"
                it.isSelected = false
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initChildren()
    }

    private fun initChildren() {
        // get selected child
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is FlatRadioButton) {
                if (child.isSelected) {
                    selectedChild = child
                }
                child.setOnClickListener(this)
            } else {
                throw IllegalArgumentException("Children of this class should only be FlatRadioButton")
            }
        }
        setChildRounding()
        setChildSelectedStates()
    }

    private fun setChildSelectedStates() {
        forEach {
            it.isSelected = it == selectedChild // can't select multiple children
        }
    }

    /**
     * Set left and right rounded corners.
     */
    private fun setChildRounding() {

        for (i in 0 until childCount) {
            val roundLeft = i == 0
            val roundRight = i == childCount - 1
            val child = getChildAt(i)

            if (child is FlatRadioButton) {

                child.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
                child.setRounding(roundLeft, roundRight)
            }
        }
    }

    fun selectButton(@IdRes buttonId: Int) {
        val child = findViewById<FlatRadioButton>(buttonId)
        child?.let {
            selectedChild = child
            setChildSelectedStates()
        }
    }

    fun deselectAll() {
        selectedChild = null
        setChildSelectedStates()
    }

    override fun onClick(child: View?) {
        if (child is FlatRadioButton) {
            selectedChild = child
            setChildSelectedStates()
            buttonSelectListener?.onButtonSelect(selectedChild?.id)
        }
    }

    /**
     * Selects child with [text] or is contained within, or contains the child's text.
     */
    fun selectChildWithTextLike(text: String?) {
        if (text == null) return
        for (i in 0 until childCount) {
            val child = getChildAt(i) as FlatRadioButton
            val childText = child.text.toString()
            if (childText == text || text.contains(childText) || childText.contains(text)) {
                selectedChild = child
                setChildSelectedStates()
                return
            }
        }
    }

    interface ButtonSelectListener {
        fun onButtonSelect(buttonId: Int?)
    }
}
