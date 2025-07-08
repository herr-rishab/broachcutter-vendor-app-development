package net.broachcutter.vendorapp
//
// import android.os.Build
// import androidx.test.core.app.ApplicationProvider.getApplicationContext
// import androidx.test.platform.app.InstrumentationRegistry
// import com.google.firebase.FirebaseApp
// import net.broachcutter.vendorapp.screens.login.LoginRepository
// import net.broachcutter.vendorapp.screens.login.first_login.FirstLoginContract
// import net.broachcutter.vendorapp.screens.login.first_login.FirstLoginPresenter
// import net.broachcutter.vendorapp.util.timber.TestingTree
// import org.junit.Before
// import org.junit.Test
// import org.junit.runner.RunWith
// import org.mockito.Mock
// import org.mockito.MockitoAnnotations
// import org.robolectric.RobolectricTestRunner
// import org.robolectric.annotation.Config
// import ru.terrakok.cicerone.Router
// import timber.log.Timber
//
// @Config(sdk = [Build.VERSION_CODES.O_MR1])
// @RunWith(RobolectricTestRunner::class)
// class FirstLoginPresenterTest {
//
//    private val emptyString = ""
//    private val junkString1 = "asdaASAFD1123"
//    private val junkString2 = "asdaASAFD111"
//    private lateinit var presenter: FirstLoginPresenter
//
//    @Mock
//    private lateinit var view: FirstLoginContract.View
//    @Mock
//    private lateinit var loginRepository: LoginRepository
//    @Mock
//    private lateinit var router: Router
//
//    @Before
//    fun setUp() {
//        VendorApplication.INSTANCE = getApplicationContext()
// //        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
//        MockitoAnnotations.initMocks(this)
//        Timber.plant(TestingTree())
//        presenter = FirstLoginPresenter(view, loginRepository)
//    }
//
// //    @Test
// //    fun `successful set password should route screen`() {
// ////        loginRepository.
// //        presenter.attemptSetPassword(junkString1, junkString1)
// //        //todo rewrite this test
// ////        verify(router).newRootScreen(Screens.Main())
// //    }
//
// }
