package com.ffreitas.flowify.data.repository.auth

import com.ffreitas.flowify.data.network.auth.FirebaseAuthService
import com.google.firebase.auth.FirebaseUser

class DefaultAuthRepository(private val service: FirebaseAuthService) : AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String): FirebaseUser? {
        return service.signUp(name, email, password)
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        return service.signIn(email, password)
    }

    override fun getCurrentUser(): FirebaseUser? = service.getCurrentUser()

    override fun signOut() = service.signOut()
}