package com.nyvoratech.composebase.core.di

import com.nyvoratech.composebase.core.common.CrashReporter
import com.nyvoratech.composebase.core.common.FirebaseCrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseCrashReporterModule {

    @Binds
    @Singleton
    abstract fun bindCrashReporter(
        implementation: FirebaseCrashReporter
    ): CrashReporter
}