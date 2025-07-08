package net.broachcutter.vendorapp.screens.holesaws.specifications

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class HolesawsSpecificationsViewModel @Inject constructor(
    private val context: Application,
    private val router: Router
) : ViewModel() {

    fun search(
        diameter: Int
    ) {
        // prepare args to pass on
        val title = context.getString(R.string.d_holesaw_diameter, diameter)
        val args = bundleOf(
            ProductListArgs.LIST_TYPE to ListType.HOLESAWS,
            ProductListArgs.TITLE to title,
            ProductListArgs.DIAMETER to diameter
        )
        // navigate to next screen
        router.navigateTo(Screens.ProductResultList(args))
    }
}
