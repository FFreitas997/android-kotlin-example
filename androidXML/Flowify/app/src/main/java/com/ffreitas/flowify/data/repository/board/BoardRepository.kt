package com.ffreitas.flowify.data.repository.board

import com.ffreitas.flowify.data.models.Board

interface BoardRepository {

    suspend fun createBoard(board: Board)

    suspend fun getBoard(id: String): Board

    suspend fun updateBoard(board: Board)

    suspend fun deleteBoard(id: String)

    suspend fun getBoardsByUserID(userID: String): List<Board>
}