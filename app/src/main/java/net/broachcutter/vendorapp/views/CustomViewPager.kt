package net.broachcutter.vendorapp.views

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.relex.circleindicator.CircleIndicator

/**
 * Fix for IllegalArgumentException: No view found for id for fragment — ViewPager
 *
 * From: https://stackoverflow.com/a/19900206/3460025
 */
class CustomViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var pagerAdapter: PagerAdapter? = null
    var indicator: CircleIndicator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (pagerAdapter != null) {
            super.setAdapter(pagerAdapter)
            indicator?.setViewPager(this)
        }
    }

    fun storeAdapter(pagerAdapter: PagerAdapter) {
        this.pagerAdapter = pagerAdapter
    }

    fun storeIndicator(indicator: CircleIndicator) {
        this.indicator = indicator
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        // do nothing
    }
}
