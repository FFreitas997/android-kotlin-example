package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.utils.Constants.USER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class DefaultFirestoreService(private val service: FirebaseFirestore) : FirestoreService {

    override suspend fun storeUser(user: User): Boolean {
        require(user.id.isNotEmpty()) { "User id cannot be empty" }
        service
            .collection(USER_COLLECTION)
            .document(user.id)
            .set(user, SetOptions.merge())
            .await()
        return true
    }

    override suspend fun updateUser(id: String, fields: Map<String, Any>): Boolean {
        require(fields.isNotEmpty()) { "Fields cannot be empty" }
        require(id.isNotEmpty()) { "User id cannot be empty" }
        service
            .collection(USER_COLLECTION)
            .document(id)
            .update(fields)
            .await()
        return true
    }

    override suspend fun getUser(email: String): User? {
        val snapshot = service
            .collection(USER_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .await()
        require(!snapshot.isEmpty) { "User with email $email not found" }
        return snapshot
            .documents[0]
            .toObject(User::class.java)
    }
}