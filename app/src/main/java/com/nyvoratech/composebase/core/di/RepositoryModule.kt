package com.nyvoratech.composebase.core.di

import com.nyvoratech.composebase.core.network.FirebaseTokenProvider
import com.nyvoratech.composebase.core.network.TokenProvider
import com.nyvoratech.composebase.data.repository.AuthRepositoryImpl
import com.nyvoratech.composebase.data.repository.PostUserRepositoryImpl
import com.nyvoratech.composebase.data.repository.UserRepositoryImpl
import com.nyvoratech.composebase.domain.repository.AuthRepository
import com.nyvoratech.composebase.domain.repository.PostUserRepository
import com.nyvoratech.composebase.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds domain-layer interfaces to their data-layer implementations. This
 * is the only place that needs to change if you swap implementations
 * (e.g. move UserRepository from REST to Firestore-backed).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenProvider(impl: FirebaseTokenProvider): TokenProvider

    @Binds
    @Singleton
    abstract fun bindPostUserRepository(impl: PostUserRepositoryImpl): PostUserRepository
}
