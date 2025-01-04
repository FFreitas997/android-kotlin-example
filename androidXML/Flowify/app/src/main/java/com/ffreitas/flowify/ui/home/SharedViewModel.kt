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
import kotlinx.coroutines.launch

class SharedViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UIState>(UIState.None)
    val state: StateFlow<UIState> = _state.asStateFlow()

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val result = userRepository.getCurrentUser()

                checkNotNull(result) { "User not found" }

                _state.value = UIState.Success(result)
            } catch (e: Exception) {
                _state.value = UIState.Error(e.message ?: "An error occurred")
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
                    firestore = application.firestore,
                    authentication = application.authentication
                )
                return SharedViewModel(authRepository, userRepository) as T
            }
        }
    }
}

sealed class UIState {
    data object None : UIState()
    data class Success(val user: User) : UIState()
    data class Error(val message: String) : UIState()
}