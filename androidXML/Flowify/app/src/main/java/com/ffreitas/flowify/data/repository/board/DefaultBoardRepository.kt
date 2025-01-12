package com.ffreitas.flowify.data.repository.board

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.network.firestore.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultBoardRepository(private val service: FirestoreService<Board>) : BoardRepository {

    private val context = Dispatchers.IO

    override suspend fun createBoard(board: Board) =
        withContext(context) {
            require(board.id.isNotEmpty()) { "Board id cannot be empty" }
            service.create(board.id, board)
        }

    override suspend fun getBoard(id: String) =
        withContext(context) { service.read(id) }

    override suspend fun updateBoard(board: Board) =
        withContext(context) {
            require(board.id.isNotEmpty()) { "Board id cannot be empty" }
            val fields = mapOf(
                "name" to board.name,
                "picture" to board.picture,
                "assignTo" to board.assignTo
            )
            service.update(board.id, fields)
        }

    override suspend fun deleteBoard(id: String) =
        withContext(context) { service.delete(id) }

    override suspend fun getBoardsByUserID(userID: String) =
        withContext(context) { service.readWhereEquals("createdBy", userID) }
}