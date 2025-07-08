package net.broachcutter.vendorapp.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import net.broachcutter.vendorapp.Screens
import ru.terrakok.cicerone.Router

abstract class BaseItemNumberViewModel(
    private val router: Router
) : ViewModel() {

    fun search(itemNumber: String) {
        // prepare args to pass on
        val args = productListArgs(itemNumber)
        // navigate to next screen
        router.navigateTo(Screens.ProductResultList(args))
    }

    abstract fun productListArgs(itemNumber: String): Bundle
}
