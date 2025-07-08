package net.broachcutter.vendorapp.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.models.UserDetail
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.screens.login.LoginRepository
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository,
    private val cartRepository: CartRepository,
    private val router: Router,
    private val analytics: Analytics
) : ViewModel() {

    init {
        // fetch user details when the user starts the main app
        GlobalScope.launch(Dispatchers.IO) {
            try {
                userRepository.refreshUserDetailsAsync()
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    val userDetail: UserDetail?
        get() = userRepository.getCachedUserDetail()

    val isCartEmpty: LiveData<Boolean> = cartRepository.isCartEmpty()

    fun signOut() {
        loginRepository.signOut()
        cartRepository.clearCart()
        router.newRootScreen(Screens.Splash())
        analytics.logout()
    }
}
