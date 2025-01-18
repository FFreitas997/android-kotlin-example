package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.network.firestore.FirestoreService

class DefaultUserRepository(private val service: FirestoreService<User>) : UserRepository {

    override suspend fun createUser(user: User) {
        service.create(user.id, user)
    }

    override suspend fun getUserByID(id: String): User {
        return service.read(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return service
            .readWhereEquals("email", email)
            .firstOrNull()
    }

    override suspend fun getUsersByMultipleIDs(ids: List<String>): List<User> {
        return service.readWhereIn("id", ids)
    }

    override suspend fun updateUser(user: User) {
        val fields = mapOf(
            "name" to user.name,
            "picture" to user.picture,
            "mobile" to user.mobile
        )
        service.update(user.id, fields)
    }
}