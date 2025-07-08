package net.broachcutter.vendorapp.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import net.broachcutter.vendorapp.db.AppDatabase
import net.broachcutter.vendorapp.db.DatabaseMigration

@Module
class CacheModule {

    @Provides
    fun provideAppDatabase(application: Application) = Room
        .databaseBuilder(application, AppDatabase::class.java, "broachcutter-db")
        .fallbackToDestructiveMigration()
        .addMigrations(
            DatabaseMigration.MIGRATION_2_3,
            DatabaseMigration.MIGRATION_3_4,
            DatabaseMigration.MIGRATION_4_5,
            DatabaseMigration.MIGRATION_6_7,
            DatabaseMigration.MIGRATION_8_9
        )
        .build()

    @Provides
    fun provideCartDao(appDatabase: AppDatabase) = appDatabase.cartDao()

    @Provides
    fun provideProductDao(appDatabase: AppDatabase) = appDatabase.productDao()

    @Provides
    fun provideUpdatedOrderDao(appDatabase: AppDatabase) = appDatabase.updatedOrder()
}
