package net.broachcutter.vendorapp.repo

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.broachcutter.vendorapp.mocks.*
import net.broachcutter.vendorapp.models.ArborRequestModel
import net.broachcutter.vendorapp.models.Machine
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial.HSS
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial.TCT
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterShank
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType.*
import net.broachcutter.vendorapp.screens.product_list.ProductRepository
import net.broachcutter.vendorapp.screens.spares.SparesSearchType
import net.broachcutter.vendorapp.screens.spares.SparesSearchType.DESCRIPTION
import net.broachcutter.vendorapp.screens.spares.SparesSearchType.PART_NUMBER

object MockProductRepository : ProductRepository {

//    override fun getDrillingMachines(forceRefresh: Boolean): LiveData<Resource<SearchResults>> {
//        val liveData = MutableLiveData<Resource<SearchResults>>()
//        liveData.postValue(Resource.loading())
//        GlobalScope.launch {
//            delay(1000)
//            liveData.postValue(Resource.success(SearchResults(results = mockDrillingMachines)))
//        }
//        return liveData
//    }

    //    @SuppressLint("CheckResult")
//    override fun searchCutters(articleNumber: String): LiveData<Resource<Product>> {
//        val single = jsonbinApi.getCutterByPartNum()
//        cutterSearchResult.value = Resource.loading(null)
//        single.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : DisposableSingleObserver<Product>() {
//
//                override fun onSuccess(cutter: Product) {
//                    cutterSearchResult.value = Resource.success(cutter)
//                }
//
//                override fun onError(e: Throwable) {
//                    val exception = AppException(e)
//                    Timber.e(exception)
//                    cutterSearchResult.value = Resource.error(exception.message, null)
//                }
//            })
//
//        return cutterSearchResult
//    }

    override fun searchCutters(articleNumber: String): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(
                Resource.success(
                    SearchResults(
                        results = allMockCutters.filter {
                            it.name.contains(articleNumber)
                        }
                    )
                )
            )
        }
        return liveData
    }

    override fun searchCutters(
        cutterType: CutterType,
        depthOfCut: Int?,
        diameter: Float?,
        cutterMaterial: CutterMaterial?,
        cutterShank: CutterShank?
    ): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            val cutterList = when (cutterType) {
                ANNULAR -> {
                    when (cutterMaterial) {
                        HSS -> mockHssAnnularCutters
                        TCT -> mockTctAnnularCutters
                        else -> allMockCutters
                    }
                }
                INVALID -> listOf()
                HOLESAW -> TODO()
            }
            liveData.postValue(Resource.success(SearchResults(results = cutterList)))
        }
        return liveData
    }

    override fun searchPilotPins(
        length: Int?,
        diameter: Float?
    ): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockPilotPins)))
        }
        return liveData
    }

    override fun getSpares(
        machinePartNumber: String,
        sparesSearchType: SparesSearchType,
        query: String
    ): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            val machine = Machine.getMachine(machinePartNumber)
            val results = mockSpares
                .filter {
                    // todo - try removing bang-bang; temp fix
                    it.associatedMachines?.contains(machine)!!
                }.filter {
                    when (sparesSearchType) {
                        PART_NUMBER -> query == it.partNumber
                        DESCRIPTION -> it.name.contains(query, false)
                    }
                }
            liveData.postValue(Resource.success(SearchResults(results = results)))
        }
        return liveData
    }

    override fun getCubAccessories(): LiveData<Resource<SearchResults>> =
        getAccessories(
            Machine.CUB
        )

    override fun getSuperAccessories(): LiveData<Resource<SearchResults>> =
        getAccessories(
            Machine.SUPER
        )

    override fun getTridentAccessories(): LiveData<Resource<SearchResults>> =
        getAccessories(
            Machine.TRIDENT
        )

    override fun getTitanAccessories(): LiveData<Resource<SearchResults>> =
        getAccessories(
            Machine.TITAN
        )

    override fun getGeneralAccessories(): LiveData<Resource<SearchResults>> =
        getAccessories(null)

//    override fun getArbors(): LiveData<Resource<SearchResults>> {
//        val liveData = MutableLiveData<Resource<SearchResults>>()
//        liveData.postValue(Resource.loading())
//        GlobalScope.launch {
//            delay(1000)
//            liveData.postValue(Resource.success(SearchResults(results = mockArbors)))
//        }
//        return liveData
//    }
//
//    override fun getAdaptors(): LiveData<Resource<SearchResults>> {
//        val liveData = MutableLiveData<Resource<SearchResults>>()
//        liveData.postValue(Resource.loading())
//        GlobalScope.launch {
//            delay(1000)
//            liveData.postValue(Resource.success(SearchResults(results = mockAdaptors)))
//        }
//        return liveData
//    }

    override fun searchSolidDrills(diameter: Float?): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = listOf()))) // mockSolidDrills
        }
        return liveData
    }

    override fun searchDrillBits(diameter: Float?): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = listOf()))) // mockDrillBits
        }
        return liveData
    }

    override fun searchSolidDrillAndDrillBits(itemNumber: String?): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = listOf())))
            // mockSolidDrillAndDrillBits.filter {
            //                it.partNumber == itemNumber.toString()
            //            }
        }
        return liveData
    }

    override fun getArborsList(arborRequestModel: ArborRequestModel): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockArbors)))
        }
        return liveData
    }

    override fun getArborByItemNumber(itemNumber: String): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockArborsByItemNumber)))
        }
        return liveData
    }

    private fun getAccessories(machine: Machine?): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(
                Resource.success(
                    SearchResults(
                        results = mockAccessories.filter {
                            // todo - try removing bang-bang; temp fix
                            it.associatedMachines?.contains(machine)!!
                        }
                    )
                )
            )
        }
        return liveData
    }
    //    @SuppressLint("CheckResult")
//    override fun genericSearch(query: String): LiveData<Resource<SearchResults>> {
//
//        val single = jsonbinApi.search()
//        searchResult.value = Resource.loading(null)
//        single.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : DisposableSingleObserver<SearchResults>() {
//
//                override fun onSuccess(searchResultsWrapper: SearchResults) {
//                    searchResult.value = Resource.success(searchResultsWrapper)
//                }
//
//                override fun onError(e: Throwable) {
//                    val exception = AppException(e)
//                    Timber.e(exception)
//                    searchResult.value = Resource.error(exception.message, null)
//                }
//            })
//
//        return searchResult
//    }

//    override fun executeSetSearch(): LiveData<Resource<SearchResults>> {
//        searchType?.let {
//            val type: ListType = searchType as ListType
//            return when(type) {
//                ListType.DRILLING_MACHINES -> networkCall<SearchResults, SearchResults> {
//                    client = jsonbinApi.getCoroutineDrillingMachines()
//                }
//                ListType.SPARES -> networkCall<SearchResults, SearchResults> {
//                    client = jsonbinApi.getSpares()
//                }
//                ListType.SEARCH_DETAILS -> TODO()
//            }
//        } ?: throw RuntimeException("search type not set")
//    }

    @SuppressLint("CheckResult")
    override fun genericSearch(
        query: String?
    ): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(
                Resource.success(
                    SearchResults(
                        results = allMockProducts
                            .filter {
                                it.name.contains(query.toString(), true) || it.partNumber.contains(
                                    query.toString(),
                                    true
                                )
                            }
//                .filter { product ->
//                    productType?.let { it == product.productType } ?: true
//                }
                    )
                )
            )
        }
        return liveData
    }

    override fun getProductList(
        forceRefresh: Boolean,
        productType: ProductType
    ): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(
                Resource.success(
                    SearchResults(
                        results = allMockProducts
                            .filter { product ->
                                (productType == product.productType)
                            }
                    )
                )
            )
        }
        return liveData
    }

    override fun searchForAnnularCutter(itemNumber: String): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockHssAnnularCutters)))
        }
        return liveData
    }

    override fun searchForPilotPin(itemNumber: String): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockPilotPins)))
        }
        return liveData
    }

    override fun getHolesawSpares(): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockSpares)))
        }
        return liveData
    }

    override fun searchHoleSaws(diameter: Int): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockHssAnnularCutters)))
        }
        return liveData
    }

    override fun searchForHolesaw(itemNumber: String): LiveData<Resource<SearchResults>> {
        val liveData = MutableLiveData<Resource<SearchResults>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(SearchResults(results = mockHssAnnularCutters)))
        }
        return liveData
    }
}
