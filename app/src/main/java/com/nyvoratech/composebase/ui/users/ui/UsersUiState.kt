package com.nyvoratech.composebase.ui.users.ui

import com.nyvoratech.composebase.ui.users.domain.model.User

data class UsersUiState(
    val users: List<User> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
