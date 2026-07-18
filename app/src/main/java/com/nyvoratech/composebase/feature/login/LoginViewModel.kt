package com.nyvoratech.composebase.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyvoratech.composebase.core.common.AnalyticsLogger
import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Channel -> one-shot events. Using a Channel (not SharedFlow) avoids
    // replaying the last navigation event to new collectors on rotation.
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onLoginClicked() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            when (val result = loginUseCase(state.email, state.password)) {
                is Resource.Success -> {
                    analyticsLogger.logEvent(AnalyticsLogger.EVENT_LOGIN_SUCCESS)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.send(LoginEvent.NavigateToUsers)
                }
                is Resource.Error -> {
                    analyticsLogger.logEvent(AnalyticsLogger.EVENT_LOGIN_FAILURE)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                Resource.Loading -> Unit
            }
        }
    }
}
