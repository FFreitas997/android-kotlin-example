package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.utils.Constants.BOARD_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class BoardFirestoreService(service: FirebaseFirestore) : FirestoreService<Board> {

    override val collection = service.collection(BOARD_COLLECTION)

    override suspend fun create(documentID: String, model: Board) {
        collection
            .document(documentID)
            .set(model, SetOptions.merge())
            .await()
    }

    override suspend fun read(documentID: String): Board {
        val snapshot = collection
            .document(documentID)
            .get()
            .await()
        val board = snapshot.toObject(Board::class.java)
        checkNotNull(board) { "Board not found" }
        return board
    }

    override suspend fun readWhereEquals(field: String, value: Any): List<Board> {
        val snapshot = collection
            .whereEqualTo(field, value)
            .get()
            .await()
        return snapshot.toObjects(Board::class.java)
    }

    override suspend fun readWhereArrayContains(field: String, value: Any): List<Board> {
        val snapshot = collection
            .whereArrayContains(field, value)
            .get()
            .await()
        return snapshot.toObjects(Board::class.java)
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