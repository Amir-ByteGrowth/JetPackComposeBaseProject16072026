package com.nyvoratech.composebase.ui.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyvoratech.composebase.core.common.FirebaseAnalyticsLogger
import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.core.network.toUserMessage
import com.nyvoratech.composebase.ui.login.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val analyticsLogger: FirebaseAnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Channel -> one-shot events. Using a Channel (not SharedFlow) avoids
    // replaying the last navigation event to new collectors on rotation.
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onLoginClicked() {

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            when (val result = loginUseCase(_uiState.value.email, _uiState.value.password)) {
                is Resource.Success -> {
                    analyticsLogger.logEvent(FirebaseAnalyticsLogger.EVENT_LOGIN_SUCCESS)
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.NavigateToUsers)
                }

                is Resource.Error -> {
                    analyticsLogger.logEvent(FirebaseAnalyticsLogger.EVENT_LOGIN_FAILURE)
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
