package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.network.models.User

interface UserRepository {

    suspend fun storeUser(user: User): Boolean

    suspend fun getUser(email: String): User?
}