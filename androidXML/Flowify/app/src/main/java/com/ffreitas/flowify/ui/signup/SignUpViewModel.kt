package com.ffreitas.flowify.ui.signup

import android.util.Log
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


class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val storeRepository: UserRepository
) : ViewModel() {

    private var name = ""
    private var email = ""
    private var password = ""

    private val _hasSignSuccess: MutableLiveData<User?> = MutableLiveData()
    val hasSignSuccess: LiveData<User?> = _hasSignSuccess

    fun onNameChanged(name: String) {
        this.name = name
    }

    fun onEmailChanged(email: String) {
        this.email = email
    }

    fun onPasswordChanged(password: String) {
        this.password = password
    }

    fun nameIsValid(): Boolean {
        return name.isNotEmpty()
    }

    fun emailIsValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordIsValid(): Boolean {
        return password.length >= 6
    }

    fun onSignUp() {
        viewModelScope.launch {
            try {
                val result = authRepository.signUp(name, email, password)
                    ?: throw Exception("Failed to sign up")

                val user = User(
                    id = result.uid,
                    name = result.displayName ?: "",
                    email = result.email ?: ""
                )

                val storeSuccess = storeRepository.storeUser(user)
                if (!storeSuccess)
                    throw Exception("Failed to store user")

                _hasSignSuccess.postValue(user)
            } catch (e: Exception) {
                Log.e(TAG, "Error signing up", e)
                _hasSignSuccess.postValue(null)
            }
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val authRepository = DefaultAuthRepository(application.authFirebase)
                val storeRepository = DefaultUserRepository(application.firestore)

                return SignUpViewModel(authRepository, storeRepository) as T
            }
        }
    }
}