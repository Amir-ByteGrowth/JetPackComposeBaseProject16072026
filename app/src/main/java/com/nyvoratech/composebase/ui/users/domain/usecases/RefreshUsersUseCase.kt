package com.nyvoratech.composebase.ui.users.domain.usecases

import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.ui.users.domain.repository.UserRepository
import javax.inject.Inject

class RefreshUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<Unit> = userRepository.refreshUsers()
}
