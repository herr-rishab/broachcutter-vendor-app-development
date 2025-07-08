package net.broachcutter.vendorapp.screens.accessories

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.models.ArborRequestModel
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.SearchResults
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.product_list.ListType
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.ARBOR_REQUEST_MODEL
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.LIST_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.PRODUCT_TYPE
import net.broachcutter.vendorapp.screens.product_list.ProductListArgs.TITLE
import net.broachcutter.vendorapp.screens.product_list.ProductRepository
import net.broachcutter.vendorapp.util.RetriggerableLiveData
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AccessoriesViewModel @Inject constructor(
    private val context: Application,
    private val router: Router,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    fun getAccessories(listType: ListType, productType: ProductType) {
        val title = when (listType) {
            ListType.ACCESSORIES_CUB -> context.getString(R.string.cub_accessories)
            ListType.ACCESSORIES_SUPER -> context.getString(R.string.super_accessories)
            ListType.ACCESSORIES_TRIDENT -> context.getString(R.string.trident_accessories)
            ListType.ACCESSORIES_TITAN -> context.getString(R.string.titan_accessories)
            ListType.ACCESSORIES_GENERAL -> context.getString(R.string.general_accessories)
            ListType.ACCESSORIES_ARBORS -> context.getString(R.string.arbors)
            ListType.ACCESSORIES_ARBORS_EXTENSIONS -> context.getString(R.string.arbors_extensions)
            ListType.ACCESSORIES_ADAPTORS -> context.getString(R.string.adaptors)
            else -> {
                ""
            }
        }
        val args = bundleOf(
            LIST_TYPE to listType,
            TITLE to title,
            PRODUCT_TYPE to productType
        )
        // navigate to next screen
        router.navigateTo(Screens.ProductResultList(args))
    }

    fun onPilotPinsClick() {
        router.navigateTo(Screens.PilotPinsHome())
    }

    fun onCommonAccessoriesClick() {
        router.navigateTo(Screens.AccessoriesCommon())
    }

    fun onArborRadialSpecClick() {
        router.navigateTo(Screens.ArborRadialSpecHome())
    }

    fun onArborSearchClick(title: String, arborRequestModel: ArborRequestModel) {
        val args = bundleOf(
            LIST_TYPE to ListType.ACCESSORIES_ARBORS,
            TITLE to title,
            ARBOR_REQUEST_MODEL to arborRequestModel
        )
        // navigate to next screen
        router.navigateTo(Screens.ProductResultList(args))
    }

    fun getAccessories() {
        getCubAccessories.execute(Unit)
        getSuperAccessories.execute(Unit)
        getTridentAccessories.execute(Unit)
        getTitanAccessories.execute(Unit)
        getGeneralAccessories.execute(Unit)
    }

    fun addToCart(product: Product, quantity: Int): LiveData<Resource<Any>> {
        return cartRepository.addToOrUpdateCart(product, quantity)
    }

    var getCubAccessories = RetriggerableLiveData<Unit, Resource<SearchResults>> {
        productRepository.getCubAccessories()
    }

    var getSuperAccessories = RetriggerableLiveData<Unit, Resource<SearchResults>> {
        productRepository.getSuperAccessories()
    }

    var getTridentAccessories = RetriggerableLiveData<Unit, Resource<SearchResults>> {
        productRepository.getTridentAccessories()
    }

    var getTitanAccessories = RetriggerableLiveData<Unit, Resource<SearchResults>> {
        productRepository.getTitanAccessories()
    }

    var getGeneralAccessories = RetriggerableLiveData<Unit, Resource<SearchResults>> {
        productRepository.getGeneralAccessories()
    }
}
