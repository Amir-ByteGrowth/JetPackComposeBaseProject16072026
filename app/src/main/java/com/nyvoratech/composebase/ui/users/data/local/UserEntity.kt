package com.nyvoratech.composebase.ui.users.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local cache representation of a User. Kept separate from the network DTO
 * and the domain model so each layer can evolve independently.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?
)
