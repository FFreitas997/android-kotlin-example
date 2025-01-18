package com.ffreitas.flowify.ui.main.components.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ffreitas.flowify.data.models.Task
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.board.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel@Inject constructor(
    private val repository: BoardRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _state: MutableLiveData<TasksState> = MutableLiveData()
    val state: LiveData<TasksState> = _state

    private var currentID: String? = null

    init {
        authRepository
            .getCurrentUser()
            ?.let { currentID = it.uid }
    }

    fun requestAllTasks() {
        viewModelScope.launch {
            try {
                _state.postValue(TasksState.Loading)
                require(!currentID.isNullOrEmpty()) { "Authenticated user not found" }
                val result = repository.getTasksByUserID(currentID!!)
                _state.postValue(TasksState.Success(result))
            } catch (e: Exception) {
                _state.postValue(TasksState.Error(e.message ?: "An error occurred"))
            }
        }
    }
}

sealed interface TasksState {
    data object Loading : TasksState
    data class Error(val message: String) : TasksState
    data class Success(val tasks: List<Task>) : TasksState
}