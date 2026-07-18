package com.nyvoratech.composebase.feature.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyvoratech.composebase.domain.model.User
import com.nyvoratech.composebase.feature.session.SharedSessionViewModel

/**
 * [sharedSessionViewModel] is passed in already scoped to the nav graph
 * (see ComposeBaseNavGraph), so it's the *same instance* the Login screen
 * saw — demonstrating cross-screen shared state without a singleton.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    sharedSessionViewModel: SharedSessionViewModel,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId by sharedSessionViewModel.currentUserId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Users") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            currentUserId?.let {
                Text(
                    text = "Signed in as: $it",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isRefreshing && uiState.users.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.errorMessage != null && uiState.users.isEmpty() -> {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        UsersList(users = uiState.users)
                    }
                }
            }
        }
    }
}

@Composable
private fun UsersList(users: List<User>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(users, key = { it.id }) { user ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = user.email, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
