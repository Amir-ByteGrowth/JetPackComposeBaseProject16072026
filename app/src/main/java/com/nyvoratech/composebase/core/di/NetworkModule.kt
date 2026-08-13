package com.nyvoratech.composebase.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nyvoratech.composebase.BuildConfig
import com.nyvoratech.composebase.ui.users.data.apiservice.ApiService
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

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addNetworkInterceptor(loggingInterceptor) // keep logging last so headers/body are visible
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }



}
