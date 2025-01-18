package com.ffreitas.flowify.data.network.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultFirebaseAuthService(
    private val service: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FirebaseAuthService {

    override suspend fun signUp(name: String, email: String, password: String): FirebaseUser? =
        withContext(ioDispatcher) {
            val result = service
                .createUserWithEmailAndPassword(email, password)
                .await()

            checkNotNull(result) { "Failed to create an user" }

            result.user?.let { user ->
                val request = UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(request).await()
            }

            result.user
        }

    override suspend fun signIn(email: String, password: String): FirebaseUser? =
        withContext(ioDispatcher) {
            val result = service
                .signInWithEmailAndPassword(email, password)
                .await()

            result.user
        }


    override fun signOut() = service.signOut()

    override fun getCurrentUser(): FirebaseUser? = service.currentUser
}
