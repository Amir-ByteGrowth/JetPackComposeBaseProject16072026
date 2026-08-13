package com.nyvoratech.composebase.ui.users.data.mapper

import com.nyvoratech.composebase.ui.users.data.local.UserEntity
import com.nyvoratech.composebase.ui.users.data.modeldto.UserDto
import com.nyvoratech.composebase.ui.users.domain.model.User

/**
 * Explicit mapping functions between the three representations of "user":
 * network DTO, local Entity, and domain Model. Keeping these in one file
 * makes it obvious where field-name drift or transformations happen.
 */

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatar
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatar
)
