package com.nyvoratech.composebase.feature.postusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyvoratech.composebase.core.common.AnalyticsLogger
import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.usecase.GetPostUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostUsersViewModel @Inject constructor(
    private val getPostUsersUseCase: GetPostUsersUseCase,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUsersUiState())
    val uiState: StateFlow<PostUsersUiState> = _uiState.asStateFlow()

    init {
        loadPostUsers()
    }

    fun loadPostUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getPostUsersUseCase()) {
                is Resource.Success -> {
                    analyticsLogger.logEvent(AnalyticsLogger.EVENT_POST_USERS_LOADED)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        postUsers = result.data.postUserList
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                Resource.Loading -> Unit
            }
        }
    }
}