package net.broachcutter.vendorapp.screens.product_list

import android.os.Bundle
import androidx.lifecycle.*
import com.valartech.commons.network.google.Resource
import com.valartech.commons.utils.exceptions.MissingArgumentException
import net.broachcutter.vendorapp.models.ArborRequestModel
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterShank
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType
import net.broachcutter.vendorapp.screens.product_list.ListType.*
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.ARBOR_REQUEST_MODEL
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.CUTTER_MATERIAL
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.CUTTER_SHANK
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.CUTTER_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.DEPTH_OF_CUT
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.DIAMETER
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.LENGTH
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.LIST_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.MACHINE_PART_NUMBER
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.PART_NUMBER
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.QUERY
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.SPARES_SEARCH_TYPE
import net.broachcutter.vendorapp.screens.spares.SparesSearchType
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val router: Router
) :
    ViewModel() {

//    private var _results : MutableLiveData<Resource<SearchResults>> = MutableLiveData()
//    val results: LiveData<Resource<SearchResults>> by lazy { getResultsLiveData(false) }

    private var _results: MediatorLiveData<Resource<SearchResults>> = MediatorLiveData()
    val results: LiveData<Resource<SearchResults>>
        get() = _results

    private lateinit var args: Bundle
    private var listType: ListType? = null

    fun init(args: Bundle) {
        this.args = args
        listType = args.getSerializable(LIST_TYPE) as ListType
        val initialResults = getResultsLiveData(false)
        _results.addSource(initialResults) {
            _results.value = it
        }
    }

    fun refreshResults() {
        val freshResults = getResultsLiveData(true)
        _results.addSource(freshResults) {
            _results.value = it
        }
    }

    @Suppress("ComplexMethod", "LongMethod")
    private fun getResultsLiveData(forceRefresh: Boolean): LiveData<Resource<SearchResults>> {
        return when (listType) {
            DRILLING_MACHINES -> productRepository.getProductList(
                forceRefresh,
                ProductType.MACHINE
            )
            SPARES -> {
                val machinePartNumber = args.getString(MACHINE_PART_NUMBER)
                val sparesSearchType =
                    SparesSearchType.values()[args.getInt(SPARES_SEARCH_TYPE)]
                val query = args.getString(QUERY)
                if (machinePartNumber == null || query == null) {
                    throw IllegalArgumentException("Missing arguments")
                } else {
                    productRepository.getSpares(machinePartNumber, sparesSearchType, query)
                }
            }
            PILOT_PINS -> {
                val diameter = args.getFloat(DIAMETER)
                val length = args.getInt(LENGTH)
                if (length == 0) {
                    productRepository.searchPilotPins(null, diameter)
                } else {
                    productRepository.searchPilotPins(length, diameter)
                }
            }
            CUTTER_SPECIFICATIONS -> {
                val cutterType = args.getSerializable(CUTTER_TYPE) as CutterType?
                val cutterMaterial = args.getSerializable(CUTTER_MATERIAL) as CutterMaterial?
                val cutterShank = args.getSerializable(CUTTER_SHANK) as CutterShank?
                val depthOfCut = args.getInt(DEPTH_OF_CUT)
                val diameter = args.getFloat(DIAMETER)

                productRepository.searchCutters(cutterType!!, depthOfCut, diameter, cutterMaterial, cutterShank)
            }
            SEARCH_DETAILS -> {
                val query = args.getString(QUERY)
                productRepository.genericSearch(query)
            }
            ACCESSORIES_CUB -> productRepository.getCubAccessories()
            ACCESSORIES_SUPER -> productRepository.getSuperAccessories()
            ACCESSORIES_TRIDENT -> productRepository.getTridentAccessories()
            ACCESSORIES_TITAN -> productRepository.getTitanAccessories()
            ACCESSORIES_GENERAL -> productRepository.getGeneralAccessories()
            ACCESSORIES_ARBORS -> {
                val arborRequestModel: ArborRequestModel = args.getParcelable(ARBOR_REQUEST_MODEL)!!
                productRepository.getArborsList(arborRequestModel)
            }
            ACCESSORIES_ARBORS_ITEM_NUMBER -> {
                val itemNumber = args.getString(PART_NUMBER)!!
                productRepository.getArborByItemNumber(itemNumber)
            }
            ACCESSORIES_ARBORS_EXTENSIONS -> productRepository.getProductList(
                forceRefresh,
                ProductType.ARBOR_EXTENSIONS
            )
            ACCESSORIES_ADAPTORS -> productRepository.getProductList(
                forceRefresh,
                ProductType.ADAPTOR
            )
            SOLID_DRILL -> {
                val diameter = args.getFloat(DIAMETER)
                productRepository.searchSolidDrills(diameter)
            }
            DRILL_BITS -> {
                val diameter = args.getFloat(DIAMETER)
                productRepository.searchDrillBits(diameter)
            }
            SOLID_DRILL_AND_DRILL_BITS -> {
                val itemNumber = args.getString(MACHINE_PART_NUMBER)!!
                productRepository
                    .searchSolidDrillAndDrillBits(itemNumber)
            }
            ANNULAR_CUTTER_ITEM_NUMBER -> {
                val itemNumber = args.getString(PART_NUMBER)!!
                productRepository.searchForAnnularCutter(itemNumber)
            }
            PILOT_PIN_ITEM_NUMBER -> {
                val itemNumber = args.getString(PART_NUMBER)!!
                productRepository.searchForPilotPin(itemNumber)
            }
            HOLESAWS -> {
                val diameter = args.getInt(DIAMETER)
                productRepository.searchHoleSaws(diameter)
            }
            HOLESAWS_ITEM_NUMBER -> {
                val itemNumber = args.getString(PART_NUMBER)!!
                productRepository.searchForHolesaw(itemNumber)
            }
            null -> throw MissingArgumentException("ListType needs to be set!")
        }
    }

    fun addToCart(product: Product, selectedQuantity: Int): LiveData<Resource<Any>> {
        Timber.i("addToCart: ${product.partNumber} $selectedQuantity")
        return cartRepository.addToOrUpdateCart(product, selectedQuantity)
    }

    fun goBack() {
        router.exit()
    }
}

enum class ListType {
    DRILLING_MACHINES,
    CUTTER_SPECIFICATIONS,
    ANNULAR_CUTTER_ITEM_NUMBER,
    PILOT_PINS,
    PILOT_PIN_ITEM_NUMBER,
    SPARES,
    ACCESSORIES_CUB,
    ACCESSORIES_SUPER,
    ACCESSORIES_TRIDENT,
    ACCESSORIES_TITAN,
    ACCESSORIES_GENERAL,
    ACCESSORIES_ARBORS,
    ACCESSORIES_ARBORS_ITEM_NUMBER,
    ACCESSORIES_ARBORS_EXTENSIONS,
    ACCESSORIES_ADAPTORS,
    SEARCH_DETAILS,
    SOLID_DRILL,
    DRILL_BITS,
    SOLID_DRILL_AND_DRILL_BITS,
    HOLESAWS,
    HOLESAWS_ITEM_NUMBER
}

object ProductListArgs {

    const val LIST_TYPE = "LIST_TYPE"
    const val TITLE = "TITLE"
    const val PART_NUMBER = "PART_NUMBER"

    // spares
    const val MACHINE_PART_NUMBER = "MACHINE_PART_NUMBER"
    const val SPARES_SEARCH_TYPE = "SPARES_SEARCH_TYPE"
    const val QUERY = "QUERY"

    // cutters
    const val CUTTER_TYPE = "CUTTER_TYPE"
    const val CUTTER_MATERIAL = "CUTTER_MATERIAL"
    const val CUTTER_SHANK = "CUTTER_SHANK"
    const val DEPTH_OF_CUT = "DEPTH_OF_CUT"
    const val DIAMETER = "DIAMETER"
    const val LENGTH = "LENGTH"

    const val DIAMETER_F = "DIAMETER_F"

    // general search
    const val PRODUCT_TYPE = "PRODUCT_TYPE"

    // general search
    const val ARBOR_REQUEST_MODEL = "ARBOR_REQUEST_MODEL"
}

// class ProductListViewModelFactory(val listType: ListType): ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
//            return LoggingClickCounterViewModel(loggingInterceptor) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
// }
