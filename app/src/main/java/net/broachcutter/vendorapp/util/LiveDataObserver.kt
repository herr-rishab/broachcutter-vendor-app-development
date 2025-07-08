package net.broachcutter.vendorapp.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.valartech.commons.aac.observeFreshly
import com.valartech.commons.network.google.Resource
import com.valartech.commons.network.google.Status
import com.valartech.loadinglayout.LoadingLayout

class LiveDataObserver<T>(private val liveData: LiveData<Resource<T>>) {

    private var loadingLayout: LoadingLayout? = null
    private var errorState: Int? = null

    private lateinit var lifecycleOwner: LifecycleOwner

    private var onSuccess: ((Resource<T>) -> Unit)? = null
    private var onError: ((Resource<T>) -> Unit)? = null
    private var onLoading: ((Resource<T>) -> Unit)? = null
    private var onNull: (() -> Unit)? = null

    private var showAsOverlay = true

    /**
     * Starts observing the LiveData. This should be called after setting LifecycleOwner
     *
     * @see setLifecycleOwner
     * @throws IllegalStateException If the lifecycle owner is not set before calling this method
     */
    fun observe(observeFreshly: Boolean = false) {
        if (!::lifecycleOwner.isInitialized) {
            throw IllegalStateException("Missing lifecycleOwner. Call setLifeCycleOwner method")
        }
        attachObserver(observeFreshly)
    }

    /**
     * Sets whether to show loading as overlay or not. Default is set to true
     */
    fun showAsOverlay(showAsOverlay: Boolean) {
        this.showAsOverlay = showAsOverlay
    }

    /**
     * Sets the LifecycleOwner for observing the LiveData
     */
    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }

    /**
     * Sets the LoadingLayout whose state is to be changed
     */
    fun setLoadingLayout(loadingLayout: LoadingLayout) {
        this.loadingLayout = loadingLayout
    }

    /**
     * Sets the error state of the loading layout. Default is Error
     *
     * @see LoadingLayout.ViewState
     */
    fun setErrorState(@LoadingLayout.ViewState state: Int) {
        this.errorState = state
    }

    /**
     * Takes the function as a parameter which will be executed when
     * LiveData enters loading state
     */
    fun onLoading(block: ((Resource<T>)) -> Unit) {
        this.onLoading = block
    }

    /**
     * Takes the function as a parameter which will be executed when
     * LiveData enters success state
     */
    fun onSuccess(block: ((Resource<T>)) -> Unit) {
        this.onSuccess = block
    }

    /**
     * Takes the function as a parameter which will be executed when
     * LiveData enters error state
     */
    fun onError(block: ((Resource<T>)) -> Unit) {
        this.onError = block
    }

    /**
     * Takes the function as a parameter which will be executed when
     * null value is received
     */
    fun onNull(block: () -> Unit) {
        this.onNull = block
    }

    private fun attachObserver(observeFreshly: Boolean) {
        val observer = Observer<Resource<T>> {
            when (it?.status) {
                Status.SUCCESS -> {
                    loadingLayout?.setState(LoadingLayout.COMPLETE)
                    onSuccess?.invoke(it)
                }
                Status.ERROR -> {
                    loadingLayout?.setState(errorState ?: LoadingLayout.ERROR)
                    onError?.invoke(it)
                }
                Status.LOADING -> {
                    loadingLayout?.setState(
                        if (showAsOverlay) LoadingLayout.LOADING_OVERLAY
                        else LoadingLayout.LOADING
                    )
                    onLoading?.invoke(it)
                }
                null -> {
                    onNull?.invoke()
                }
            }
        }

        if (observeFreshly) {
            liveData.observeFreshly(lifecycleOwner, observer)
        } else {
            liveData.observe(lifecycleOwner, observer)
        }
    }
}
