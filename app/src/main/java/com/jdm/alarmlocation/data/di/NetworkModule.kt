package com.jdm.alarmlocation.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jdm.alarmlocation.BuildConfig
import com.jdm.alarmlocation.data.ReverseGeoApi
import com.jdm.alarmlocation.data.SearchApi
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SEARCH

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NCP

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val defaultConnectTimeout = 10
    private const val defaultReadTimeout = 30
    private const val defaultWriteTimeout = 15

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.HEADERS
        }
    }
    private val networkJson: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    private val searchHeader: Headers
        get() {
            val keyValue = mutableListOf<String>(
                "X-Naver-Client-Id", BuildConfig.SEARCH_CLIENT_ID,
                "X-Naver-Client-Secret", BuildConfig.SEARCH_SECRET_ID,
            )

            return Headers.headersOf(*keyValue.toTypedArray())
        }

    private val ncpHeader: Headers
        get() {
            val keyValue = mutableListOf<String>(
                "x-ncp-apigw-api-key-id", BuildConfig.NCP_API_ID,
                "x-ncp-apigw-api-key", BuildConfig.NCP_API_SECRET_ID,
            )

            return Headers.headersOf(*keyValue.toTypedArray())
        }


    @SEARCH
    @Provides
    @Singleton
    fun provideSearchInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                .headers(searchHeader)
                .build()
            chain.proceed(request)
        }
    }

    @NCP
    @Provides
    @Singleton
    fun provideNcpInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                .headers(ncpHeader)
                .build()
            chain.proceed(request)
        }
    }

    @SEARCH
    @Provides
    @Singleton
    fun provideSearchOkHttpClient(
        @SEARCH headerInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(headerInterceptor)
            addInterceptor(httpLoggingInterceptor)
        }.build()
    }

    @NCP
    @Provides
    @Singleton
    fun provideNcpOkHttpClient(
        @NCP headerInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(headerInterceptor)
            addInterceptor(httpLoggingInterceptor)
        }.build()
    }

    @SEARCH
    @Provides
    @Singleton
    fun provideSearchRetrofit(@SEARCH okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
    }

    @NCP
    @Provides
    @Singleton
    fun provideNcpRetrofit(@NCP okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://maps.apigw.ntruss.com/map-reversegeocode/v2/")
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
    }

    @SEARCH
    @Provides
    @Singleton
    fun provideSearchApi(@SEARCH retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }

    @NCP
    @Provides
    @Singleton
    fun provideNcpApi(@NCP retrofit: Retrofit): ReverseGeoApi {
        return retrofit.create(ReverseGeoApi::class.java)
    }
}