package com.nyvoratech.composebase.ui.users.domain.model

import com.nyvoratech.composebase.ui.users.data.local.UserEntity

/**
 * Domain-level representation of a User. This is what ViewModels and
 * UseCases work with — it has no knowledge of Retrofit DTOs, Room
 * entities, or Firestore documents.
 */

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?
)

fun User.toEntity(): UserEntity {
    return UserEntity(id = id, name = name, email = email, avatarUrl = avatarUrl)
}
