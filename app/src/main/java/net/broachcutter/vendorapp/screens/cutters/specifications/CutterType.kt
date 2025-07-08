@file:Suppress("MagicNumber")

// hardcoded values for cutters needed
package net.broachcutter.vendorapp.screens.cutters.specifications

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.R

// sealed class CutterType(@StringRes val labelRes: Int)
//
// class Annular(
//    var diameter: Float?,
//    val cutterMaterials: List<CutterMaterial> = Arrays.asList(
//        CutterMaterial.TCT,
//        CutterMaterial.HSS
//    ),
//    val depthOfCut: List<Int> = Arrays.asList(25, 35, 50, 75)
// ) :
//    CutterType(R.string.annular_cutter)
//
// class Rail(
//    var diameter: Float?,
//    val cutterMaterials: List<CutterMaterial> = Arrays.asList(
//        CutterMaterial.TCT,
//        CutterMaterial.HSS,
//        CutterMaterial.SOLID_DRILL
//    ),
//    val depthOfCut: List<Int> = Arrays.asList(25, 50)
// ) : CutterType(R.string.rail_annular_cutter)
//
// class Holesaw(
//    var diameter: Float?,
//    val depthOfCut: List<Int> = Arrays.asList(5)
// ) : CutterType(R.string.holesaws)
//
// class SolidDrill(
//    var diameter: List<Int> = Arrays.asList(5, 6, 7, 8, 9, 10),
//    val depthOfCut: List<Int> = Arrays.asList(35)
// ) : CutterType(R.string.solid_drills)
enum class CutterType(
    val id: Int,
    @StringRes
    val labelRes: Int,
    @LayoutRes
    val layoutRes: Int,
    val jsonValue: String
) {
    @SerializedName("ANNULAR")
    ANNULAR(1, R.string.annular_cutter, R.layout.cutter_annular, "ANNULAR"),

    @SerializedName("HOLESAW")
    HOLESAW(2, R.string.holesaws, R.layout.cutter_rail, "HOLESAW"),

    @SerializedName("INVALID")
    INVALID(-1, R.string.invalid, R.layout.cutter_solid_drills, "");

    override fun toString(): String {
        return DealerApplication.INSTANCE.getString(labelRes)
    }

    companion object {
        fun getCutterType(id: Int): CutterType {
            for (cutterType in CutterType.values()) {
                if (cutterType.id == id) {
                    return cutterType
                }
            }
            return INVALID
        }
    }
}

enum class CutterMaterial(
    val id: Int,
    @StringRes val labelRes: Int,
    val jsonValue: String
) {
    @SerializedName("TCT")
    TCT(1, R.string.tct_caps, "TCT"),

    @SerializedName("HSS")
    HSS(2, R.string.hss_caps, "HSS");

    companion object {
        fun getCutterMaterial(id: Int): CutterMaterial {
            for (cutterMaterial in values()) {
                if (cutterMaterial.id == id) {
                    return cutterMaterial
                }
            }
            return TCT
        }
    }
}

enum class CutterShank(
    val id: Int,
    @StringRes val labelRes: Int,
    val jsonValue: String
) {
    @SerializedName("Weldon")
    WELDON(1, R.string.weldon, "Weldon"),

    @SerializedName("Universal")
    UNIVERSAL(2, R.string.universal, "Universal");

    companion object {
        fun getCutterShank(name: String): CutterShank {
            for (shank in values()) {
                if (shank.name == name) {
                    return shank
                }
            }
            return WELDON
        }
    }
}
