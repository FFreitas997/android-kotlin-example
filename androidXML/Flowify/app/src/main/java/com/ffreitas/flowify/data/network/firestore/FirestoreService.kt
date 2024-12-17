package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.network.models.User

interface FirestoreService {

    suspend fun storeUser(user: User): Boolean

    suspend fun getUser(email: String): User?
}