package com.ffreitas.flowify.data.repository.board

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.models.Task

interface BoardRepository {

    suspend fun createBoard(board: Board)

    suspend fun getBoard(id: String): Board

    suspend fun updateBoard(board: Board)

    suspend fun deleteBoard(id: String)

    suspend fun getBoardsByUserID(userID: String): List<Board>

    suspend fun createTask(task: Task, board: Board): Task

    suspend fun getTasksByUserID(userID: String): List<Task>

    suspend fun assignUserToBoard(boardID: String, userID: String)

    suspend fun removeTask(task: Task, board: Board)

    suspend fun deleteMember(currentBoardID: String, userID: String)
}