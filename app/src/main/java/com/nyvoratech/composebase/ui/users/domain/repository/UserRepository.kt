package com.nyvoratech.composebase.ui.users.domain.repository

import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.ui.users.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Domain-facing contract. The implementation (in the data layer) decides
 * how to combine the Room cache with the Retrofit remote source following
 * the single-source-of-truth pattern.
 */
interface UserRepository {
    /** Always emits from the local DB; triggers a background refresh from network. */
    fun observeUsers(): Flow<List<User>>

    suspend fun refreshUsers(): Resource<Unit>

    suspend fun getUserById(id: String): Resource<User>
}