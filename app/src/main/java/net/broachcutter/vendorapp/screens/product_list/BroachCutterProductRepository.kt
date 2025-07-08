package net.broachcutter.vendorapp.screens.product_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.valartech.commons.network.google.ApiResponse
import com.valartech.commons.network.google.RateLimiter
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.DealerApplication
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.db.ProductDao
import net.broachcutter.vendorapp.models.ArborRequestModel
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.network.CachedNetworkResource
import net.broachcutter.vendorapp.network.google.AppExecutors
import net.broachcutter.vendorapp.network.networkCall
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterShank
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType
import net.broachcutter.vendorapp.screens.spares.SparesSearchType
import net.broachcutter.vendorapp.util.AccessoriesOf
import net.broachcutter.vendorapp.util.BCResponse
import net.broachcutter.vendorapp.util.DI.INDUS_API
import net.broachcutter.vendorapp.util.mapToSearchResults
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@Suppress("TooManyFunctions")
class BroachCutterProductRepository @Inject constructor(
    @Named(INDUS_API) val broachCutterApi: BroachCutterApi,
    val appExecutors: AppExecutors,
    val productDao: ProductDao
) : ProductRepository {

    init {
        DealerApplication.INSTANCE.appComponent.inject(this)
    }

    companion object {
        private const val PRODUCT_CACHE_TIME_MIN = 10
    }

    /**
     * Keep product data in the db cache for 10 minutes.
     */
    private val productDataRateLimit =
        RateLimiter<String>(PRODUCT_CACHE_TIME_MIN, TimeUnit.MINUTES)

    override fun genericSearch(
        query: String?
    ): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.searchAsync(query, null)
        }.mapToSearchResults()

    /**
     * This is a generic cache function used with [productType].
     * [forceRefresh] - Fetch data from network forcefully
     */
    override fun getProductList(
        forceRefresh: Boolean,
        productType: ProductType
    ): LiveData<Resource<SearchResults>> {
        return object :
            CachedNetworkResource<SearchResults, BCResponse<List<Product>>>(
                appExecutors,
                dataMapper = {
                    SearchResults(null, it.data)
                }
            ) {

            override fun saveCallResult(item: BCResponse<List<Product>>) {
                item.data?.let {
                    productDao.insert(it)
                }
            }

            override fun shouldFetch(data: BCResponse<List<Product>>?): Boolean {
                return data == null || data.data.isNullOrEmpty() || forceRefresh ||
                    productDataRateLimit.shouldFetch(productType.name)
            }

            override fun loadFromDb(): LiveData<BCResponse<List<Product>>> =
                productDao.findByProductType(productType).map {
                    // Creating a DataResponse object to return for the UI
                    // Since we are saving only the ResponseNetworkType
                    BCResponse.createSuccessResponse(it)
                }

            override fun createCall(): LiveData<ApiResponse<BCResponse<List<Product>>>> {
                return broachCutterApi.getProductList(search = null, productType = productType.name)
            }
        }.asLiveData()
    }

    override fun searchCutters(articleNumber: String): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getCutterByPartNumAsync(articleNumber)
        }.mapToSearchResults()

    override fun searchCutters(
        cutterType: CutterType,
        depthOfCut: Int?,
        diameter: Float?,
        cutterMaterial: CutterMaterial?,
        cutterShank: CutterShank?
    ): LiveData<Resource<SearchResults>> {
        val apiDepthOfCut = if (depthOfCut != 0) depthOfCut else null
        val apiDiameter = if (diameter != 0f) diameter else null

        // todo search shank
        return networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getCuttersAsync(
                apiDepthOfCut,
                apiDiameter,
                cutterType.jsonValue,
                cutterMaterial?.jsonValue,
                cutterShank?.jsonValue
            )
        }.mapToSearchResults()
    }

    override fun searchPilotPins(
        length: Int?,
        diameter: Float?
    ): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getPilotPinsAsync(diameter, length)
        }.mapToSearchResults()

    override fun getSpares(
        machinePartNumber: String,
        sparesSearchType: SparesSearchType,
        query: String
    ): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getSparesAsync(query = query, spareOf = machinePartNumber)
        }.mapToSearchResults()

    override fun getCubAccessories() = getAccessories(AccessoriesOf.CUB)

    override fun getSuperAccessories() = getAccessories(AccessoriesOf.SUPER)

    override fun getTridentAccessories() = getAccessories(AccessoriesOf.TRIDENT)

    override fun getTitanAccessories() = getAccessories(AccessoriesOf.TITAN)

    override fun getGeneralAccessories() = getAccessories(AccessoriesOf.GENERAL)

    private fun getAccessories(machinePartNumber: String) =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getAccessoriesAsync(machinePartNumber)
        }.mapToSearchResults()

    override fun searchSolidDrills(diameter: Float?): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getSolidDrillsAsync(diameter)
        }.mapToSearchResults()

    override fun searchDrillBits(diameter: Float?): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getDrillBitsAsync(diameter)
        }.mapToSearchResults()

    override fun searchSolidDrillAndDrillBits(itemNumber: String?): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.searchAsync(itemNumber, null)
        }.mapToSearchResults()

    override fun getArborsList(arborRequestModel: ArborRequestModel): LiveData<Resource<SearchResults>> {
        return networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getArborsListAsync(
                arborRequestModel.morseTaper,
                arborRequestModel.shankDiameter,
                arborRequestModel.depthOfCut
            )
        }.mapToSearchResults()
    }

    override fun getArborByItemNumber(itemNumber: String): LiveData<Resource<SearchResults>> {
        return networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.searchAsync(itemNumber, ProductType.ARBOR.name)
        }.mapToSearchResults()
    }

    override fun searchForAnnularCutter(itemNumber: String): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.searchAsync(itemNumber, ProductType.CUTTER.name)
        }.mapToSearchResults()

    override fun searchForPilotPin(itemNumber: String): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.searchAsync(itemNumber, ProductType.PILOT_PINS.name)
        }.mapToSearchResults()

    override fun getHolesawSpares(): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getHolesawSparesAsync()
        }.mapToSearchResults()

    override fun searchHoleSaws(diameter: Int): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getHolesawsAsync(diameter)
        }.mapToSearchResults()

    override fun searchForHolesaw(itemNumber: String): LiveData<Resource<SearchResults>> =
        networkCall<BCResponse<List<Product>>, List<Product>> {
            client = broachCutterApi.getProductByPartNumberAsync(itemNumber)
        }.mapToSearchResults()
}
