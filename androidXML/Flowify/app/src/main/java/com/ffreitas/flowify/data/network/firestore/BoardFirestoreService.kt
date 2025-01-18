package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.utils.Constants.BOARD_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BoardFirestoreService(
    service: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FirestoreService<Board> {

    override val collection = service.collection(BOARD_COLLECTION)

    override suspend fun create(documentID: String, model: Board): Unit =
        withContext(ioDispatcher) {
            collection
                .document(documentID)
                .set(model, SetOptions.merge())
                .await()
        }

    override suspend fun read(documentID: String): Board =
        withContext(ioDispatcher) {
            val snapshot = collection
                .document(documentID)
                .get()
                .await()

            snapshot.toObject(Board::class.java) ?: throw Exception("Board not found")
        }

    override suspend fun readWhereEquals(field: String, value: Any): List<Board> =
        withContext(ioDispatcher) {
            val snapshot = collection
                .whereEqualTo(field, value)
                .get()
                .await()
            snapshot.toObjects(Board::class.java)
        }

    override suspend fun readWhereIn(field: String, values: List<Any>): List<Board> =
        withContext(ioDispatcher) {
            val snapshot = collection
                .whereIn(field, values)
                .get()
                .await()
            snapshot.toObjects(Board::class.java)
        }

    override suspend fun readWhereArrayContains(field: String, value: Any): List<Board> =
        withContext(ioDispatcher) {
            val snapshot = collection
                .whereArrayContains(field, value)
                .get()
                .await()
            snapshot.toObjects(Board::class.java)
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