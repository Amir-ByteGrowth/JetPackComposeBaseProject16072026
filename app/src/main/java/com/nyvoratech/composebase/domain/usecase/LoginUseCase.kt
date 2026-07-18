package com.nyvoratech.composebase.domain.usecase

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Validates input before delegating to the repository. Validation rules
 * belong in the domain layer so they're testable without Android/Firebase
 * and reusable from any UI (Compose, widgets, etc).
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<Unit> {
        if (email.isBlank() || !email.contains("@")) {
            return Resource.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return Resource.Error("Password must be at least 6 characters.")
        }
        return authRepository.signIn(email, password)
    }
}
