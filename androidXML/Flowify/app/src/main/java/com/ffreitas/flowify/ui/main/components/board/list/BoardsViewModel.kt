package com.ffreitas.flowify.ui.main.components.board.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.board.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardsViewModel @Inject constructor(
    private val repository: BoardRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state: MutableLiveData<BoardsState> = MutableLiveData()
    val state: LiveData<BoardsState> = _state

    init {
        fetchBoards()
    }

    fun fetchBoards() {
        viewModelScope.launch {
            try {
                _state.postValue(BoardsState.Loading)
                val currentUser = authRepository.getCurrentUser()
                checkNotNull(currentUser) { "Authenticated user not found" }
                val result = repository.getBoardsByUserID(currentUser.uid)
                _state.postValue(BoardsState.Success(result))
            } catch (e: Exception) {
                _state.postValue(BoardsState.Error(e.message ?: "An error occurred"))
            }
        }
    }
}

sealed interface BoardsState {
    data object Loading : BoardsState
    data class Error(val message: String) : BoardsState
    data class Success(val boards: List<Board>) : BoardsState
}