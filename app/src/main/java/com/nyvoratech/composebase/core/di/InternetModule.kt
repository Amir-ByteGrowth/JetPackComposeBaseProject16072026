package com.nyvoratech.composebase.core.di

import com.nyvoratech.composebase.core.network.NetworkMonitor
import com.nyvoratech.composebase.core.network.NetworkMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InternetModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        implementation: NetworkMonitorImpl
    ): NetworkMonitor
}