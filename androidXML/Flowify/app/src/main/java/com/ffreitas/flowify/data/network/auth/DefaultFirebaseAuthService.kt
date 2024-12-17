package com.ffreitas.flowify.data.network.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class DefaultFirebaseAuthService(private val instance: FirebaseAuth) : FirebaseAuthService {

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): FirebaseUser? {
        val result = instance
            .createUserWithEmailAndPassword(email, password)
            .await()

        if (result == null || result.user == null) {
            Log.e(TAG, "Failed to sign up")
            return null
        }

        val request = UserProfileChangeRequest
            .Builder()
            .setDisplayName(name)
            .build()

        result.user?.updateProfile(request)?.await()
        return result.user
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        val result = instance
            .signInWithEmailAndPassword(email, password)
            .await()

        if (result == null || result.user == null) {
            Log.e(TAG, "Failed to sign in")
            return null
        }

        return result.user
    }

    override fun signOut() = instance.signOut()

    override fun getCurrentUser(): FirebaseUser? = instance.currentUser

    companion object {
        private const val TAG = "DefaultFirebaseAuthService"
        private var instance: FirebaseAuth? = null

        fun create(): DefaultFirebaseAuthService {
            val instance = this.instance ?: FirebaseAuth.getInstance()
            return DefaultFirebaseAuthService(instance)
        }
    }

}
