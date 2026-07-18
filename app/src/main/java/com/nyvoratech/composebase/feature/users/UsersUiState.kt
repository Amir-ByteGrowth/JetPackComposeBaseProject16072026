package com.nyvoratech.composebase.feature.users

import com.nyvoratech.composebase.domain.model.User

data class UsersUiState(
    val users: List<User> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
