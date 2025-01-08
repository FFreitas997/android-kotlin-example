package com.ffreitas.flowify.data.network.firestore

import com.ffreitas.flowify.data.models.Board
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class BoardFirestoreService(service: FirebaseFirestore) : FirestoreService<Board> {

    private val boardCollection = service.collection("boards")

    override suspend fun create(documentID: String, model: Board) {
        boardCollection
            .document(documentID)
            .set(model, SetOptions.merge())
            .await()
    }

    override suspend fun read(documentID: String): Board {
        val snapshot = boardCollection
            .document(documentID)
            .get()
            .await()
        val board = snapshot.toObject(Board::class.java)
        checkNotNull(board) { "Board not found" }
        return board
    }

    override suspend fun update(documentID: String, fields: Map<String, Any>) {
        boardCollection
            .document(documentID)
            .update(fields)
            .await()
    }

    override suspend fun delete(documentID: String) {
        boardCollection
            .document(documentID)
            .delete()
            .await()
    }
}