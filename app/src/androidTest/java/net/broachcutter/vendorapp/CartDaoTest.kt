package net.broachcutter.vendorapp

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import net.broachcutter.vendorapp.db.AppDatabase
import net.broachcutter.vendorapp.db.CartDao
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.cart.CartItem
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CartDaoTest {
    private var cartDao: CartDao? = null
    private var db: AppDatabase? = null

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        cartDao = db!!.cartDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db!!.close()
    }

    private fun getDummyCartItem(): CartItem {
        val product = Product(
            "123", "testName", "blah", ProductType.MACHINE, null, 18f
        )
        return CartItem(
            product,
            product.partNumber,
            3,
            null,
            null,
            null
        )
    }

    @Test
    @Throws(Exception::class)
    fun addToCartTest() = runBlocking {
        val cartItem = getDummyCartItem()
        cartDao!!.addToCart(cartItem)

        val fetchedItem = cartDao!!.getCartItem("123")

        fetchedItem?.product?.name shouldEqual "testName"
        fetchedItem?.product?.description shouldEqual "blah"
        fetchedItem?.quantity shouldEqual 3
    }
}
