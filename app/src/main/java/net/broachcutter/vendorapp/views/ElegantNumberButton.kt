package net.broachcutter.vendorapp.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import net.broachcutter.vendorapp.R
import org.jetbrains.anko.backgroundDrawable
import timber.log.Timber

@Suppress("TooManyFunctions")
class ElegantNumberButton : RelativeLayout {
    companion object {
        private const val TIME_DELAY_MILLIS: Long = 300
        private const val maxQuantity: Int = 999
        private const val minQuantity: Int = 1
    }

    private var attrs: AttributeSet? = null
    private var styleAttr = 0
    private var mListener: OnClickListener? = null
    private var currentQuantity = 0
    private lateinit var editTextView: EditText
    private var mOnValueChangeListener: OnValueChangeListener? = null
    lateinit var addBtn: CustomImageButton
    lateinit var subtractBtn: CustomImageButton
    lateinit var rootLayout: LinearLayout
    private var initialNumber = minQuantity

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.attrs = attrs
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.attrs = attrs
        styleAttr = defStyleAttr
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.elegant_number_button_layout, this)
        val res = resources
        val defaultColor = ResourcesCompat.getColor(res, R.color.colorPrimary, null)
        val defaultTextColor = ResourcesCompat.getColor(res, R.color.white, null)
        val defaultDrawable = ResourcesCompat.getDrawable(res, R.drawable.product_counter_bg, null)
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ElegantNumberButton,
            styleAttr, 0
        )
        val textSize = a.getDimension(R.styleable.ElegantNumberButton_textSize, 13f)
        val color = a.getColor(R.styleable.ElegantNumberButton_backGroundColor, defaultColor)
        val textColor = a.getColor(R.styleable.ElegantNumberButton_textColor, defaultTextColor)
        var drawable = a.getDrawable(R.styleable.ElegantNumberButton_backgroundDrawable)

        subtractBtn = findViewById(R.id.subtract_btn)
        addBtn = findViewById(R.id.add_btn)
        editTextView = findViewById(R.id.number_counter)
        rootLayout = findViewById(R.id.layout)

//        subtractBtn.setTextColor(textColor)
//        addBtn.setTextColor(textColor)
        editTextView.setTextColor(textColor)
//        subtractBtn.textSize = textSize
//        addBtn.textSize = textSize
        editTextView.textSize = textSize

        if (drawable == null) {
            drawable = defaultDrawable
        }
        drawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)

        backgroundDrawable = drawable
        editTextView.setText(initialNumber.toString())
        currentQuantity = initialNumber
        subtractBtn.setOnClickListener {
            performSubtract()
        }

        editTextView.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event.keyCode == KeyEvent.KEYCODE_ENTER &&
                event.action == KeyEvent.ACTION_DOWN
            ) {
                takeQuantity()
                editTextView.clearFocus()
            }
            false
        }

        editTextView.addTextChangedListener(quantityEditTextChangeListener)

        // this is for decrement item quantity continue on press minus button
        /*subtractBtn.setOnTouchListener(object : OnTouchListener {
        private var mHandler: Handler? = null
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mHandler == null) {
                        mHandler = Handler()
                        mHandler?.postDelayed(mAction, TIME_DELAY_MILLIS)
                        false
                    } else {
                        true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mHandler != null) {
                        mHandler?.removeCallbacks(mAction)
                        mHandler = null

                        false
                    } else
                        true
                }
                else -> {
                    false
                    // not sure how the below was recommended as a working solution
//                        v?.performClick()
//                        if (mHandler != null) {
//                            mHandler?.removeCallbacks(mAction)
//                            mHandler = null
//                            false
//                        } else
//                            true
                }
            }
        }

        var mAction = object : Runnable {
            override fun run() {
                performSubtract()
                mHandler?.postDelayed(this, TIME_DELAY_MILLIS)
            }
        }
    })*/

        addBtn.setOnClickListener {
            performAddition()
        }

        // this is for increment item quantity continue on press add button
        /*addBtn.setOnTouchListener(object : OnTouchListener {

            private var mHandler: Handler? = null
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (mHandler == null) {
                            mHandler = Handler()
                            mHandler?.postDelayed(mAction, TIME_DELAY_MILLIS)
                            false
                        } else {
                            true
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (mHandler != null) {
                            mHandler?.removeCallbacks(mAction)
                            mHandler = null

                            false
                        } else
                            true
                    }
                    else -> {
                        false
                        // not sure how the below was recommended as a working solution
//                        v?.performClick()
//                        if (mHandler != null) {
//                            mHandler?.removeCallbacks(mAction)
//                            mHandler = null
//                            false
//                        } else
//                            true
                    }
                }
            }

            var mAction = object : Runnable {
                override fun run() {
                    performAddition()
                    mHandler?.postDelayed(this, TIME_DELAY_MILLIS)
                }
            }
        })*/

        a.recycle()
    }

    /**
     * it called when edittext have focus and press back button
     * Note:- update quantity,clear focus and close keypad on back press button
     */
    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        Timber.i("dispatchKeyEventPreIme")
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            takeQuantity()
            clearFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.windowToken, 0)
            return true
        }
        return super.dispatchKeyEventPreIme(event)
    }

    private fun takeQuantity() {
        /**
         * quantity should be not empty
         * if empty then put minQuantity
         */
        if (currentQuantity> 0) {
            if (currentQuantity <= maxQuantity) {
                setNumber(currentQuantity, true)
            } else {
                setNumber(maxQuantity, true)
            }
        } else {
            setNumber(minQuantity, true)
        }
    }

    private fun performAddition() {
        val num = currentQuantity
        setNumber((num + 1), true)
    }

    private fun performSubtract() {
        val num = currentQuantity
        if (num > 1) {
            setNumber((num - 1), true)
        }
    }

    private fun callListener(view: View) {
        if (mListener != null) {
            mListener!!.onClick(view)
        }
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener!!.onValueChange(this, currentQuantity, currentQuantity)
        }
    }

    var quantity: Int
        get() = currentQuantity
        set(number) {
            editTextView.setText("$number")
        }

    private fun setNumber(number: Int, notifyListener: Boolean) {
        this.quantity = number
        if (notifyListener) {
            callListener(this)
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener?) {
        mListener = onClickListener
    }

    fun setOnValueChangeListener(onValueChangeListener: OnValueChangeListener?) {
        mOnValueChangeListener = onValueChangeListener
    }

    @FunctionalInterface
    interface OnClickListener {
        fun onClick(view: View?)
    }

    interface OnValueChangeListener {
        fun onValueChange(view: ElegantNumberButton?, oldValue: Int, newValue: Int)
    }

    fun updateColors(@ColorInt backgroundColor: Int, @ColorInt textColor: Int) {
        editTextView.setTextColor(textColor)
        backgroundDrawable?.colorFilter = PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC)
    }

    private val quantityEditTextChangeListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            val inputLength = s.toString().length
            if (inputLength> 0) {
                currentQuantity = Integer.parseInt(s.toString())
                if (inputLength > 2) {
                    editTextView.textSize = resources.getDimension(R.dimen._6ssp)
                } else {
                    editTextView.textSize = resources.getDimension(R.dimen._8ssp)
                }
            }
        }
    }
}
