package com.ffreitas.flowify.data.repository.auth

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
            require(name.isNotEmpty()) { "Name must not be empty" }
            require(email.isNotEmpty()) { "Email must not be empty" }
            require(password.isNotEmpty()) { "Password must not be empty" }
            firebase.signUp(name, email, password)
        }

    override suspend fun signIn(email: String, password: String) =
        withContext(dispatcher) {
            require(email.isNotEmpty()) { "Email must not be empty" }
            require(password.isNotEmpty()) { "Password must not be empty" }
            firebase.signIn(email, password)
        }

    override fun getCurrentUser(): FirebaseUser? = firebase.getCurrentUser()

    override fun signOut() = firebase.signOut()
}