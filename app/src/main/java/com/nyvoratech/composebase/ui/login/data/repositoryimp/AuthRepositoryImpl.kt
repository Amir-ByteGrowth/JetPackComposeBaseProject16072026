package com.nyvoratech.composebase.ui.login.data.repositoryimp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.nyvoratech.composebase.core.common.FirebaseCrashReporter
import com.nyvoratech.composebase.core.di.auth.TokenManager
import com.nyvoratech.composebase.core.network.AppError
import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.ui.login.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val crashReporter: FirebaseCrashReporter,
    private val tokenManager: TokenManager
) : AuthRepository {

    override fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Resource<Unit> {

        return safeFirebaseCall {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val idToken = firebaseAuth.currentUser
                ?.getIdToken(false)
                ?.await()
                ?.token
                ?: throw IllegalStateException(
                    "Firebase ID token is null"
                )
//            Log.d("FirebaseToken", idToken)
            tokenManager.updateAccessToken(idToken)
        }
    }

    override suspend fun signUp(email: String, password: String): Resource<Unit> {

        return safeFirebaseCall {
            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

        }
    }

    override fun signOut(): Resource<Unit> {

        return try {
            firebaseAuth.signOut()
            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error(
                AppError.Unknown
            )
        }
    }

    private suspend fun safeFirebaseCall(
        action: suspend () -> Unit
    ): Resource<Unit> {

        return try {
            action()
            Resource.Success(Unit)
        } catch (e: FirebaseAuthException) {

            crashReporter.recordException(e, "FirebaseLoginError")

            Resource.Error(
                AppError.Firebase(
                    code = e.errorCode,
                    message = e.message
                )
            )
        } catch (e: Exception) {

            Timber.tag("FirebaseError").d(e.message.toString())
            crashReporter.recordException(e, "Unexpected_Firebase_Error")
            Resource.Error(
                AppError.Firebase("Unexpected_Firebase_Error", e.message)
            )
        }
    }
}