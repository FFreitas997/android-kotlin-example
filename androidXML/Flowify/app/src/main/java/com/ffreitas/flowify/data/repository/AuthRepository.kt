package com.ffreitas.flowify.data.repository

interface AuthRepository {

    suspend fun signUp(name: String, email: String, password: String): Boolean

    suspend fun signIn(email: String, password: String): Boolean
}