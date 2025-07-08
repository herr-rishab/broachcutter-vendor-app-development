package net.broachcutter.vendorapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.valartech.commons.network.google.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.broachcutter.vendorapp.mocks.mockUser
import net.broachcutter.vendorapp.models.UpdatedOrderHistory
import net.broachcutter.vendorapp.models.UserDetail
import net.broachcutter.vendorapp.network.AppException
import net.broachcutter.vendorapp.network.MISSING_DATA
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.updatedMockHistory

object MockUserRepository : UserRepository {

    override fun getUserUpdates(): LiveData<UserDetail> {
        val liveData = MutableLiveData<UserDetail>()
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(mockUser)
        }
        return liveData
    }

    override suspend fun refreshUserDetailsAsync(): UserDetail {
        return mockUser
    }

    override fun getCachedUserDetail(): UserDetail? = mockUser

    override fun getOrderHistoryUpdated(
        month: Int,
        year: Int,
        forceRefresh: Boolean,
        scope: CoroutineScope
    ): LiveData<Resource<UpdatedOrderHistory>> {
        val liveData = MutableLiveData<Resource<UpdatedOrderHistory>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(UpdatedOrderHistory(1, 2021, updatedMockHistory)))
        }
        return liveData
    }

    override fun resetPassword(): LiveData<Resource<Any>> {
        val resetLiveData = MutableLiveData<Resource<Any>>()
        resetLiveData.postValue(Resource.loading(null))
        GlobalScope.launch {
            val email = getUserEmail()
            email?.let {
                val auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resetLiveData.postValue(Resource.success(null))
                    } else {
                        val appException = AppException(task.exception)
                        resetLiveData.postValue(Resource.error(appException.message, null))
                    }
                }
            } ?: Resource.error(AppException(MISSING_DATA).message, null)
        }
        return resetLiveData
    }

    private fun getUserEmail(): String? {
        return mockUser.email
    }

    override suspend fun getUserDetail(): UserDetail = mockUser

    override fun refreshUserDetails(): LiveData<Resource<UserDetail>> {
        val liveData = MutableLiveData<Resource<UserDetail>>()
        liveData.postValue(Resource.loading())
        GlobalScope.launch {
            delay(1000)
            liveData.postValue(Resource.success(mockUser))
        }
        return liveData
    }
}
