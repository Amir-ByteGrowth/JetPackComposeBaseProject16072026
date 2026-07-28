package com.nyvoratech.composebase.feature.postusers

import com.nyvoratech.composebase.domain.model.PostUser

data class PostUsersUiState(
    val postUsers: List<PostUser> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)