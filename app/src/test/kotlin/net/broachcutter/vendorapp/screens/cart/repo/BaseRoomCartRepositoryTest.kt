package net.broachcutter.vendorapp.screens.cart.repo

import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.broachcutter.vendorapp.BaseMockitoTestCase
import net.broachcutter.vendorapp.db.CartDao
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.models.cart.PaymentTerm
import net.broachcutter.vendorapp.models.cart.Pricing
import org.junit.Test
import org.mockito.Mock

class BaseRoomCartRepositoryTest : BaseMockitoTestCase() {

    @Mock
    private lateinit var cartDao: CartDao

    @ExperimentalCoroutinesApi
    @Test
    fun getCart() {
        runBlockingTest {
            // given
            val cub = Product(
                partNumber = "900001",
                name = "CUB",
                productType = ProductType.MACHINE
            )
            val cubAuto = Product(
                partNumber = "900002",
                name = "CUB AUTO",
                productType = ProductType.MACHINE
            )
            val cartItem1 = CartItem(
                product = cub,
                partNumber = "900001",
                quantity = 1,
                unitPrice = Pricing(48000.0, 70.0f, 14400.0),
                paymentTerms = listOf(
                    PaymentTerm("P0070", "70% off", 70.0, 19),
                    PaymentTerm("P2165", "65% off", 65.0, 20)
                ),
                selectedPaymentTerm = PaymentTerm("P0070", "70% off", 70.0, 21),
            )
            whenever(cartDao.getCartItems()).thenReturn(listOf(cartItem1))
            // when
            // todo
            // then
        }
    }
}
