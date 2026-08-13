package com.nyvoratech.composebase.ui.users.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyvoratech.composebase.core.common.AnalyticsLogger
import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.core.network.toUserMessage
import com.nyvoratech.composebase.ui.users.domain.usecases.GetUsersUseCase
import com.nyvoratech.composebase.ui.users.domain.usecases.RefreshUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val refreshState = MutableStateFlow(RefreshState())

    val uiState: StateFlow<UsersUiState> = combine(
        getUsersUseCase(),
        refreshState
    ) { users, refresh ->
        UsersUiState(
            users = users,
            isRefreshing = refresh.isRefreshing,
            errorMessage = refresh.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UsersUiState()
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshState.value = refreshState.value.copy(isRefreshing = true, errorMessage = null)
            when (val result = refreshUsersUseCase()) {
                is Resource.Success -> {
                    analyticsLogger.logEvent(AnalyticsLogger.EVENT_USERS_LOADED)
                    refreshState.value = refreshState.value.copy(isRefreshing = false)
                }

                is Resource.Error -> {
                    refreshState.value = refreshState.value.copy(
                        isRefreshing = false,
                        errorMessage = result.error.toUserMessage()
                    )
                }
            }
        }
    }

    private data class RefreshState(
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null
    )
}
