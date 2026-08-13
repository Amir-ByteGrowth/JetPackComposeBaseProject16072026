package com.nyvoratech.composebase.ui.login.ui

/** Single immutable state class for the Login screen. */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/** One-off events that shouldn't live in persistent state (navigation, toasts). */
sealed class LoginEvent {
    data object NavigateToUsers : LoginEvent()
    data class ShowSnackbar(val message: String) : LoginEvent()
}
