package com.nyvoratech.composebase.domain.repository

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.model.User
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
