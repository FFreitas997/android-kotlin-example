package com.ffreitas.flowify.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state: MutableLiveData<HomeUIState<User>> = MutableLiveData()
    val state: LiveData<HomeUIState<User>> = _state

    private val _shouldFetchBoards: MutableLiveData<Boolean> = MutableLiveData()
    val shouldFetchBoards: LiveData<Boolean> = _shouldFetchBoards

    init {
        getCurrentUser()
    }

    fun signalBoardsFetch() {
        _shouldFetchBoards.postValue(true)
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _state.postValue(HomeUIState.Loading)
                val authenticated = authRepository.getCurrentUser()
                if (authenticated == null) {
                    _state.postValue(HomeUIState.Error("User not found"))
                    return@launch
                }
                val result = userRepository.getUserByID(authenticated.uid)
                _state.postValue(HomeUIState.Success(result))
            } catch (e: Exception) {
                _state.postValue(HomeUIState.Error(e.message ?: "An error occurred"))
            }
        }
    }

    fun signOut() = authRepository.signOut()
}

sealed interface HomeUIState<out S> {
    data object Loading : HomeUIState<Nothing>
    data class Success<S>(val data: S) : HomeUIState<S>
    data class Error(val message: String) : HomeUIState<Nothing>
}