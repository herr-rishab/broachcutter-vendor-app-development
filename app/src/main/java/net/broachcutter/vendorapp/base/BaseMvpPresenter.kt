package net.broachcutter.vendorapp.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import net.broachcutter.vendorapp.DealerApplication
import ru.terrakok.cicerone.Router

/**
 * Created by amitavk on 26/06/17.
 */
interface BaseMvpPresenter : LifecycleObserver {
    // todo remove this hard dependency, causes tests to fail
    val router: Router
        get() = DealerApplication.INSTANCE.appComponent.getRouter()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
    }
}
