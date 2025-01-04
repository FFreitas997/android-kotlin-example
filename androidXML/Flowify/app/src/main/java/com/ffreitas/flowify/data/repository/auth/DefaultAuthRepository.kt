package com.ffreitas.flowify.data.repository.auth

import android.util.Log
import com.ffreitas.flowify.data.network.auth.FirebaseAuthService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultAuthRepository(
    private val firebase: FirebaseAuthService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String) =
        withContext(dispatcher) {
            Log.d(TAG, "signUp: $email")
            require(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                "Name, email and password must not be empty"
            }
            firebase.signUp(name, email, password)
        }

    override suspend fun signIn(email: String, password: String) =
        withContext(dispatcher) {
            Log.d(TAG, "signIn: $email")
            require(email.isNotEmpty() && password.isNotEmpty()) {
                "Email and password must not be empty"
            }
            firebase.signIn(email, password)
        }

    override fun getCurrentUser(): FirebaseUser? = firebase.getCurrentUser()

    override fun signOut() = firebase.signOut()

    companion object {
        private const val TAG = "Auth Repository"
    }
}