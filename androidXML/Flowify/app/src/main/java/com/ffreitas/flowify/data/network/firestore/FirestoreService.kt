package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.User

interface FirestoreService {

    suspend fun storeUser(user: User): Boolean

    suspend fun updateUser(id: String, fields: Map<String, Any>): Boolean

    suspend fun getUser(email: String): User?
}