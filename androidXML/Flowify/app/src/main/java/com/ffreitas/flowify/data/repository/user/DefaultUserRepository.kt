package com.ffreitas.flowify.data.repository.user

import com.ffreitas.flowify.data.network.firestore.FirestoreService
import com.ffreitas.flowify.data.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultUserRepository(
    private val firestore: FirestoreService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    override suspend fun storeUser(user: User): Boolean =
        withContext(dispatcher) {
            firestore.storeUser(user)
        }

    override suspend fun getUser(email: String) =
        withContext(dispatcher) {
            firestore.getUser(email)
        }

}