package com.nyvoratech.composebase.core.di.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val tokenStorage: TokenStorage
) {

    @Volatile
    private var accessToken: String? = null

    @Volatile
    private var refreshToken: String? = null

    suspend fun initialize() {
        accessToken = tokenStorage.getAccessToken()
        refreshToken = tokenStorage.getRefreshToken()
    }

    fun getAccessToken(): String? = accessToken

    fun getRefreshToken(): String? = refreshToken

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {

        this.accessToken = accessToken
        this.refreshToken = refreshToken

        tokenStorage.saveTokens(
            accessToken,
            refreshToken
        )
    }

    suspend fun updateAccessToken(
        accessToken: String
    ) {

        this.accessToken = accessToken

        tokenStorage.updateAccessToken(
            accessToken
        )
    }

    suspend fun clearTokens() {

        accessToken = null
        refreshToken = null

        tokenStorage.clearTokens()
    }

    suspend fun hasSession(): Boolean {

        if (refreshToken != null)
            return true

        return tokenStorage.hasSession()
    }
}