package net.broachcutter.vendorapp.screens.pilot_pins.specifications

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class PilotPinsSpecificationsViewModel @Inject constructor(
    private val context: Application,
    private val router: Router
) : ViewModel() {

    fun search(
        diameter: Float? = null,
        length: Int? = null
    ) {
        // prepare args to pass on
        val title = generateTitle(diameter, length)
        val args = bundleOf(
            ProductListArgs.LIST_TYPE to ListType.PILOT_PINS,
            ProductListArgs.TITLE to title,
            ProductListArgs.DIAMETER to diameter,
            ProductListArgs.LENGTH to length
        )
        // navigate to next screen
        router.navigateTo(Screens.ProductResultList(args))
    }

    /**
     * Handles generating titles for search results for cutters.
     */
    private fun generateTitle(
        diameter: Float?,
        length: Int?
    ): String {
        return if (diameter == null && length == null) {
            // just "Pilot Pins" if no other specific attributes
            context.getString(R.string.pilot_pins)
        } else {
            val builder = StringBuilder()
            if (length != null && diameter == null) {
                // just length specified
                builder.append(context.getString(R.string.length_d, length))
            } else if (diameter != null && length == null) {
                // just diameter specified
                val formattedDiameter = String.format("%s", diameter)
                builder.append(context.getString(R.string.diameter_s_mm, formattedDiameter))
            } else if (length != null && diameter != null) {
                // both length and diameter
                val formattedDiameter = String.format("%s", diameter)
                builder.append(context.getString(R.string.dia_x_doc, formattedDiameter, length))
            }
            builder.append(" ").append(context.getString(R.string.pilot_pins))
            builder.toString()
        }
    }
}
