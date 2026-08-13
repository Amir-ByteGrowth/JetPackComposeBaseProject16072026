package com.nyvoratech.composebase.ui.users.di

import com.nyvoratech.composebase.core.di.MainRetrofit
import com.nyvoratech.composebase.ui.users.data.apiservice.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @Singleton
    fun provideApiService(@MainRetrofit retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

}