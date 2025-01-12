package com.ffreitas.flowify.ui.authentication.components.signup

import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.user.UserRepository
import com.ffreitas.flowify.utils.Constants.PASSWORD_MIN_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state: MutableLiveData<SignUpUIState<String>> = MutableLiveData()
    val state: LiveData<SignUpUIState<String>> = _state

    private var name = ""
    private var email = ""
    private var password = ""

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
                _state.postValue(SignUpUIState.Loading)

                val result = authRepository.signUp(name, email, password)

                checkNotNull(result) { "Failed to sign up user with email $email" }

                val user = User(
                    id = result.uid,
                    name = result.displayName ?: "",
                    email = result.email ?: ""
                )

                userRepository.createUser(user)

                _state.postValue(SignUpUIState.Success(user.id))
            } catch (e: Exception) {
                _state.postValue(SignUpUIState.Error(e.message ?: "An error occurred"))
            }
        }
    }
}

sealed interface SignUpUIState<out S> {
    data object Loading : SignUpUIState<Nothing>
    data class Success<S>(val data: S) : SignUpUIState<S>
    data class Error(val message: String) : SignUpUIState<Nothing>
}