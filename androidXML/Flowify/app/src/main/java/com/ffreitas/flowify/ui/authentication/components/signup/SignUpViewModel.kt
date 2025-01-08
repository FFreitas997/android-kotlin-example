package com.ffreitas.flowify.ui.authentication.components.signup

import android.text.Editable
import android.util.Patterns
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
import com.ffreitas.flowify.utils.Constants.PASSWORD_MIN_LENGTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var name = ""
    private var email = ""
    private var password = ""

    private val _state: MutableStateFlow<SignUpUIState<String>?> = MutableStateFlow(null)
    val state: StateFlow<SignUpUIState<String>?> = _state.asStateFlow()

    fun onNameChanged(name: Editable?) {
        name?.let { this.name = it.toString() }
    }

    fun onEmailChanged(email: Editable?) {
        email?.let { this.email = it.toString() }
    }

    fun onPasswordChanged(password: Editable?) {
        password?.let { this.password = it.toString() }
    }

    fun isNameValid() = name.isNotEmpty()

    fun isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isPasswordValid() = password.length >= PASSWORD_MIN_LENGTH

    fun signUp() {
        viewModelScope.launch {
            try {
                _state.update { SignUpUIState.Loading }

                val result = authRepository.signUp(name, email, password)

                checkNotNull(result) { "Failed to sign up user with email $email" }

                val user = User(
                    id = result.uid,
                    name = result.displayName ?: "",
                    email = result.email ?: ""
                )

                userRepository.createUser(user)

                _state.update { SignUpUIState.Success(user.id) }
            } catch (e: Exception) {
                _state.update { SignUpUIState.Error(e.message ?: "Failed to sign up user") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val authRepository = DefaultAuthRepository(application.authentication)
                val userRepository = DefaultUserRepository(application.userStorage)

                return SignUpViewModel(authRepository, userRepository) as T
            }
        }
    }
}

sealed interface SignUpUIState<out S> {
    data object Loading : SignUpUIState<Nothing>
    data class Success<S>(val data: S) : SignUpUIState<S>
    data class Error(val message: String) : SignUpUIState<Nothing>
}