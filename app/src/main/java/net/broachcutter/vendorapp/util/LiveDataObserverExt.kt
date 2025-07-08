package net.broachcutter.vendorapp.util

import androidx.lifecycle.LiveData
import com.valartech.commons.network.google.Resource

inline fun <T> LiveData<Resource<T>>.attachObserver(block: LiveDataObserver<T>.() -> Unit) {
    block(LiveDataObserver(this))
}
