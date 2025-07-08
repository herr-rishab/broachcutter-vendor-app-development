package net.broachcutter.vendorapp.screens.cutters.specifications

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.CUTTER_MATERIAL
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.CUTTER_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.DEPTH_OF_CUT
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.DIAMETER
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.LIST_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.TITLE
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@Deprecated("")
class CutterSpecificationsViewModel @Inject constructor(
    private val context: Application,
    private val router: Router
) : ViewModel() {

    fun search(
        cutterType: CutterType,
        cutterMaterial: CutterMaterial? = null,
        depthOfCut: Int? = null,
        diameter: Float? = null
    ) {
        // prepare args to pass on
        val title = generateTitle(cutterType, cutterMaterial, depthOfCut, diameter)
        val args = bundleOf(
            LIST_TYPE to ListType.CUTTER_SPECIFICATIONS,
            TITLE to title,
            CUTTER_TYPE to cutterType,
            CUTTER_MATERIAL to cutterMaterial,
            DEPTH_OF_CUT to depthOfCut,
            DIAMETER to diameter
        )
        // navigate to next screen
        router.navigateTo(Screens.ProductResultList(args))
    }

    /**
     * Handles generating titles for search results for cutters.
     */
    @Suppress("ComplexMethod") // can't really simplify further. Added comments
    private fun generateTitle(
        cutterType: CutterType,
        cutterMaterial: CutterMaterial?,
        depthOfCut: Int?,
        diameter: Float?
    ): String {
        return if (cutterMaterial == null && depthOfCut == null && diameter == null) {
            // just "Annular Cutter" if no other specific attributes
            context.getString(cutterType.labelRes)
        } else {
            val builder = StringBuilder()
            if (depthOfCut != null && diameter == null) {
                // just DoC specified
                builder.append(context.getString(R.string.doc_d, depthOfCut))
            } else if (depthOfCut == null && diameter != null) {
                // just diameter specified
                val formattedDiameter = String.format("%s", diameter)
                builder.append(context.getString(R.string.diameter_s_mm, formattedDiameter))
            } else if (depthOfCut != null && diameter != null) {
                // both DoC and diameter
                val formattedDiameter = String.format("%s", diameter)
                builder.append(context.getString(R.string.dia_x_doc, formattedDiameter, depthOfCut))
            }
            cutterMaterial?.let { builder.append(" ").append(context.getString(it.labelRes)) }
            cutterType.let { builder.append(" ").append(context.getString(it.labelRes)) }
            builder.toString()
        }
    }
}
