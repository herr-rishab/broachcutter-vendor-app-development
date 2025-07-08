package net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.home

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.item_number.ArborRadialSpecItemNumberFragment
import net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.specification.ArborRadialSpecSpecificationFragment

class ArborRadialSpecTabAdapter(fragmentManager: FragmentManager, val context: Context) :
    FragmentStatePagerAdapter(fragmentManager) {

    companion object {
        const val TABS_COUNT = 2
    }

    override fun getItem(position: Int) = when (position) {
        0 -> ArborRadialSpecSpecificationFragment()
        1 -> ArborRadialSpecItemNumberFragment()
        else -> ArborRadialSpecSpecificationFragment()
    }

    override fun getCount() =
        TABS_COUNT

    override fun getPageTitle(position: Int): String = when (position) {
        0 -> context.getString(R.string.specifications)
        1 -> context.getString(R.string.item_number)
        else -> context.getString(R.string.specifications)
    }
}
