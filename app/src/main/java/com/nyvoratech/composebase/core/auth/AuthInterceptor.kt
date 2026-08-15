package com.nyvoratech.composebase.core.auth

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

// AuthInterceptor.kt — unchanged, already minimal
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.url.encodedPath.contains("auth/")) return chain.proceed(original)

        val token = tokenManager.getAccessToken()
        val request = token?.let {
            original.newBuilder().header("Authorization", "Bearer $it").build()
        } ?: original
        return chain.proceed(request)
    }
}