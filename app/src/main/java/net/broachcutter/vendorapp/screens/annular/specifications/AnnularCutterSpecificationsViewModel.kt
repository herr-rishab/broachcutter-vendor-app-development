package net.broachcutter.vendorapp.screens.annular.specifications

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterShank
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AnnularCutterSpecificationsViewModel @Inject constructor(
    private val context: Application,
    private val router: Router
) : ViewModel() {

    fun search(
        cutterMaterial: CutterMaterial,
        cutterShank: CutterShank? = null,
        depthOfCut: Int? = null,
        diameter: Float? = null
    ) {
        // prepare args to pass on
        val title = generateTitle(CutterType.ANNULAR, cutterMaterial, depthOfCut, diameter)
        val args = bundleOf(
            ProductListArgs.LIST_TYPE to ListType.CUTTER_SPECIFICATIONS,
            ProductListArgs.TITLE to title,
            ProductListArgs.CUTTER_TYPE to CutterType.ANNULAR,
            ProductListArgs.CUTTER_MATERIAL to cutterMaterial,
            ProductListArgs.CUTTER_SHANK to cutterShank,
            ProductListArgs.DEPTH_OF_CUT to depthOfCut,
            ProductListArgs.DIAMETER to diameter
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
