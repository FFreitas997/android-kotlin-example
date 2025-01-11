package com.ffreitas.flowify.ui.authentication.components.signin

import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.auth.DefaultAuthRepository
import com.ffreitas.flowify.utils.Constants.PASSWORD_MIN_LENGTH
import kotlinx.coroutines.launch

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state: MutableLiveData<SignInUIState<String>> = MutableLiveData()
    val state: LiveData<SignInUIState<String>> = _state

    private var email = ""
    private var password = ""

    fun onEmailChanged(email: Editable?) {
        email?.let { this.email = it.toString() }
    }

    fun onPasswordChanged(password: Editable?) {
        password?.let { this.password = it.toString() }
    }

    fun isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isPasswordValid() = password.length >= PASSWORD_MIN_LENGTH

    fun signIn() {
        viewModelScope.launch {
            try {
                _state.postValue(SignInUIState.Loading)

                val result = authRepository.signIn(email, password)

                checkNotNull(result) { "Failed to sign in user with email $email" }

                _state.postValue(SignInUIState.Success(result.uid))
            } catch (e: Exception) {
                _state.postValue(SignInUIState.Error(e.message ?: "An error occurred"))
            }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val authRepository = DefaultAuthRepository(application.authentication)

                return SignInViewModel(authRepository) as T
            }
        }
    }
}

sealed interface SignInUIState<out S> {
    data object Loading : SignInUIState<Nothing>
    data class Success<S>(val data: S) : SignInUIState<S>
    data class Error(val message: String) : SignInUIState<Nothing>
}