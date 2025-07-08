package net.broachcutter.vendorapp.screens.cutters.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import net.broachcutter.vendorapp.databinding.CutterTabLayoutBinding

class CutterTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var viewPager: ViewPager? = null
    private val binding: CutterTabLayoutBinding =
        CutterTabLayoutBinding.inflate(LayoutInflater.from(context), this)

    init {
        if (isInEditMode) {
            setSelected(0)
        }
        for (i in 0..2) {
            getTabAt(i).setOnClickListener {
                setSelected(i)
            }
        }
    }

    fun setSelected(index: Int) {
        for (i in 0..2) {
            getTabAt(i).isSelected = index == i
        }
        viewPager?.currentItem = index
    }

    fun getSelected(): Int = viewPager?.currentItem ?: 0

    private fun getTabAt(index: Int): TextView {
        return when (index) {
            0 -> binding.specifications
            1 -> binding.partNumber
            2 -> binding.pilotPins
            else -> throw IllegalArgumentException("Index should be 0, 1 or 2")
        }
    }
}
