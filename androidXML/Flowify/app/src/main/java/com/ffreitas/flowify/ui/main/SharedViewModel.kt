package com.ffreitas.flowify.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.auth.DefaultAuthRepository
import com.ffreitas.flowify.data.repository.user.DefaultUserRepository
import com.ffreitas.flowify.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class SharedViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state: MutableLiveData<HomeUIState<User>> = MutableLiveData()
    val state: LiveData<HomeUIState<User>> = _state

    init {
        getCurrentUser()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _state.postValue(HomeUIState.Loading)
                val result = userRepository.getCurrentUser()
                checkNotNull(result) { "User not found" }
                _state.postValue(HomeUIState.Success(result))
            } catch (e: Exception) {
                _state.postValue(HomeUIState.Error(e.message ?: "An error occurred"))
            }
        }
    }

    fun signOut() = authRepository.signOut()

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val authRepository = DefaultAuthRepository(application.authentication)
                val userRepository = DefaultUserRepository(
                    firestore = application.userStorage,
                    authentication = application.authentication
                )
                return SharedViewModel(authRepository, userRepository) as T
            }
        }
    }
}

sealed interface HomeUIState<out S> {
    data object Loading : HomeUIState<Nothing>
    data class Success<S>(val data: S) : HomeUIState<S>
    data class Error(val message: String) : HomeUIState<Nothing>
}