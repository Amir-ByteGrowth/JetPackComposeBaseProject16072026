package com.nyvoratech.composebase.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.core.common.safeCall
import com.nyvoratech.composebase.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Resource<Unit> = safeCall {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    override suspend fun signUp(email: String, password: String): Resource<Unit> = safeCall {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        Unit
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
