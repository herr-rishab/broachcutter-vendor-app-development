package net.broachcutter.vendorapp.screens.cutters.pilot_pins

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.DEPTH_OF_CUT
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.DIAMETER_F
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.LIST_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.TITLE
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class CutterPilotPinsViewModel @Inject constructor(
    private val context: Application,
    private val router: Router
) : ViewModel() {

    fun search(depthOfCut: Int?, diameter: Float?) {
        // prepare args to pass on
        val title = generateTitle(depthOfCut, diameter)
        val args = bundleOf(
            LIST_TYPE to ListType.PILOT_PINS,
            TITLE to title,
            DEPTH_OF_CUT to depthOfCut,
            DIAMETER_F to diameter
        )
        // navigate to next screen
        router.navigateTo(Screens.ProductResultList(args))
    }

    /**
     */
    private fun generateTitle(depthOfCut: Int?, diameter: Float?): String {
        return if (depthOfCut == null && diameter == null) {
            context.getString(R.string.all_pilot_pins)
        } else {
            val builder = StringBuilder()
            if (depthOfCut != null && diameter == null) {
                builder.append(context.getString(R.string.doc_d, depthOfCut))
            } else if (depthOfCut == null && diameter != null) {
                val formattedDiameter = String.format("%s", diameter)
                builder.append(context.getString(R.string.diameter_s_mm, formattedDiameter))
            } else if (depthOfCut != null && diameter != null) {
                val formattedDiameter = String.format("%s", diameter)
                builder.append(
                    context.getString(
                        R.string.dia_s_x_doc,
                        formattedDiameter,
                        depthOfCut
                    )
                )
            }
            builder.append(" ").append(context.getString(R.string.pilot_pins))
            builder.toString()
        }
    }
}
