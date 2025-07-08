package net.broachcutter.vendorapp.testutil

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * https://android.jlelse.eu/how-to-inject-mock-dependencies-into-android-components-using-dagger-androidinjector-e274c8f6a9a6
 */
class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}
