package com.ffreitas.flowify.data.repository.board

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.models.Task
import com.ffreitas.flowify.data.network.firestore.FirestoreService

class DefaultBoardRepository(private val service: FirestoreService<Board>) : BoardRepository {

    override suspend fun createBoard(board: Board) {
        service.create(board.id, board)
    }

    override suspend fun getBoard(id: String): Board {
        return service.read(id)
    }

    override suspend fun updateBoard(board: Board) {
        val fields = mapOf(
            "name" to board.name,
            "picture" to board.picture,
            "taskList" to board.taskList,
            "assignTo" to board.assignTo
        )
        service.update(board.id, fields)
    }

    override suspend fun deleteBoard(id: String) {
        service.delete(id)
    }

    override suspend fun getBoardsByUserID(userID: String): List<Board> {
        return service.readWhereEquals("createdBy", userID)
    }

    override suspend fun createTask(task: Task, board: Board): Task {
        return board
            .copy(taskList = board.taskList + task)
            .apply { updateBoard(this) }
            .let { task }
    }

    override suspend fun getTasksByUserID(userID: String): List<Task> {
        val boards = service.readWhereArrayContains("assignTo", userID)
        return boards
            .flatMap { board ->
                board
                    .taskList
                    .map { task -> task.copy(boardName = board.name, boardImage = board.picture) }
            }
    }

    override suspend fun assignUserToBoard(boardID: String, userID: String) {
        val board = getBoard(boardID)
        require(!board.assignTo.contains(userID)) { "User already assigned to board" }
        val updatedBoard = board
            .copy(assignTo = board.assignTo + userID)
        updateBoard(updatedBoard)
    }

    override suspend fun removeTask(task: Task, board: Board) {
        board
            .copy(taskList = board.taskList.filter { it.id != task.id })
            .apply { updateBoard(this) }
    }

    override suspend fun deleteMember(currentBoardID: String, userID: String) {
        val board = getBoard(currentBoardID)
        require(board.createdBy != userID) { "Cannot delete the creator of the board" }
        val updatedBoard = board.copy(assignTo = board.assignTo.filter { it != userID })
        updateBoard(updatedBoard)
    }
}