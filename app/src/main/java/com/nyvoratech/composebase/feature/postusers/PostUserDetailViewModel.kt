package com.nyvoratech.composebase.feature.postusers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.usecase.GetPostUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostUserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPostUserByIdUseCase: GetPostUserByIdUseCase
) : ViewModel() {

    private val userId: Long = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(PostUserDetailUiState())
    val uiState: StateFlow<PostUserDetailUiState> = _uiState.asStateFlow()

    init {
        loadPostUser()
    }

    private fun loadPostUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getPostUserByIdUseCase(userId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        postUser = result.data
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