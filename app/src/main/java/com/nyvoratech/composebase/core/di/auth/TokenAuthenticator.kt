package com.nyvoratech.composebase.core.di.auth

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

// TokenAuthenticator.kt — same core logic, one added guard replaces the second client entirely
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiService: AuthApiService
) : Authenticator {
    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.contains("auth/")) return null // never retry the refresh call itself
        if (responseCount(response) >= 3) return null

        return runBlocking {
            refreshMutex.withLock {
                val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
                val currentToken = tokenManager.getAccessToken()
                if (currentToken != null && currentToken != failedToken) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken").build()
                }

                val refreshToken = tokenManager.getRefreshToken()
                if (refreshToken == null) {
                    tokenManager.clearTokens()
                    return@withLock null
                }

                try {
                    val result = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
                    tokenManager.saveTokens(result.accessToken, result.refreshToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${result.accessToken}").build()
                } catch (e: Exception) {
                    tokenManager.clearTokens()
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++; prior = prior.priorResponse
        }
        return count
    }
}