package com.nyvoratech.composebase.ui.postusers.ui.postuserlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyvoratech.composebase.core.common.FirebaseAnalyticsLogger
import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.core.network.toUserMessage
import com.nyvoratech.composebase.ui.postusers.domain.usecases.GetPostUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostUsersViewModel @Inject constructor(
    private val getPostUsersUseCase: GetPostUsersUseCase,
    private val analyticsLogger: FirebaseAnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUsersUiState())
    val uiState: StateFlow<PostUsersUiState> = _uiState.asStateFlow()

    init {
        loadPostUsers()
    }

    fun loadPostUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getPostUsersUseCase()) {
                is Resource.Success -> {
                    analyticsLogger.logEvent(FirebaseAnalyticsLogger.EVENT_POST_USERS_LOADED)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            postUsers = result.data
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }

            }
        }
    }
}