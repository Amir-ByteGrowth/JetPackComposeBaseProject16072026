package com.nyvoratech.composebase.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyvoratech.composebase.ui.login.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Holds state that needs to survive and be shared across multiple screens
 * in the same navigation flow (e.g. current signed-in user id). Obtain this
 * with a navigation-graph-scoped `hiltViewModel()` call from each screen
 * (see [com.nyvoratech.composebase.core.navigation.ComposeBaseNavGraph]) so
 * every screen inside that graph shares the *same* instance, instead of
 * each screen getting its own.
 */
@HiltViewModel
class SharedSessionViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    val currentUserId: StateFlow<String?> = authRepository.observeAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val isLoggedIn: Boolean
        get() = currentUserId.value != null
}
