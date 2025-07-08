package net.broachcutter.vendorapp.models

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.network.DataResponse
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType

/**
 * Todo implement inheritance here rather than stuffing all the attributes into one.
 * Painful to do currently since we would have to switch from a data to a sealed class, which comes
 * with its own restrictions.
 */
@Parcelize
@Entity
@JsonClass(generateAdapter = true)
data class Product(
    @PrimaryKey
    @SerializedName("PartNumber", alternate = ["partNumber"])
    @Json(name = "PartNumber")
    val partNumber: String,
    @SerializedName("Name", alternate = ["productName"])
    @Json(name = "Name")
    val name: String,
    @SerializedName("Description")
    @Json(name = "Description")
    val description: String? = null,
    @SerializedName("ProductType", alternate = ["productType"])
    @Json(name = "ProductType")
    var productType: ProductType,
    @SerializedName("ImageURL")
    @Json(name = "ImageURL")
    val imageUrl: String? = null,
    @SerializedName("TaxRate", alternate = ["taxRate"])
    @Json(name = "TaxRate")
    val taxRate: Float = 18f,
    @SerializedName("cutterType")
    @Json(name = "cutterType")
    val cutterType: CutterType? = null,
    @SerializedName("DepthOfCut")
    @Json(name = "DepthOfCut")
    val depthOfCut: Int? = null,
    @SerializedName("Diameter")
    @Json(name = "Diameter")
    val diameter: Float? = null,
    @SerializedName("cutterMaterial")
    @Json(name = "cutterMaterial")
    val cutterMaterial: CutterMaterial? = null,
    @SerializedName("pilotPinType")
    @Json(name = "pilotPinType")
    val pilotPinType: String? = null,
    val associatedMachines: List<Machine>? = null
) : Parcelable, DataResponse<Product> {
    override fun retrieveData() = this
}

// data class Cutter(
//    @PrimaryKey
//    @Json(name = "PartNumber") val partNumber: String,
//    @Json(name = "Name") val name: String?,
//    @Json(name = "Description") val description: String?,
//    @Json(name = "ProductType") val productType: ProductType?,
//    @Json(name = "ImageURL") val imageUrl: String?,
//    @Json(name = "TaxRate") val taxRate: Int?
// ): Product()

@Suppress("MagicNumber")
enum class ProductType(val id: Int, @StringRes val nameRes: Int, @ColorRes val colorRes: Int) {
    @Json(name = "Machines")
    @SerializedName("Machines")
    MACHINE(1, R.string.magnetic_drilling_machines, R.color.burnt_red),

    @SerializedName("Cutters")
    CUTTER(2, R.string.cutters, R.color.marine),

    @SerializedName("Spares")
    SPARE(3, R.string.spares, R.color.brownish_grey_two),

    @SerializedName("Accessories")
    ACCESSORY(4, R.string.accessories, R.color.dark_grey),

    @SerializedName("Arbors")
    ARBOR(5, R.string.arbors, R.color.dark_grey),

    @SerializedName("Arbor Extensions")
    ARBOR_EXTENSIONS(6, R.string.arbors_extensions, R.color.dark_grey),

    @SerializedName("Adaptors")
    ADAPTOR(7, R.string.adaptors, R.color.dark_grey),

    @SerializedName("DrillBits")
    DRILL_BITS(8, R.string.drill_bits, R.color.dark_grey),

    @SerializedName("Solid Drill")
    SOLID_DRILL(9, R.string.solid_drill, R.color.dark_grey),

    @SerializedName("Pilot Pins")
    PILOT_PINS(10, R.string.pilot_pins, R.color.dark_grey),
    INVALID(-1, R.string.invalid, R.color.burnt_red);

    val group: ProductType
        get() = when (this) {
            MACHINE -> MACHINE
            CUTTER -> CUTTER
            SPARE -> SPARE
            ACCESSORY -> ACCESSORY
            ARBOR -> ACCESSORY
            ARBOR_EXTENSIONS -> ACCESSORY
            ADAPTOR -> ADAPTOR
            DRILL_BITS -> DRILL_BITS
            SOLID_DRILL -> SOLID_DRILL
            PILOT_PINS -> ACCESSORY
            INVALID -> INVALID
        }

    companion object {
        fun getGroups(): List<ProductType> {
            val groups = ArrayList<ProductType>()
            enumValues<ProductType>().forEach {
                if (it != INVALID) {
                    groups.add(it.group)
                }
            }
            return groups.distinct()
        }

        fun getProductType(id: Int): ProductType {
            for (productType in values()) {
                if (productType.id == id) {
                    return productType
                }
            }
            return INVALID
        }
    }
}

// sealed class ProductType1(@StringRes open val nameRes: Int, @ColorRes val colorRes: Int)
// @JsonClass(generateAdapter = true)
// class Machine : ProductType1(R.string.magnetic_drilling_machines, R.color.burnt_red)
// @JsonClass(generateAdapter = true)
// class Cutter : ProductType1(R.string.cutters, R.color.marine)
// @JsonClass(generateAdapter = true)
// class Spare : ProductType1(R.string.spares, R.color.brownish_grey_two)
// @JsonClass(generateAdapter = true)
// open class Accessory : ProductType1(R.string.accessories, R.color.dark_grey)
// @JsonClass(generateAdapter = true)
// class Arbor : Accessory() {
//    override val nameRes = R.string.arbors
// }
// @JsonClass(generateAdapter = true)
// class Adaptor : Accessory() {
//    override val nameRes = R.string.adaptors
// }
//
// class ProductTypeAdapter {
//
//    @ToJson
//    fun toJson(productType: ProductType) = when (productType) {
//        ProductType.MACHINE -> "machine"
//        ProductType.CUTTER -> "cutter"
//        ProductType.SPARE -> "spare"
//        ProductType.ACCESSORY -> "accessory"
//        ProductType.ARBOR -> "arbor"
//        ProductType.ADAPTOR -> "adaptor"
//        ProductType.INVALID -> "invalid"
//    }
//
//    @FromJson
//    fun fromJson(productType: String) = when (productType) {
//        "machine" -> ProductType.MACHINE
//        "cutter" -> ProductType.CUTTER
//        "spare" -> ProductType.SPARE
//        "accessory" -> ProductType.ACCESSORY
//        "arbor" -> ProductType.ARBOR
//        "adaptor" -> ProductType.ADAPTOR
//        else -> ProductType.INVALID
//    }
// }

data class ProductListWrapper(val products: List<Product>?) : DataResponse<ProductListWrapper> {
    override fun retrieveData() = this
}

@JsonClass(generateAdapter = true)
data class SearchResults(
    var title: String? = null,
    @SerializedName("Products") @Json(name = "Products") var results: List<Product>?
) : DataResponse<SearchResults> {
    override fun retrieveData() = this
}
