package net.broachcutter.vendorapp.di

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.db.ProductDao
import net.broachcutter.vendorapp.db.UpdatedOrderDao
import net.broachcutter.vendorapp.network.BCAuthHeaderInterceptor
import net.broachcutter.vendorapp.network.google.AppExecutors
import net.broachcutter.vendorapp.screens.cart.repo.BroachCutterCartRepository
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.coupon.repo.CouponRepository
import net.broachcutter.vendorapp.screens.home.repo.BroachcutterUserRepository
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.product_list.BroachCutterProductRepository
import net.broachcutter.vendorapp.screens.product_list.ProductRepository
import net.broachcutter.vendorapp.util.DI
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        @Named(DI.INDUS_API) broachCutterApi: BroachCutterApi,
        appExecutors: AppExecutors,
        updatedOrderDao: UpdatedOrderDao,
        firebaseHolder: FirebaseHolder,
        application: Application,
    ): UserRepository = BroachcutterUserRepository(
        broachCutterApi,
        appExecutors,
        updatedOrderDao,
        firebaseHolder,
        application
    )

    @Provides
    @Singleton
    fun provideProductRepository(
        @Named(DI.INDUS_API) broachCutterApi: BroachCutterApi,
        appExecutors: AppExecutors,
        productDao: ProductDao
    ): ProductRepository = BroachCutterProductRepository(
        broachCutterApi,
        appExecutors,
        productDao
    )

    @Provides
    @Singleton
    fun provideCartRepository(
        @Named(DI.INDUS_API) indusApi: BroachCutterApi,
        @Named(DI.VALARTECH_API) valartechApi: BroachCutterApi,
    ): CartRepository =
        BroachCutterCartRepository(
            indusApi,
            valartechApi
        )

    @Provides
    @Singleton
    fun provideLoginRepository(
        appExecutors: AppExecutors,
        application: Application,
        authHeaderInterceptor: BCAuthHeaderInterceptor,
        sharedPreferences: SharedPreferences,
        @Named(DI.INDUS_API) broachCutterApi: BroachCutterApi,
        firebaseHolder: FirebaseHolder,
        analytics: Analytics
    ): LoginRepository = LoginRepository(
        appExecutors,
        application,
        authHeaderInterceptor,
        sharedPreferences,
        broachCutterApi,
        firebaseHolder,
        analytics
    )

    @Provides
    @Singleton
    fun provideCouponRepository(
        @Named(DI.VALARTECH_API) broachCutterApi: BroachCutterApi,
        gson: Gson,
        sharedPreferences: SharedPreferences
    ): CouponRepository = CouponRepository(broachCutterApi, gson, sharedPreferences)
}
