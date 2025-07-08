package net.broachcutter.vendorapp

import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations

abstract class BaseMockitoTestCase {

    lateinit var closeable: AutoCloseable

    @Before
    fun openMocks() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun closeMocks() {
        closeable.close()
    }
}
