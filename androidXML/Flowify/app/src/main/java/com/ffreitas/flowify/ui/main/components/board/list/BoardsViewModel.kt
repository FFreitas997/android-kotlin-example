package com.ffreitas.flowify.ui.main.components.board.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.repository.board.BoardRepository
import com.ffreitas.flowify.data.repository.board.DefaultBoardRepository
import kotlinx.coroutines.launch

class BoardsViewModel(private val repository: BoardRepository) : ViewModel() {

    private val _state: MutableLiveData<BoardsState> = MutableLiveData()
    val state: LiveData<BoardsState> = _state

    fun fetchBoards(currentUserID: String) {
        viewModelScope.launch {
            try {
                _state.postValue(BoardsState.Loading)
                repository
                    .getBoardsByUserID(currentUserID)
                    .let { boards -> _state.postValue(BoardsState.Success(boards)) }
            } catch (e: Exception) {
                _state.postValue(BoardsState.Error(e.message ?: "An error occurred"))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val boardRepository = DefaultBoardRepository(application.boardStorage)
                return BoardsViewModel(repository = boardRepository) as T
            }
        }
    }
}

sealed interface BoardsState {
    data object Loading : BoardsState
    data class Error(val message: String) : BoardsState
    data class Success(val boards: List<Board>) : BoardsState
}