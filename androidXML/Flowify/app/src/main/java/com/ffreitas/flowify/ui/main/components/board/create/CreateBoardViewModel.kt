package com.ffreitas.flowify.ui.main.components.board.create

import android.content.Context
import android.net.Uri
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.board.BoardRepository
import com.ffreitas.flowify.data.repository.storage.StorageRepository
import com.ffreitas.flowify.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateBoardViewModel @Inject constructor(
    private val repository: BoardRepository,
    private val storage: StorageRepository,
    private val authentication: AuthRepository
) : ViewModel() {

    private val _state: MutableLiveData<CreateBoardUIState<Board>> = MutableLiveData()
    val state: LiveData<CreateBoardUIState<Board>> = _state

    private var name = ""
    private var boardPicture: File? = null

    fun createBoard() {
        viewModelScope.launch {
            try {
                _state.postValue(CreateBoardUIState.Loading)
                require(isNameValid()) { "Invalid name" }
                require(isPictureValid()) { "Invalid picture" }
                val boardImageUri = storage.uploadBoardPicture(boardPicture!!)
                val currentUser = authentication.getCurrentUser()
                checkNotNull(currentUser) { "User not authenticated" }
                val board = Board(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    picture = boardImageUri.toString(),
                    createdBy = currentUser.uid,
                    createdAt = getCurrentDateTime(),
                    assignTo = listOf(currentUser.uid),
                    taskList = emptyList()
                )
                repository.createBoard(board)
                _state.postValue(CreateBoardUIState.Success(board))
            } catch (e: Exception) {
                _state.postValue(TypeError.SubmitError(e.message ?: "Failed to create board"))
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        return current.format(formatter)
    }

    fun handleNameChanged(name: Editable?) {
        name?.let { this.name = it.toString() }
    }

    fun handlePictureSelection(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.postValue(CreateBoardUIState.Loading)
                val filename = "${Constants.BOARD_FILE_PREFIX}_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, filename)
                context
                    .contentResolver
                    .openInputStream(uri)
                    .use { input ->
                        file
                            .outputStream()
                            .use { out -> input?.copyTo(out) }
                    }
                boardPicture = file
                _state.postValue(CreateBoardUIState.None)
            } catch (e: Exception) {
                _state.postValue(
                    TypeError.FileError(
                        e.message ?: "Failed to handle picture selection"
                    )
                )
            }
        }
    }

    fun isNameValid() = name.isNotEmpty() && name.length <= Constants.MAX_BOARD_NAME_LENGTH

    fun isPictureValid() = boardPicture != null && boardPicture!!.exists()
}

sealed interface CreateBoardUIState<out S> {
    data object None : CreateBoardUIState<Nothing>
    data object Loading : CreateBoardUIState<Nothing>
    data class Success<S>(val result: S) : CreateBoardUIState<S>
    data class Error(val message: String) : TypeError
}

sealed interface TypeError : CreateBoardUIState<Nothing> {
    data class SubmitError(val message: String) : TypeError
    data class FileError(val message: String) : TypeError
}