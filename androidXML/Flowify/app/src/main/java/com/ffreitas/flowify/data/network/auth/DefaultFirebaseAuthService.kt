package com.ffreitas.flowify.data.network.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class DefaultFirebaseAuthService(private val service: FirebaseAuth) : FirebaseAuthService {

    override suspend fun signUp(name: String, email: String, password: String): FirebaseUser? {
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

        return result.user
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        val result = service
            .signInWithEmailAndPassword(email, password)
            .await()

        checkNotNull(result) { "Failed to sign in" }

        return result.user
    }

    override fun signOut() = service.signOut()

    override fun getCurrentUser(): FirebaseUser? = service.currentUser
}
