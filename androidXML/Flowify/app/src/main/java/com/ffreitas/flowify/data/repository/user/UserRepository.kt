package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.models.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserByID(id: String): User

    suspend fun getUserByEmail(email: String): User?

    suspend fun getCurrentUser(): User?

    suspend fun updateUser(user: User)

}