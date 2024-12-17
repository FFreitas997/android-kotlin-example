package com.ffreitas.flowify.data.network.auth

import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthService {

    suspend fun signUp(name: String, email: String, password: String): FirebaseUser?

    suspend fun signIn(email: String, password: String): FirebaseUser?

    fun signOut()

    fun getCurrentUser(): FirebaseUser?
}