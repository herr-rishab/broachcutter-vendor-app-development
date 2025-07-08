package net.broachcutter.vendorapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.broachcutter.vendorapp.app
import net.broachcutter.vendorapp.di.AppComponent
import ru.terrakok.cicerone.Navigator

abstract class BaseActivity : AppCompatActivity() {

    abstract val navigator: Navigator

    val router by lazy { getApplicationComponent().getRouter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApplicationComponent().inject(this)
    }

    override fun onResume() {
        super.onResume()
        getApplicationComponent().getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        getApplicationComponent().getNavigatorHolder().removeNavigator()
    }

    fun getApplicationComponent(): AppComponent = app.appComponent
}
