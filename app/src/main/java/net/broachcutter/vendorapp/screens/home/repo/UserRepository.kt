package net.broachcutter.vendorapp.screens.home.repo

import androidx.lifecycle.LiveData
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.CoroutineScope
import net.broachcutter.vendorapp.models.UpdatedOrderHistory
import net.broachcutter.vendorapp.models.UserDetail

interface UserRepository {

    /**
     * Provides the cached version of the user detail.
     */
    fun getCachedUserDetail(): UserDetail?

    /**
     * Provides the cached version of the user detail, or fetches it if there's none cached.
     */
    suspend fun getUserDetail(): UserDetail

    /**
     * Provides the updated user detail, whenever available.
     */
    fun getUserUpdates(): LiveData<UserDetail>

    /**
     * Fetches the user details from the server and caches the value.
     *
     * Should be called after authentication events, profile data checks and on checkout completion.
     */
    fun refreshUserDetails(): LiveData<Resource<UserDetail>>

    /**
     * Fetches the user details from the server and caches the value asynchronously.
     *
     * Should be called after authentication events, profile data checks and on checkout completion.
     *
     * Can throw an exception if one is encountered during the network call.
     */
    suspend fun refreshUserDetailsAsync(): UserDetail

    fun getOrderHistoryUpdated(
        month: Int,
        year: Int,
        forceRefresh: Boolean,
        scope: CoroutineScope
    ): LiveData<Resource<UpdatedOrderHistory>>

//    fun deleteOrderItem(orderNumber: String, partNumber: String): LiveData<Resource<Nothing>>

    fun resetPassword(): LiveData<Resource<Any>>
}
