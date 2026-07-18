package com.nyvoratech.composebase.fakes

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.model.User
import com.nyvoratech.composebase.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeUserRepository : UserRepository {

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())
    var refreshResult: Resource<Unit> = Resource.Success(Unit)
    var seedUsersOnRefresh: List<User> = emptyList()

    override fun observeUsers(): StateFlow<List<User>> = usersFlow

    override suspend fun refreshUsers(): Resource<Unit> {
        if (refreshResult is Resource.Success) {
            usersFlow.value = seedUsersOnRefresh
        }
        return refreshResult
    }

    override suspend fun getUserById(id: String): Resource<User> {
        val match = usersFlow.value.firstOrNull { it.id == id }
        return match?.let { Resource.Success(it) } ?: Resource.Error("User not found")
    }
}
