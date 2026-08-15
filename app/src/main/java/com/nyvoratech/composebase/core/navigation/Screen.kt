package com.nyvoratech.composebase.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations (Navigation Compose 2.8+ supports
 * @Serializable route objects directly, no more string route parsing).
 */
sealed interface Screen {

    @Serializable
    data object SessionLoading : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Users : Screen

    @Serializable
    data object PostUsers : Screen

    @Serializable
    data class PostUserDetail(val id: Long) : Screen

    /** Shared parent route both Login and Users belong to, so they can share a ViewModel. */
    @Serializable
    data object SessionGraph : Screen
}
