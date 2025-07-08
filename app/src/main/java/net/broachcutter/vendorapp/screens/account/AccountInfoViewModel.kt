package net.broachcutter.vendorapp.screens.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.valartech.commons.network.google.Resource
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.models.UserDetail
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.screens.login.LoginRepository
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AccountInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository,
    private val router: Router,
    private val analytics: Analytics
) : ViewModel() {

    val userDetail: LiveData<Resource<UserDetail>> = userRepository.refreshUserDetails()

    fun signOut() {
        loginRepository.signOut()
        router.newRootScreen(Screens.Splash())
        analytics.logout()
    }

    fun resetPassword() = userRepository.resetPassword()
}
