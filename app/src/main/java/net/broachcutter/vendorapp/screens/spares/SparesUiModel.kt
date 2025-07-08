package net.broachcutter.vendorapp.screens.spares

import androidx.annotation.StringRes
import com.valartech.commons.network.google.Resource
import com.valartech.commons.network.google.Status
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.models.SearchResults

data class SparesUiModel(
    var sparesSearchType: SparesSearchType,
    var isSearchEnabled: Boolean,
    var machinesList: List<String>,
    var selectedMachinePosition: Int
)

enum class SparesSearchType(@StringRes val labelRes: Int, @StringRes val hintRes: Int) {
    PART_NUMBER(R.string.part_number, R.string.part_number_hint),
    DESCRIPTION(R.string.description, R.string.description_hint)
}

fun Resource<SearchResults>.convertToSparesUiModel(): Resource<SparesUiModel> {
    return when (this.status) {
        Status.SUCCESS -> {
            val machinesList = ArrayList<String>()
            this.data?.results?.forEach { product ->
                product.name.let { machinesList.add(it) }
            } ?: machinesList.add("")
            return Resource.success(
                SparesUiModel(
                    SparesSearchType.PART_NUMBER,
                    false,
                    machinesList,
                    0
                )
            )
        }
        Status.ERROR -> Resource(this.status, null, this.message)
        Status.LOADING -> Resource(this.status, null, this.message)
    }
}
