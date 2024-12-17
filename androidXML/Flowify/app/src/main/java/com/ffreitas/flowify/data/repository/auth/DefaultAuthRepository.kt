package com.ffreitas.flowify.data.repository.auth

import android.util.Log
import com.ffreitas.flowify.data.network.auth.FirebaseAuthService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "DefaultAuthRepository"

class DefaultAuthRepository(
    private val firebase: FirebaseAuthService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String) =
        withContext(dispatcher) {
            try {
                firebase.signUp(name, email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Error Sign Up ${e.message}")
                null
            }
        }

    override suspend fun signIn(email: String, password: String) =
        withContext(dispatcher) {
            try {
                firebase.signIn(email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Error Sign In ${e.message}")
                null
            }
        }

    override fun getCurrentUser(): FirebaseUser? = firebase.getCurrentUser()

    override fun signOut() = firebase.signOut()
}