package net.broachcutter.vendorapp.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.valartech.commons.network.google.*
import net.broachcutter.vendorapp.network.google.AppExecutors
import timber.log.Timber

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 * You can read more about it in the [Architecture Guide]
 * (https://developer.android.com/jetpack/docs/guide#best-practices).
 *
 * We have tweaked the actual example a little.
 * Renaming [ResultType] to [ResponseUIType] and [RequestType] to [ResponseNetworkType]
 *
 * @param <ResponseUIType> - This is a generic that we would use for the UI.
 * @param <ResponseNetworkType> - Generic that we get from the API call.
 * @param dataMapper - Used to map network data to UI objects, if necessary.
</RequestType></ResponseUIType> */
abstract class CachedNetworkResource<ResponseUIType, ResponseNetworkType>
@MainThread constructor(
    private val appExecutors: AppExecutors,
    private val dataMapper: (ResponseNetworkType) -> ResponseUIType = { it as ResponseUIType }
) {

    private val result = MediatorLiveData<Resource<ResponseUIType>>()

    init {
        result.value = Resource.loading()
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                Timber.d("Fetching from network")
                fetchFromNetwork(dbSource)
            } else {
                Timber.d("Serving cached value")
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(dataMapper.invoke(newData)))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResponseUIType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    /**
     * Fetching data from the network.
     * But while we load, we also supply the stale data from the database and then update the data from network
     */
    private fun fetchFromNetwork(dbSource: LiveData<ResponseNetworkType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(dataMapper.invoke(newData)))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {
                    appExecutors.diskIO().execute {
                        saveCallResult(processResponse(response))
                        appExecutors.mainThread().execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb()) { newData ->
                                setValue(Resource.success(dataMapper.invoke(newData)))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    appExecutors.mainThread().execute {
                        // reload from disk whatever we had
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.success(dataMapper.invoke(newData)))
                        }
                    }
                }
                is ApiErrorResponse<ResponseNetworkType> -> {
                    onFetchFailed(response.errorMessage)
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.errorMessage, dataMapper.invoke(newData)))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed(errorMessage: String) {}

    fun asLiveData() = result as LiveData<Resource<ResponseUIType>>

    fun forceNetworkFetch() {
        val dbSource = loadFromDb()
        fetchFromNetwork(dbSource)
    }

    /**
     * Processing data before we save to the database, if necessary.
     */
    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<ResponseNetworkType>) =
        response.body

    /**
     * Saving <ResponseNetworkType> to the local database.
     * */
    @WorkerThread
    protected abstract fun saveCallResult(item: ResponseNetworkType)

    /**
     * Used to determine the if we should fetch from the network or not.
     */
    @MainThread
    protected abstract fun shouldFetch(data: ResponseNetworkType?): Boolean

    /**
     * Loading the NetworkType data form the database.
     * We would usually create a DataResponse object after fetching.
     */
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResponseNetworkType>

    /**
     * Fetching data from network.
     */
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResponseNetworkType>>
}
