package com.nyvoratech.composebase.core.di


import com.nyvoratech.composebase.core.di.auth.DataStoreTokenStore
import com.nyvoratech.composebase.core.di.auth.TokenStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindTokenStorage(
        impl: DataStoreTokenStore
    ): TokenStorage
}