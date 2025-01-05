package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.models.User

interface UserRepository {

    suspend fun createUser(user: User): Boolean

    suspend fun getCurrentUser(): User?

    suspend fun updateUser(user: User): Boolean
}