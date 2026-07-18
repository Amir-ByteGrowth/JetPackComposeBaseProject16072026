package com.nyvoratech.composebase.data.repository

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.core.common.safeCall
import com.nyvoratech.composebase.core.database.UserDao
import com.nyvoratech.composebase.core.network.ApiService
import com.nyvoratech.composebase.data.mapper.toDomain
import com.nyvoratech.composebase.data.mapper.toEntity
import com.nyvoratech.composebase.domain.model.User
import com.nyvoratech.composebase.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth: the UI always observes Room. [refreshUsers] pulls
 * fresh data from the network and writes it into Room, which pushes the
 * update back out through [observeUsers]'s Flow automatically.
 *
 * Swap in a FirestoreUserRepositoryImpl the same way if you prefer Firestore
 * as the remote source instead of your own REST API — the domain layer
 * (UserRepository interface) never needs to change.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) : UserRepository {

    override fun observeUsers(): Flow<List<User>> =
        userDao.observeUsers().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshUsers(): Resource<Unit> = safeCall {
        val remoteUsers = apiService.getUsers()
        userDao.upsertAll(remoteUsers.map { it.toEntity() })
    }

    override suspend fun getUserById(id: String): Resource<User> = safeCall {
        userDao.getUserById(id)?.toDomain()
            ?: apiService.getUserById(id).toDomain().also {
                // cache it for next time
                userDao.upsertAll(listOf(apiService.getUserById(id).toEntity()))
            }
    }
}
