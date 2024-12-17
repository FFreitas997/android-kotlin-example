package com.ffreitas.flowify.data.repository.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun signUp(name: String, email: String, password: String): FirebaseUser?

    suspend fun signIn(email: String, password: String): FirebaseUser?

    fun getCurrentUser(): FirebaseUser?

    fun signOut()
}