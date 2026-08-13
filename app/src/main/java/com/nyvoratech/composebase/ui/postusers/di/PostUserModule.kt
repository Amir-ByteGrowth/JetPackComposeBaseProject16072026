package com.nyvoratech.composebase.ui.postusers.di

import com.nyvoratech.composebase.ui.postusers.data.apiservice.PostUserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostUserModule {
    @Provides
    @Singleton
    fun providePostUserApiService(
        retrofit: Retrofit
    ): PostUserApiService = retrofit.create(PostUserApiService::class.java)
}