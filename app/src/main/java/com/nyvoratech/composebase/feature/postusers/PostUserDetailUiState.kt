package com.nyvoratech.composebase.feature.postusers

import com.nyvoratech.composebase.domain.model.PostUser

data class PostUserDetailUiState(
    val postUser: PostUser? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)