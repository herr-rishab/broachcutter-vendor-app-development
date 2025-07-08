package net.broachcutter.vendorapp.di

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.valartech.commons.network.retrofit.CamelCaseConverterFactory
import com.valartech.commons.network.retrofit.LiveDataCallAdapterFactory
import com.valartech.commons.network.retrofit.SlowNetworkInterceptor
import dagger.Module
import dagger.Provides
import io.sentry.android.okhttp.SentryOkHttpInterceptor
import net.broachcutter.vendorapp.BuildConfig
import net.broachcutter.vendorapp.api.BroachCutterApi
import net.broachcutter.vendorapp.network.*
import net.broachcutter.vendorapp.network.retrofit.BaseResponseConverterFactory
import net.broachcutter.vendorapp.screens.coupon.slide.ZonedDateTimeSerializerDeserializer
import net.broachcutter.vendorapp.util.DI
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.ZonedDateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@Module
class NetworkModule {
    //    private val AUTH_HEADER_KEY = "Authorization"
//    private val AUTH_HEADER_PREFIX = "Bearer "
    companion object {
        private const val BROACHCUTTER_ENDPOINT = BuildConfig.API_ENDPOINT
        private const val VALARTECH_ENDPOINT = BuildConfig.VALARTECH_API_ENDPOINT
        private const val TIMEOUT_SEC = 60L
    }

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    fun provideSlowNetworkInterceptor() =
        SlowNetworkInterceptor(1500)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        context: Application,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authHeaderInterceptor: BCAuthHeaderInterceptor,
        tokenRefreshAuthenticator: TokenRefreshAuthenticator,
        cache: Cache
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(SentryOkHttpInterceptor())
            .addInterceptor(authHeaderInterceptor)
            .authenticator(tokenRefreshAuthenticator)
            .readTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
            .cache(cache)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor)
        }
        return builder.build()
    }

    @Provides
    fun provideGsonConverterFactory(
        zonedDateTimeTypeAdapter: ZonedDateTimeTypeAdapter
    ): GsonConverterFactory {
        val gsonBuilder =
            GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .registerTypeAdapter(ZonedDateTime::class.java, zonedDateTimeTypeAdapter)
        return GsonConverterFactory.create(gsonBuilder.create())
    }

    @Provides
    fun provideMoshi(moshiDateTimeAdapter: MoshiDateTimeAdapter): Moshi =
        Moshi.Builder().add(moshiDateTimeAdapter).build()

    @Provides
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    fun provideRxJava2CallAdapterFactory(): RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.create()

    @Provides
    fun provideCoroutineCallAdapterFactory() = CoroutineCallAdapterFactory()

    @Provides
    fun provideLiveDataCallAdapterFactory() = LiveDataCallAdapterFactory()

    @Provides
    fun provideBaseResponseConverterFactory() = BaseResponseConverterFactory()

    @Provides
    fun provideCamelCaseConverterFactory() = CamelCaseConverterFactory()

    @Provides
    @Named("api_endpoint")
    fun provideApiEndpoint() = BROACHCUTTER_ENDPOINT

    @Provides
    @Named("valartech_api_endpoint")
    fun provideValartechApiEndpoint() = VALARTECH_ENDPOINT

    @Suppress("LongParameterList")
    @Provides
    @Singleton
    @Named(DI.INDUS_API)
    fun provideIndusBroachCutterApi(
        okHttpClient: OkHttpClient,
        gsonConverterFactoryFactory: GsonConverterFactory,
        @Named("api_endpoint") apiEndpoint: String,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory,
        liveDataCallAdapterFactory: LiveDataCallAdapterFactory
    ): BroachCutterApi = Retrofit.Builder()
        .baseUrl(apiEndpoint)
        .client(okHttpClient)
        .addCallAdapterFactory(RetryCallAdapterFactory.create())
        .addConverterFactory(gsonConverterFactoryFactory)
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addCallAdapterFactory(coroutineCallAdapterFactory)
        .addCallAdapterFactory(liveDataCallAdapterFactory)
        .build()
        .create(BroachCutterApi::class.java)

    @Suppress("LongParameterList")
    @Provides
    @Singleton
    @Named(DI.VALARTECH_API)
    fun provideValartechBroachCutterApi(
        okHttpClient: OkHttpClient,
        gsonConverterFactoryFactory: GsonConverterFactory,
        @Named("valartech_api_endpoint") apiEndpoint: String,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory,
        liveDataCallAdapterFactory: LiveDataCallAdapterFactory
    ): BroachCutterApi = Retrofit.Builder()
        .baseUrl(apiEndpoint)
        .client(okHttpClient)
        .addCallAdapterFactory(RetryCallAdapterFactory.create())
        .addConverterFactory(gsonConverterFactoryFactory)
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addCallAdapterFactory(coroutineCallAdapterFactory)
        .addCallAdapterFactory(liveDataCallAdapterFactory)
        .build()
        .create(BroachCutterApi::class.java)

    @Provides
    @Singleton
    fun provideGson(zonedDateTimeSerializerDeserializer: ZonedDateTimeSerializerDeserializer): Gson {
        val gsonBuilder = GsonBuilder()
            .registerTypeAdapter(
                ZonedDateTime::class.java,
                zonedDateTimeSerializerDeserializer
            )
        return gsonBuilder.create()
    }
}
