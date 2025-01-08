package com.ffreitas.flowify.ui.home

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SharedViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state: MutableStateFlow<HomeUIState<User>?> = MutableStateFlow(null)
    val state: StateFlow<HomeUIState<User>?> = _state.asStateFlow()

    var currentUser: User? = null

    init { getCurrentUser() }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val result = userRepository.getCurrentUser()
                checkNotNull(result) { "User not found" }
                currentUser = result
                _state.update { HomeUIState.Success(result) }
            } catch (e: Exception) {
                _state.update { HomeUIState.Error(e.message ?: "Failed to get user") }
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
    data class Success<S>(val data: S) : HomeUIState<S>
    data class Error(val message: String) : HomeUIState<Nothing>
}