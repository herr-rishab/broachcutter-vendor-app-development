package net.broachcutter.vendorapp.screens.accessories.arbor_radial_spec.item_number

import android.app.Application
import android.os.Bundle
import androidx.core.os.bundleOf
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseItemNumberViewModel
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class ArborRadialSpecItemNumberViewModel @Inject constructor(
    private val context: Application,
    router: Router
) : BaseItemNumberViewModel(router) {
    override fun productListArgs(itemNumber: String): Bundle {
        val title = context.getString(R.string.search_for_part, itemNumber)
        return bundleOf(
            ProductListArgs.LIST_TYPE to ListType.ACCESSORIES_ARBORS_ITEM_NUMBER,
            ProductListArgs.TITLE to title,
            ProductListArgs.PART_NUMBER to itemNumber
        )
    }
}
