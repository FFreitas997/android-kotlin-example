package com.ffreitas.flowify.ui.signup

import android.util.Log
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
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var name = ""
    private var email = ""
    private var password = ""

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.None)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun onNameChanged(name: CharSequence?) {
        name?.let { this.name = it.toString().trim() }
    }

    fun onEmailChanged(email: CharSequence?) {
        email?.let { this.email = it.toString().trim() }
    }

    fun onPasswordChanged(password: CharSequence?) {
        password?.let { this.password = it.toString() }
    }

    fun nameIsValid() = name.isNotEmpty()

    fun emailIsValid() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun passwordIsValid() = password.length >= PASSWORD_MIN_LENGTH

    fun signUp() {
        viewModelScope.launch {
            try {
                _uiState.value = UIState.Loading

                val result = authRepository.signUp(name, email, password)

                checkNotNull(result) { "Failed to sign up user with email $email" }

                val user = User(
                    id = result.uid,
                    name = result.displayName ?: "",
                    email = result.email ?: ""
                )

                if (!userRepository.createUser(user))
                    throw Exception("Failed to store user with id ${user.id}")

                _uiState.value = UIState.Success(user)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sign up user with email $email", e)
                _uiState.value = UIState.Error(e.message ?: "Failed to sign up")
            }
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val authRepository = DefaultAuthRepository(application.authentication)
                val userRepository = DefaultUserRepository(application.firestore)

                return SignUpViewModel(authRepository, userRepository) as T
            }
        }
    }
}

sealed class UIState {
    data object None : UIState()
    data object Loading : UIState()
    data class Success(val user: User) : UIState()
    data class Error(val message: String) : UIState()
}