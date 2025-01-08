package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.utils.Constants.USER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserFirestoreService(service: FirebaseFirestore) : FirestoreService<User> {

    private val userCollection = service.collection(USER_COLLECTION)

    override suspend fun create(documentID: String, model: User) {
        userCollection
            .document(documentID)
            .set(model, SetOptions.merge())
            .await()
    }

    override suspend fun read(documentID: String): User {
        val snapshot = userCollection
            .document(documentID)
            .get()
            .await()
        val user = snapshot.toObject(User::class.java)
        checkNotNull(user) { "User not found" }
        return user
    }

    override suspend fun update(documentID: String, fields: Map<String, Any>) {
        userCollection
            .document(documentID)
            .update(fields)
            .await()
    }

    override suspend fun delete(documentID: String) {
        userCollection
            .document(documentID)
            .delete()
            .await()
    }
}