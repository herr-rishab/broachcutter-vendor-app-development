package net.broachcutter.vendorapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.valartech.commons.aac.AbsentLiveData

class RetriggerableLiveData<Input, Result>(block: (Input) -> LiveData<Result>?) {

    private val _liveData = MutableLiveData<Input>()

    val liveData: LiveData<Result> = _liveData.switchMap {
        if (it != null) {
            block(it)
        } else {
            AbsentLiveData.create()
        }
    }

    fun execute(value: Input) {
        value?.let { _liveData.postValue(it) }
    }

    fun retrigger() {
        _liveData.retrigger()
    }
}

fun <T> MutableLiveData<T>.retrigger() {
    this.postValue(this.value)
}
