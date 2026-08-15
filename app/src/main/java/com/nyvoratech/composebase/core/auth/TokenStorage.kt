package com.nyvoratech.composebase.core.auth


import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun updateAccessToken(newAccessToken: String)
    suspend fun clearTokens()
    suspend fun hasSession(): Boolean
    fun observeAccessToken(): Flow<String?>


}