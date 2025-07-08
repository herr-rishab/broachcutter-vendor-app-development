package net.broachcutter.vendorapp.db

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import net.broachcutter.vendorapp.di.AppModule
import net.broachcutter.vendorapp.di.DaggerAppComponent
import net.broachcutter.vendorapp.testutil.TestApplication
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.ZonedDateTime

/**
 * todo switch to using DaggerApplication to get the below method working
 *
 * https://android.jlelse.eu/how-to-inject-mock-dependencies-into-android-components-using-dagger-androidinjector-e274c8f6a9a6
 */
@RunWith(AndroidJUnit4::class)
class OrderTypeConvertersTest {

    private val orderTypeConverters = OrderTypeConverters()
    lateinit var now: ZonedDateTime

    @Before
    fun setup() {
        now = ZonedDateTime.now().withZoneSameInstant(OrderTypeConverters.indianZoneId)

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestApplication

        DaggerAppComponent
            .builder()
            .appModule(AppModule(app))
            .build()
            .inject(orderTypeConverters)
    }

    @Test
    fun fromZonedDateTime() {
        val epoch = orderTypeConverters.fromZonedDateTime(now)
        epoch shouldEqual now.toEpochSecond()
    }

    @Test
    fun toZonedDateTime() {
        val zonedDateTime = orderTypeConverters.toZonedDateTime(now.toEpochSecond())
        zonedDateTime shouldEqual now
    }
}
