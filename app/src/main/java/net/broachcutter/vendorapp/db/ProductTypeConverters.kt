package net.broachcutter.vendorapp.db

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.models.Machine
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType
import javax.inject.Inject

class ProductTypeConverters {

    @Inject
    lateinit var moshi: Moshi
    private val machineListAdapter: JsonAdapter<List<Machine>>

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)

        val machineListType = Types.newParameterizedType(List::class.java, Machine::class.java)
        machineListAdapter = moshi.adapter(machineListType)
    }

    @TypeConverter
    fun productTypeToId(productType: ProductType?): Int? {
        return productType?.id
    }

    @TypeConverter
    fun productTypeFromId(id: Int?): ProductType? {
        return id?.let { ProductType.getProductType(id) }
    }

    @TypeConverter
    fun cutterTypeToId(cutterType: CutterType?): Int? {
        return cutterType?.id
    }

    @TypeConverter
    fun cutterTypeFromId(id: Int?): CutterType? {
        return id?.let { CutterType.getCutterType(id) }
    }

    @TypeConverter
    fun cutterMaterialToId(cutterMaterial: CutterMaterial?): Int? {
        return cutterMaterial?.id
    }

    @TypeConverter
    fun cutterMaterialFromId(id: Int?): CutterMaterial? {
        return id?.let { CutterMaterial.getCutterMaterial(id) }
    }

    @TypeConverter
    fun machineToPartNumber(machine: Machine?): String? {
        return machine?.partNumber
    }

    @TypeConverter
    fun machineFromPartNumber(partNumber: String?): Machine? {
        return partNumber?.let { Machine.getMachine(partNumber) }
    }

    @TypeConverter
    fun fromMachineList(machines: List<Machine>?): String? {
        return if (machines != null) {
            machineListAdapter.toJson(machines)
        } else null
    }

    @TypeConverter
    fun toMachineList(jsonString: String?): List<Machine>? {
        return if (jsonString != null) {
            machineListAdapter.fromJson(jsonString)
        } else null
    }
}
