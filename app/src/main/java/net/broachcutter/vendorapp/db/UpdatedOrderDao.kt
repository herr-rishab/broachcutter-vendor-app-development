package net.broachcutter.vendorapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.broachcutter.vendorapp.models.UpdatedOrder

@Dao
interface UpdatedOrderDao {

    @Query("SELECT * FROM `updatedorder` WHERE orderId = :orderNumber")
    fun getOrder(orderNumber: String): UpdatedOrder?

    @Query("SELECT * FROM `updatedorder` ORDER BY orderId DESC")
    fun getAllOrdersLiveData(): LiveData<List<UpdatedOrder>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrders(orders: List<UpdatedOrder>)

    @Update
    fun updateOrder(order: UpdatedOrder)
}
