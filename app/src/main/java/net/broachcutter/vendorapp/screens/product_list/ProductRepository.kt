@file:Suppress("TooManyFunctions")
package net.broachcutter.vendorapp.screens.product_list

import androidx.lifecycle.LiveData
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.models.ArborRequestModel
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterShank
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType
import net.broachcutter.vendorapp.screens.spares.SparesSearchType

interface ProductRepository {

    fun genericSearch(
        query: String?
    ): LiveData<Resource<SearchResults>>

    fun getProductList(
        forceRefresh: Boolean = false,
        productType: ProductType
    ): LiveData<Resource<SearchResults>>

    fun getSpares(
        machinePartNumber: String,
        sparesSearchType: SparesSearchType,
        query: String
    ): LiveData<Resource<SearchResults>>

    fun searchCutters(articleNumber: String): LiveData<Resource<SearchResults>>

    fun searchPilotPins(
        length: Int?,
        diameter: Float?
    ): LiveData<Resource<SearchResults>>

    fun searchCutters(
        cutterType: CutterType,
        depthOfCut: Int?,
        diameter: Float?,
        cutterMaterial: CutterMaterial?,
        cutterShank: CutterShank?
    ): LiveData<Resource<SearchResults>>

    fun getCubAccessories(): LiveData<Resource<SearchResults>>

    fun getSuperAccessories(): LiveData<Resource<SearchResults>>

    fun getTridentAccessories(): LiveData<Resource<SearchResults>>

    fun getTitanAccessories(): LiveData<Resource<SearchResults>>

    fun getGeneralAccessories(): LiveData<Resource<SearchResults>>

    fun searchSolidDrills(diameter: Float?): LiveData<Resource<SearchResults>>

    fun searchDrillBits(diameter: Float?): LiveData<Resource<SearchResults>>

    fun searchSolidDrillAndDrillBits(itemNumber: String?): LiveData<Resource<SearchResults>>

    fun getArborsList(arborRequestModel: ArborRequestModel): LiveData<Resource<SearchResults>>

    fun getArborByItemNumber(itemNumber: String): LiveData<Resource<SearchResults>>

    fun searchForAnnularCutter(itemNumber: String): LiveData<Resource<SearchResults>>

    fun searchForPilotPin(itemNumber: String): LiveData<Resource<SearchResults>>

    fun getHolesawSpares(): LiveData<Resource<SearchResults>>

    fun searchHoleSaws(diameter: Int): LiveData<Resource<SearchResults>>

    fun searchForHolesaw(itemNumber: String): LiveData<Resource<SearchResults>>
}
