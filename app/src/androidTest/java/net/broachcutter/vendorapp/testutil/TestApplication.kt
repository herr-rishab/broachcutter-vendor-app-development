package net.broachcutter.vendorapp.testutil

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * https://android.jlelse.eu/how-to-inject-mock-dependencies-into-android-components-using-dagger-androidinjector-e274c8f6a9a6
 */
class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
