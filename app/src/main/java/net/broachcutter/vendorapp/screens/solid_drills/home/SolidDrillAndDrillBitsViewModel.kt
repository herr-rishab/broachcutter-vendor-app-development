package net.broachcutter.vendorapp.screens.solid_drills.home

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class SolidDrillAndDrillBitsViewModel @Inject constructor(
    private val application: Application,
    private val router: Router
) :
    ViewModel() {
    companion object {
        const val SOLID_DRILL_MIN_MM = 3
        const val SOLID_DRILL_MAX_MM = 12
    }

    fun searchSolidDrillAndDrillBits(diameter: Float?, type: ListType) {
        val title: String = when (type) {
            ListType.SOLID_DRILL -> {
                application.getString(R.string.solid_drill_with_diameter, diameter.toString())
            }
            ListType.DRILL_BITS -> {
                application.getString(R.string.drill_bits_with_diameter, diameter.toString())
            }
            else -> {
                application.getString(R.string.solid_drill_with_diameter, diameter.toString())
            }
        }
        val args = bundleOf(
            ProductListArgs.LIST_TYPE to type,
            ProductListArgs.TITLE to title,
            ProductListArgs.DIAMETER to diameter
        )
        router.navigateTo(Screens.ProductResultList(args))
    }

    fun searchSolidDrillAndDrillBitsByItemNumber(itemNumber: String) {
        val title =
            application.getString(R.string.solid_drills_and_drill_bits_with_item_number, itemNumber)
        val type = ListType.SOLID_DRILL_AND_DRILL_BITS
        val args = bundleOf(
            ProductListArgs.LIST_TYPE to type,
            ProductListArgs.TITLE to title,
            ProductListArgs.MACHINE_PART_NUMBER to itemNumber
        )
        router.navigateTo(Screens.ProductResultList(args))
    }
}
