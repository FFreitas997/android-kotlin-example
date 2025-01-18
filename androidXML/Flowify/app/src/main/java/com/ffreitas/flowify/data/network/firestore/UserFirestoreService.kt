package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.utils.Constants.USER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserFirestoreService(
    service: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FirestoreService<User> {

    override val collection = service.collection(USER_COLLECTION)

    override suspend fun create(documentID: String, model: User): Unit =
        withContext(ioDispatcher) {
            collection
                .document(documentID)
                .set(model, SetOptions.merge())
                .await()
        }

    override suspend fun read(documentID: String): User =
        withContext(ioDispatcher) {
            val snapshot = collection
                .document(documentID)
                .get()
                .await()
            snapshot.toObject(User::class.java) ?: throw Exception("User not found")
        }

    override suspend fun readWhereEquals(field: String, value: Any): List<User> =
        withContext(ioDispatcher) {
            val snapshot = collection
                .whereEqualTo(field, value)
                .get()
                .await()
            snapshot.toObjects(User::class.java)
        }

    override suspend fun readWhereIn(field: String, values: List<Any>): List<User> =
        withContext(ioDispatcher) {
            val snapshot = collection
                .whereIn(field, values)
                .get()
                .await()
            snapshot.toObjects(User::class.java)
        }

    override suspend fun readWhereArrayContains(field: String, value: Any): List<User> =
        withContext(ioDispatcher) {
            val snapshot = collection
                .whereArrayContains(field, value)
                .get()
                .await()
            snapshot.toObjects(User::class.java)
        }

    override suspend fun update(documentID: String, fields: Map<String, Any>): Unit =
        withContext(ioDispatcher) {
            collection
                .document(documentID)
                .update(fields)
                .await()
        }

    override suspend fun delete(documentID: String): Unit =
        withContext(ioDispatcher) {
            collection
                .document(documentID)
                .delete()
                .await()
        }
}