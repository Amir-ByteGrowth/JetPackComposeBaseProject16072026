package com.nyvoratech.composebase.core.auth.session

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    private val _authState =
        MutableStateFlow<AuthState>(AuthState.Unknown)

    val authState: StateFlow<AuthState> =
        _authState.asStateFlow()

    private val authStateListener =
        FirebaseAuth.AuthStateListener { auth ->
            _authState.value =
                if (auth.currentUser != null) {
                    AuthState.Authenticated
                } else {
                    AuthState.Unauthenticated
                }
        }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }
}