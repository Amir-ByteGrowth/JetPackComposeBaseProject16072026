package com.nyvoratech.composebase.core.di.auth

sealed interface AuthState {
    data object Unknown : AuthState
    data object Authenticated : AuthState
    data object Unauthenticated : AuthState
}