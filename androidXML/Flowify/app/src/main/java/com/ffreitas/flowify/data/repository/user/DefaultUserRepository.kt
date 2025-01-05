package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.network.auth.FirebaseAuthService
import com.ffreitas.flowify.data.network.firestore.FirestoreService
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

    override suspend fun getCurrentUser(): User? =
        withContext(dispatcher) {
            checkNotNull(authentication) { "Auth service cannot be null" }
            authentication
                .getCurrentUser()
                ?.let { current -> firestore.getUser(current.email ?: "") }
        }

    override suspend fun updateUser(user: User): Boolean =
        withContext(dispatcher) { firestore.storeUser(user) }
}