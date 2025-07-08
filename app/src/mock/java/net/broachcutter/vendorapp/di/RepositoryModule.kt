package net.broachcutter.vendorapp.di

import android.app.Application
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import net.broachcutter.vendorapp.analytics.Analytics
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.network.BCAuthHeaderInterceptor
import net.broachcutter.vendorapp.network.google.AppExecutors
import net.broachcutter.vendorapp.repo.MockProductRepository
import net.broachcutter.vendorapp.repo.MockUserRepository
import net.broachcutter.vendorapp.screens.cart.repo.CartRepository
import net.broachcutter.vendorapp.screens.cart.repo.MockRoomCartRepository
import net.broachcutter.vendorapp.screens.home.repo.UserRepository
import net.broachcutter.vendorapp.screens.login.LoginRepository
import net.broachcutter.vendorapp.screens.product_list.ProductRepository
import net.broachcutter.vendorapp.util.DI
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = MockUserRepository

    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository = MockProductRepository

    @Provides
    @Singleton
    fun provideCartRepository(): CartRepository = MockRoomCartRepository

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
}
