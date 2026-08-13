package com.nyvoratech.composebase.ui.login.domain.repository

import com.nyvoratech.composebase.core.network.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Contract for authentication. The impl wraps Firebase Auth, but nothing
 * above this interface knows that.
 */
interface AuthRepository {
    /** Emits the current user id, or null when signed out. */
    fun observeAuthState(): Flow<String?>

    suspend fun signIn(email: String, password: String): Resource<Unit>

    suspend fun signUp(email: String, password: String): Resource<Unit>

    fun signOut(): Resource<Unit>
}
