package net.broachcutter.vendorapp.db

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.UpdatedOrder
import net.broachcutter.vendorapp.models.cart.CartItem
import net.broachcutter.vendorapp.util.Constants.CURRENT_DATABASE_VERSION

@Database(
    entities = [CartItem::class, Product::class, UpdatedOrder::class],
    version = CURRENT_DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(from = 7, to = 8, spec = AppDatabase.Db7to8AutoMigrationSpec::class)
    ]
)
@TypeConverters(
    CartTypeConverters::class, ProductTypeConverters::class,
    OrderTypeConverters::class, TrackingDetailTypeConverter::class,
    DocDueDateTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    abstract fun productDao(): ProductDao

    abstract fun updatedOrder(): UpdatedOrderDao

    @DeleteTable(tableName = "Order")
    class Db7to8AutoMigrationSpec : AutoMigrationSpec
}
