// package net.broachcutter.vendorapp.views
//
// import android.content.Context
// import android.os.Build
// import android.util.AttributeSet
// import android.util.TypedValue
// import android.view.Gravity
// import android.view.View
// import android.widget.LinearLayout
// import android.widget.TextView
// import androidx.annotation.ColorInt
// import androidx.annotation.ColorRes
// import androidx.core.content.ContextCompat
// import com.valartech.commons.utils.extensions.spToPx
// import net.broachcutter.vendorapp.R
// import net.broachcutter.vendorapp.util.spToPx
//
// /**
// * Big rounded button with text in the centre, and optionally an arrow pointing right at the end.
// *
// *
// * Shows ripples when clicked on devices >= API 21, and turns grey when disabled.
// */
// class RoundedTextButton(context: Context, attrs: AttributeSet?) : TextView(context, attrs) {
//
//    private var text: String? = null
//    private val textSizePx: Float
//    private var bgColor: BgColor
//    private val letterSpacing: Float
//    @ColorInt
//    private val textColor: Int
//
//    enum class BgColor(val id: Int, @ColorRes val colorRes: Int) {
//        RED(BG_RED_ID, R.color.burnt_red),
//        BLUE(BG_BLUE_ID, R.color.marine);
//
//        companion object {
//            fun fromId(id: Int): BgColor {
//                for (color in values()) {
//                    if (color.id == id) return color
//                }
//                throw IllegalArgumentException()
//            }
//        }
//    }
//
//    init {
//        //get values from attrs
//        val a = context.obtainStyledAttributes(
//            attrs,
//            R.styleable.RoundedButton, 0, 0
//        )
//
//        text = if (a.hasValue(R.styleable.RoundedButton_text)) {
//            a.getString(R.styleable.RoundedButton_text)
//        } else {
//            context.getString(R.string.search)
//        }
//        bgColor = BgColor.fromId(a.getInt(R.styleable.RoundedButton_bg_color, BG_BLUE_ID))
//        textSizePx = a.getDimensionPixelSize(
//            R.styleable.RoundedButton_textSize,
//            DEFAULT_TEXT_SIZE_SP.spToPx().toInt()
//        ).toFloat()
//        letterSpacing = a.getFloat(R.styleable.RoundedButton_letterSpacing, 0f)
//        textColor = a.getColor(
//            R.styleable.RoundedButton_textColor,
//            ContextCompat.getColor(getContext(), DEFAULT_TEXT_COLOR)
//        )
//        a.recycle()
//
//
//
//        gravity = Gravity.CENTER
//
// //        View.inflate(context, R.layout.rounded_button, this)
//
//        applyBackgroundColour()
//        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
//        setTextColor(textColor)
//        setLetterSpacing(letterSpacing)
//    }
//
//    fun setTextColor(@ColorInt textColor: Int) {
//        textView.setTextColor(textColor)
//    }
//
//    fun setLetterSpacing(letterSpacing: Float) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            textView.letterSpacing = letterSpacing
//        }
//    }
//
//
//    fun setBgColor(bgColor: BgColor) {
//        this.bgColor = bgColor
//        applyBackgroundColour()
//    }
//
//    private fun applyBackgroundColour() {
//        when (bgColor) {
//            BG_BLUE_ID -> setBackgroundResource(R.drawable.blue_rounded_button_selector)
//        }
//    }
//
//    fun setArrowVisibility(visible: Boolean) {
//        hasArrow = visible
//        if (hasArrow) {
//            arrow!!.visibility = View.VISIBLE
//            textView!!.setGravity(Gravity.START)
//        } else {
//            arrow!!.visibility = View.GONE
//            textView!!.setGravity(Gravity.CENTER_HORIZONTAL)
//        }
//    }
//
//    companion object {
//
//        const val BG_BLUE_ID = 0
//        const val BG_RED_ID = 1
//
//        private const val DEFAULT_TEXT_SIZE_SP = 12.0f
//        @ColorRes
//        private val DEFAULT_TEXT_COLOR = R.color.white
//    }
// }
