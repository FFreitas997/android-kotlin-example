package com.ffreitas.flowify.ui.home.components.board.create

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.auth.DefaultAuthRepository
import com.ffreitas.flowify.data.repository.board.BoardRepository
import com.ffreitas.flowify.data.repository.board.DefaultBoardRepository
import com.ffreitas.flowify.data.repository.storage.DefaultStorageRepository
import com.ffreitas.flowify.data.repository.storage.StorageRepository
import com.ffreitas.flowify.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class CreateBoardViewModel(
    private val repository: BoardRepository,
    private val storage: StorageRepository,
    private val authentication: AuthRepository
) : ViewModel() {

    private val _state: MutableStateFlow<CreateBoardUIState<Board>?> = MutableStateFlow(null)
    val state: StateFlow<CreateBoardUIState<Board>?> = _state.asStateFlow()

    private var name = ""
    private var boardPicture: File? = null

    fun createBoard() {
        viewModelScope.launch {
            try {
                _state.update { CreateBoardUIState.Loading }
                require(isNameValid() && isPictureValid()) {
                    "Invalid name or picture"
                }
                val boardImageUri = storage.uploadBoardPicture(boardPicture!!)
                val board = Board(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    picture = boardImageUri.toString(),
                    createdBy = authentication.getCurrentUser()?.uid ?: ""
                )
                repository.createBoard(board)
                _state.update { CreateBoardUIState.Success(board) }
            } catch (e: Exception) {
                _state.update { CreateBoardUIState.Error(e.message ?: "Failed to create board") }
            }
        }
    }

    fun handleNameChanged(name: Editable?) {
        name?.let { this.name = it.toString() }
    }

    fun handlePictureSelection(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
            } catch (e: Exception) {
                Log.e(TAG, "Failed to handle picture selection", e)
            }
        }
    }

    fun isNameValid() = name.isNotEmpty() && name.length <= Constants.MAX_BOARD_NAME_LENGTH

    fun isPictureValid() = boardPicture != null && boardPicture!!.exists()


    companion object {
        const val TAG = "CreateBoardViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val boardRepository = DefaultBoardRepository(application.boardStorage)
                val storageRepository = DefaultStorageRepository(application.resourceStorage)
                val authenticationRepository = DefaultAuthRepository(application.authentication)
                return CreateBoardViewModel(boardRepository, storageRepository, authenticationRepository) as T
            }
        }
    }
}

sealed interface CreateBoardUIState<out S> {
    data object Loading : CreateBoardUIState<Nothing>
    data class Success<S>(val result: S) : CreateBoardUIState<S>
    data class Error(val message: String) : CreateBoardUIState<Nothing>
}