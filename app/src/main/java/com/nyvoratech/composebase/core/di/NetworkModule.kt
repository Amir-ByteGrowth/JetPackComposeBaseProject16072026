package com.nyvoratech.composebase.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nyvoratech.composebase.BuildConfig
import com.nyvoratech.composebase.core.auth.AuthApiService
import com.nyvoratech.composebase.core.auth.TokenAuthenticator
import com.nyvoratech.composebase.core.network.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton


private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

/**
 * Provides all networking singletons: JSON config, logging, OkHttp client
 * (with the auth interceptor wired in), and the Retrofit instance/services
 * built on top of it.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
//            redactHeader("Authorization") for sensitive headers enable this
//            redactHeader("Cookie") for sensitive session
        }

//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        loggingInterceptor: HttpLoggingInterceptor,
//        authInterceptor: AuthInterceptor
//    ): OkHttpClient = OkHttpClient.Builder()
//        .addInterceptor(authInterceptor)
//        .addNetworkInterceptor(loggingInterceptor) // keep logging last so headers/body are visible
//        .build()

    private fun createBaseClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient.Builder {

        return OkHttpClient.Builder()
//            .retryOnConnectionFailure(true)
            .addNetworkInterceptor(loggingInterceptor)
    }

    @Provides
    @Singleton
    @AuthOkHttp
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        return createBaseClient(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @MainOkHttp
    fun provideMainOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        return createBaseClient(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }


    //    @Provides
//    @Singleton
//    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
//        val contentType = "application/json".toMediaType()
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(json.asConverterFactory(contentType))
//            .build()
//    }
    private fun createRetrofit(
        json: Json,
        okHttpClient: OkHttpClient
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // -------------------------------------------------------------------------
    // Auth Retrofit
    // -------------------------------------------------------------------------

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        json: Json,
        @AuthOkHttp okHttpClient: OkHttpClient
    ): Retrofit =
        createRetrofit(json, okHttpClient)

    // -------------------------------------------------------------------------
    // Main Retrofit
    // -------------------------------------------------------------------------

    @Provides
    @Singleton
    @MainRetrofit
    fun provideMainRetrofit(
        json: Json,
        @MainOkHttp okHttpClient: OkHttpClient
    ): Retrofit =
        createRetrofit(json, okHttpClient)

    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------

    @Provides
    @Singleton
    fun provideAuthApiService(
        @AuthRetrofit retrofit: Retrofit
    ): AuthApiService =
        retrofit.create(AuthApiService::class.java)


}
