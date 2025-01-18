package com.ffreitas.flowify.ui.main.components.board.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.models.Task
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.board.BoardRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailsBoardViewModel @Inject constructor(
    val repository: BoardRepository,
    val authRepository: AuthRepository
) : ViewModel() {

    private val _boardState: MutableLiveData<DetailsBoardUIState<Board>> = MutableLiveData()
    val boardState: MutableLiveData<DetailsBoardUIState<Board>> = _boardState

    private val _createTaskState: MutableLiveData<DetailsBoardUIState<Task>> = MutableLiveData()
    val createTaskState: MutableLiveData<DetailsBoardUIState<Task>> = _createTaskState

    private var currentUser: FirebaseUser? = null
    var currentBoard: Board? = null

    init {
        authRepository
            .getCurrentUser()
            ?.let { currentUser = it }
    }

    fun createTask(title: String?, description: String?) {
        viewModelScope.launch {
            try {
                _createTaskState.postValue(DetailsBoardUIState.Loading)
                checkNotNull(currentUser) { "User not found" }
                require(!title.isNullOrEmpty()) { "Title cannot be empty" }
                require(!description.isNullOrEmpty()) { "Description cannot be empty" }
                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    createdBy = currentUser?.uid!!,
                    createdByName = currentUser?.displayName ?: "Anonymous",
                    createdAt = getCurrentDateTime()
                )
                require(currentBoard != null) { "Board not found" }
                val result = repository.createTask(newTask, currentBoard!!)
                currentBoard = currentBoard!!.copy(taskList = currentBoard!!.taskList + result)
                _createTaskState.postValue(DetailsBoardUIState.Success(result))
            } catch (e: Exception) {
                _createTaskState.postValue(
                    DetailsBoardUIState.Error(
                        e.message ?: "Failed to create task"
                    )
                )
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        return current.format(formatter)
    }

    fun currentBoard(boardId: String) {
        viewModelScope.launch {
            try {
                _boardState.postValue(DetailsBoardUIState.Loading)
                if (currentBoard != null)
                    _boardState.postValue(DetailsBoardUIState.Success(currentBoard!!))
                val result = repository.getBoard(boardId)
                currentBoard = result
                _boardState.postValue(DetailsBoardUIState.Success(result))
            } catch (e: Exception) {
                _boardState.postValue(
                    DetailsBoardUIState.Error(e.message ?: "Failed to fetch tasks")
                )
            }
        }
    }

    fun deleteTask(itemDeleted: Task) {
        viewModelScope.launch {
            try {
                require(currentBoard != null) { "Board not found" }
                repository.removeTask(itemDeleted, currentBoard!!)
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "Failed to delete task")
            }
        }
    }

    fun getCurrentBoardID(): String? {
        return currentBoard?.id
    }

    companion object {
        private const val TAG = "DetailsBoardViewModel"
    }
}

sealed interface DetailsBoardUIState<out S> {
    data object Loading : DetailsBoardUIState<Nothing>
    data class Success<S>(val data: S) : DetailsBoardUIState<S>
    data class Error(val message: String) : DetailsBoardUIState<Nothing>
}

