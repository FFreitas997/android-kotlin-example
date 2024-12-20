package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.network.firestore.FirestoreService
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.network.auth.FirebaseAuthService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultUserRepository(
    private val firestore: FirestoreService,
    private val authentication: FirebaseAuthService? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    override suspend fun createUser(user: User): Boolean =
        withContext(dispatcher) { firestore.storeUser(user) }

    override suspend fun getUser(email: String) =
        withContext(dispatcher) { firestore.getUser(email) }

    override suspend fun getCurrentUser(): User? {
        authentication ?: return null
        return withContext(dispatcher) {
            val current = authentication.getCurrentUser() ?: return@withContext null
            firestore.getUser(current.email ?: return@withContext null)
        }
    }

    override suspend fun updateUser(user: User): Boolean =
        withContext(dispatcher) { firestore.storeUser(user) }
}