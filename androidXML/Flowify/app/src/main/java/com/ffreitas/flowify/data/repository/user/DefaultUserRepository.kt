package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.network.auth.FirebaseAuthService
import com.ffreitas.flowify.data.network.firestore.FirestoreService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultUserRepository(
    private val firestore: FirestoreService<User>,
    private val authentication: FirebaseAuthService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    override suspend fun createUser(user: User) =
        withContext(dispatcher) {
            require(user.id.isNotEmpty()) { "User id cannot be empty" }
            firestore.create(user.id, user)
        }

    override suspend fun getUserByID(id: String) =
        withContext(dispatcher) {
            firestore.read(id)
        }

    override suspend fun getUserByEmail(email: String): User? =
        withContext(dispatcher) {
            firestore
                .readWhereEquals("email", email)
                .firstOrNull()
        }

    override suspend fun getCurrentUser(): User? =
        withContext(dispatcher) {
            authentication
                .getCurrentUser()
                ?.let { current -> firestore.read(current.uid) }
        }

    override suspend fun updateUser(user: User) =
        withContext(dispatcher) {
            require(user.id.isNotEmpty()) { "User id cannot be empty" }
            val fields = mapOf(
                "name" to user.name,
                "picture" to user.picture,
                "mobile" to user.mobile
            )
            firestore.update(user.id, fields)
        }
}