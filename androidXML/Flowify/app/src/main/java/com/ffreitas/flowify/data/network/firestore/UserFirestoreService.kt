package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.utils.Constants.USER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserFirestoreService(service: FirebaseFirestore) : FirestoreService<User> {

    override val collection = service.collection(USER_COLLECTION)

    override suspend fun create(documentID: String, model: User) {
        collection
            .document(documentID)
            .set(model, SetOptions.merge())
            .await()
    }

    override suspend fun read(documentID: String): User {
        val snapshot = collection
            .document(documentID)
            .get()
            .await()
        return snapshot.toObject(User::class.java) ?: throw Exception("User not found")
    }

    override suspend fun readWhereEquals(field: String, value: String): List<User> {
        val snapshot = collection
            .whereEqualTo(field, value)
            .get()
            .await()
        return snapshot.toObjects(User::class.java)
    }

    override suspend fun update(documentID: String, fields: Map<String, Any>) {
        collection
            .document(documentID)
            .update(fields)
            .await()
    }

    override suspend fun delete(documentID: String) {
        collection
            .document(documentID)
            .delete()
            .await()
    }
}