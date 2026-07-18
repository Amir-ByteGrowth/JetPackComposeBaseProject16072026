package com.nyvoratech.composebase.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Attaches an auth token to every outgoing request. [tokenProvider] is a
 * suspend lambda so it can be backed by DataStore, EncryptedSharedPrefs,
 * or FirebaseAuth's getIdToken() without this class knowing the source.
 */
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenProvider.getToken() }
        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}

/** Abstraction so AuthInterceptor doesn't depend on FirebaseAuth or any specific storage. */
interface TokenProvider {
    suspend fun getToken(): String?
}
