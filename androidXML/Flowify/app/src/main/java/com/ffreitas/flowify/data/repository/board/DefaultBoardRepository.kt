package com.ffreitas.flowify.data.repository.board

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.models.Task
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
                "taskList" to board.taskList,
                "assignTo" to board.assignTo
            )
            service.update(board.id, fields)
        }

    override suspend fun deleteBoard(id: String) =
        withContext(context) { service.delete(id) }

    override suspend fun getBoardsByUserID(userID: String) =
        withContext(context) { service.readWhereEquals("createdBy", userID) }

    override suspend fun createTask(task: Task, board: Board) =
        withContext(context) {
            require(task.id.isNotEmpty()) { "Task id cannot be empty" }
            val updatedBoard = board
                .copy(taskList = board.taskList + task)
            updateBoard(updatedBoard)
            task
        }

    override suspend fun getTasksByUserID(userID: String): List<Task> =
        withContext(context) {
            val boards = service.readWhereArrayContains("assignTo", userID)
            boards.flatMap { board ->
                board
                    .taskList
                    .map { task -> task.copy(boardName = board.name, boardImage = board.picture) }
            }
        }

    override suspend fun assignUserToBoard(boardID: String, userID: String) =
        withContext(context) {
            val board = getBoard(boardID)
            val updatedBoard = board
                .copy(assignTo = board.assignTo + userID)
            updateBoard(updatedBoard)
        }

    override suspend fun removeTask(task: Task, board: Board) =
        withContext(context) {
            require(task.id.isNotEmpty()) { "Task id cannot be empty" }
            val updatedBoard = board
                .copy(taskList = board.taskList.filter { it.id != task.id })
            updateBoard(updatedBoard)
        }

    override suspend fun deleteMember(currentBoardID: String, userID: String) =
        withContext(context) {
            val board = getBoard(currentBoardID)
            val updatedBoard = board
                .copy(assignTo = board.assignTo.filter { it != userID })
            updateBoard(updatedBoard)
        }
}