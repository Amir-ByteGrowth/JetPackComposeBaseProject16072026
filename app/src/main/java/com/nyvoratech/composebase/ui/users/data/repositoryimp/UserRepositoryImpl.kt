package com.nyvoratech.composebase.ui.users.data.repositoryimp

import com.nyvoratech.composebase.ui.users.data.local.UserDao
import com.nyvoratech.composebase.core.network.ApiCallHandler
import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.ui.users.data.mapper.toDomain
import com.nyvoratech.composebase.ui.users.data.mapper.toEntity
import com.nyvoratech.composebase.ui.users.data.apiservice.ApiService
import com.nyvoratech.composebase.ui.users.domain.model.User
import com.nyvoratech.composebase.ui.users.domain.model.toEntity
import com.nyvoratech.composebase.ui.users.domain.repository.UserRepository
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
    private val userDao: UserDao,
    private val apiCallHandler: ApiCallHandler
) : UserRepository {

    override fun observeUsers(): Flow<List<User>> =
        userDao.observeUsers().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshUsers(): Resource<Unit> {

        return when (
            val result = apiCallHandler.execute {
                apiService.getUsers()
            }
        ) {
            is Resource.Success -> {
                userDao.upsertAll(result.data.map { it.toEntity() })

                Resource.Success(Unit)
            }

            is Resource.Error -> {
                result
            }
        }
    }

    override suspend fun getUserById(id: String): Resource<User> {
        // 1. Try local database first
        val cachedUser = userDao.getUserById(id)
        if (cachedUser != null) {
            return Resource.Success(cachedUser.toDomain())
        }
        return when (
            val result = apiCallHandler.execute {
                apiService.getUserById(id)
            }
        ) {
            is Resource.Success -> {

                val user = result.data

                // 3. Cache remote user
                userDao.upsertAll(listOf(user.toEntity()))

                Resource.Success(user)
            }

            is Resource.Error -> {
                result
            }
        }
    }

}