package net.broachcutter.vendorapp.screens.solid_drills.home

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.screens.solid_drills.drill_bits.DrillBitsFragment
import net.broachcutter.vendorapp.screens.solid_drills.item_number.SolidDrillItemNumberFragment
import net.broachcutter.vendorapp.screens.solid_drills.solid_drill.SolidDrillFragment

class SolidDrillsHomeTabAdapter(fragmentManager: FragmentManager, val context: Context) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        const val TABS_COUNT = 3
    }

    override fun getItem(position: Int) = when (position) {
        0 -> SolidDrillFragment()
        1 -> DrillBitsFragment()
        2 -> SolidDrillItemNumberFragment()
        else -> SolidDrillFragment()
    }

    override fun getCount() =
        TABS_COUNT

    override fun getPageTitle(position: Int): String = when (position) {
        0 -> context.getString(R.string.solid_drill)
        1 -> context.getString(R.string.drill_bits)
        2 -> context.getString(R.string.item_number)
        else -> context.getString(R.string.solid_drill)
    }
}
