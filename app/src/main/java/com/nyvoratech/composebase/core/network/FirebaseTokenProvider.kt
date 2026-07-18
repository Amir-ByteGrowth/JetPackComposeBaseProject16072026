package com.nyvoratech.composebase.core.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Default [TokenProvider] backed by the current Firebase user's ID token.
 * Swap this binding in [com.nyvoratech.composebase.core.di.RepositoryModule]
 * if you switch to a different auth backend.
 */
class FirebaseTokenProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : TokenProvider {
    override suspend fun getToken(): String? {
        val user = firebaseAuth.currentUser ?: return null
        return try {
            user.getIdToken(false).await().token
        } catch (e: Exception) {
            null
        }
    }
}
