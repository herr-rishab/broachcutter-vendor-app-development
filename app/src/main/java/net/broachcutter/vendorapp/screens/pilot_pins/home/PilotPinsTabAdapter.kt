package net.broachcutter.vendorapp.screens.pilot_pins.home

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.screens.pilot_pins.item_number.PilotPinsItemNumberFragment
import net.broachcutter.vendorapp.screens.pilot_pins.specifications.PilotPinsSpecificationFragment

class PilotPinsTabAdapter(fragmentManager: FragmentManager, val context: Context) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        const val TABS_COUNT = 2
    }

    override fun getItem(position: Int) = when (position) {
        0 -> PilotPinsSpecificationFragment()
        1 -> PilotPinsItemNumberFragment()
        else -> PilotPinsSpecificationFragment()
    }

    override fun getCount() = TABS_COUNT

    override fun getPageTitle(position: Int): String = when (position) {
        0 -> context.getString(R.string.specifications)
        1 -> context.getString(R.string.item_number)
        else -> context.getString(R.string.specifications)
    }
}
