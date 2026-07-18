package com.nyvoratech.composebase.domain.model

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
