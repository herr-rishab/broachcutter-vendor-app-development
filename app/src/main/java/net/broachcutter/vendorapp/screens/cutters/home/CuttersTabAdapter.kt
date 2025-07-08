package net.broachcutter.vendorapp.screens.cutters.home

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.screens.cutters.part_number.CutterPartNumberFragment
import net.broachcutter.vendorapp.screens.cutters.pilot_pins.CutterPilotPinsFragment
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterSpecificationsFragment

class CuttersTabAdapter(fragmentManager: FragmentManager, val context: Context) :
    FragmentStatePagerAdapter(fragmentManager) {

    companion object {
        const val TABS_COUNT = 3
    }

    override fun getItem(position: Int) = when (position) {
        0 -> CutterSpecificationsFragment()
        1 -> CutterPartNumberFragment()
        2 -> CutterPilotPinsFragment()
        else -> CutterPartNumberFragment()
    }

    override fun getCount() =
        TABS_COUNT

    override fun getPageTitle(position: Int): String = when (position) {
        0 -> context.getString(R.string.specifications)
        1 -> context.getString(R.string.part_number)
        2 -> context.getString(R.string.pilot_pins)
        else -> context.getString(R.string.part_number)
    }
}
