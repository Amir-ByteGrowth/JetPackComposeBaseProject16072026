package com.nyvoratech.composebase.ui.users.domain.usecases

import com.nyvoratech.composebase.ui.users.domain.model.User
import com.nyvoratech.composebase.ui.users.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Encapsulates "observe the list of users" business logic. Right now it's a
 * thin pass-through, but this is where you'd add sorting, filtering, or
 * combining multiple repositories as the app grows — without touching the
 * ViewModel or the repository implementation.
 */
class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = userRepository.observeUsers()
}
