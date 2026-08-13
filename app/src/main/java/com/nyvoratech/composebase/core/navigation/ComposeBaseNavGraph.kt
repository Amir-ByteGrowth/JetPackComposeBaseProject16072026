package com.nyvoratech.composebase.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.nyvoratech.composebase.ui.login.ui.LoginScreen
import com.nyvoratech.composebase.ui.postusers.ui.postuserdetail.PostUserDetailScreen
import com.nyvoratech.composebase.ui.postusers.ui.postuserlist.PostUsersScreen
import com.nyvoratech.composebase.ui.session.SharedSessionViewModel
import com.nyvoratech.composebase.ui.users.ui.UsersScreen

/**
 * Root navigation graph. Login and Users both live inside [Screen.SessionGraph],
 * so a [SharedSessionViewModel] requested with `hiltViewModel(parentEntry)`
 * from either screen resolves to the SAME instance — this is the
 * recommended way to share state across a multi-step flow without a
 * process-wide singleton.
 */
@Composable
fun ComposeBaseNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.SessionGraph) {
        navigation<Screen.SessionGraph>(startDestination = Screen.Login) {

            composable<Screen.Login> { backStackEntry ->
                val sharedSessionViewModel = backStackEntry.sharedSessionViewModel(navController)

                LoginScreen(
                    onNavigateToUsers = {
                        navController.navigate(Screen.Users) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    },
                    // LoginViewModel is screen-scoped (default hiltViewModel()); only the
                    // session state below is intentionally shared.
                )
                // Keep the shared VM alive even though LoginScreen doesn't consume it directly.
//                sharedSessionViewModel
            }

            composable<Screen.Users> { backStackEntry ->
                val sharedSessionViewModel = backStackEntry.sharedSessionViewModel(navController)
                UsersScreen(
                    sharedSessionViewModel = sharedSessionViewModel
                )
            }

            composable<Screen.PostUsers> {
                PostUsersScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.PostUserDetail(id)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Screen.PostUserDetail> {
                PostUserDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Resolves a [SharedSessionViewModel] scoped to the parent [Screen.SessionGraph]
 * back stack entry, so every screen inside that graph shares one instance.
 */
@Composable
private fun NavBackStackEntry.sharedSessionViewModel(
    navController: androidx.navigation.NavController
): SharedSessionViewModel {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(Screen.SessionGraph)
    }
    return hiltViewModel(parentEntry)
}
