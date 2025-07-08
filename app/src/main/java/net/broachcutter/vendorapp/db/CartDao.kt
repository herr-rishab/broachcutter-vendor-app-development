package net.broachcutter.vendorapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.models.cart.PaymentTerm

@Dao
interface CartDao {

    @Query("SELECT * FROM cartitem WHERE part_number_primary = :partNumber")
    suspend fun getCartItem(partNumber: String): CartItem?

    @Query("SELECT quantity FROM cartitem WHERE part_number_primary = :partNumber")
    suspend fun getCartItemQuantity(partNumber: String): Int?

    @Query("SELECT * FROM cartitem")
    fun getCartItemsLiveData(): LiveData<List<CartItem>>

    @Query("SELECT * FROM cartitem")
    suspend fun getCartItems(): List<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cartItem: CartItem)

    @Query("UPDATE cartitem SET quantity = :newQuantity WHERE :partNumber = partNumber")
    suspend fun updateQuantity(partNumber: String, newQuantity: Int)

    @Query("UPDATE cartitem SET selectedPaymentTerm = :paymentTerm WHERE :partNumber = partNumber")
    suspend fun setPaymentTerm(partNumber: String, paymentTerm: PaymentTerm)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteFromCart(cartItem: CartItem)

    @Query("DELETE FROM cartitem")
    suspend fun clearCart()
}
