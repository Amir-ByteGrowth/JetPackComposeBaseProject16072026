package com.nyvoratech.composebase.core.database

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
