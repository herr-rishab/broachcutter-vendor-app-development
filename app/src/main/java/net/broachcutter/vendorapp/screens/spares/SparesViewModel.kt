package net.broachcutter.vendorapp.screens.spares

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.LIST_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.MACHINE_PART_NUMBER
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.QUERY
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.SPARES_SEARCH_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.TITLE
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class SparesViewModel @Inject constructor(
    private val application: Application,
    private val router: Router
) :
    ViewModel() {

    val uiModel: LiveData<Resource<SparesUiModel>>
        get() = _uiModel
    private val _uiModel: MediatorLiveData<Resource<SparesUiModel>> = MediatorLiveData()
    private val viewModelState: MutableLiveData<Resource<SparesUiModel>?> = MutableLiveData()
    private var machineProductList: List<String> =
        listOf("CUB_XL", "SUPER_XL", "TITAN_XL", "CUB", "SUPER", "TRIDENT", "TITAN")

    init {
        val initialState = MutableLiveData<Resource<SparesUiModel>>()
        initialState.postValue(
            Resource.success(
                SparesUiModel(
                    SparesSearchType.PART_NUMBER,
                    false,
                    machineProductList,
                    0
                )
            )
        )
        _uiModel.addSource(initialState) { _uiModel.value = it }
        _uiModel.addSource(viewModelState) { _uiModel.value = it }
    }

    fun onTextChanged(text: CharSequence?) {
        val state = _uiModel.value
        state?.data?.isSearchEnabled = !text.isNullOrEmpty()
        viewModelState.value = state
    }

    fun search(query: String) {
        _uiModel.value?.data?.run {
            // prepare args to pass on
            val machineName = machinesList[selectedMachinePosition]
            val title = application.getString(R.string.spares_title, machineName, query)
            val args = bundleOf(
                LIST_TYPE to ListType.SPARES,
                TITLE to title,
                MACHINE_PART_NUMBER to machineName,
                SPARES_SEARCH_TYPE to sparesSearchType.ordinal,
                QUERY to query
            )
            // navigate to next screen
            router.navigateTo(Screens.ProductResultList(args))
        }
    }

    fun setSearchType(searchType: SparesSearchType) {
        val state = _uiModel.value
        state?.data?.sparesSearchType = searchType
        viewModelState.value = state
    }

    fun onMachineSelected(position: Int) {
        val state = _uiModel.value
        state?.data?.selectedMachinePosition = position
        viewModelState.value = state
    }
}
