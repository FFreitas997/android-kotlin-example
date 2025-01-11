package com.ffreitas.flowify.data.repository.board

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.network.firestore.BoardFirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultBoardRepository(private val service: BoardFirestoreService): BoardRepository {

    private val context = Dispatchers.IO

    override suspend fun createBoard(board: Board) =
        withContext(context) {
            require(board.id.isNotEmpty()) { "Board id cannot be empty" }
            service.create(board.id, board)
        }


    override suspend fun getBoard(id: String) =
        withContext(context) { service.read(id) }

    override suspend fun updateBoard() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBoard() {
        TODO("Not yet implemented")
    }

    override suspend fun getBoards() {
        TODO("Not yet implemented")
    }

    override suspend fun getBoardsByUserID(userID: String) =
        withContext(context) { service.readAllByUserID(userID) }
}