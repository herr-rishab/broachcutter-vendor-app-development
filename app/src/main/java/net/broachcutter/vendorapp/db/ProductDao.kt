package net.broachcutter.vendorapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(products: List<Product>)

    @Query("SELECT * FROM product WHERE productType = :productType")
    fun findByProductType(productType: ProductType): LiveData<List<Product>>

    @Query("SELECT * FROM product")
    fun getAllProducts(): LiveData<List<Product>>
}
