package com.ffreitas.flowify.ui.signin

import android.util.Log
import android.util.Patterns
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

class SignInViewModel(
    private val authRepository: AuthRepository,
    private val storeRepository: UserRepository
) : ViewModel() {

    private var email = ""
    private var password = ""

    private val _hasSignInSuccess: MutableLiveData<User?> = MutableLiveData()
    val hasSignInSuccess: LiveData<User?> = _hasSignInSuccess


    fun onEmailChanged(email: String) {
        this.email = email
    }

    fun onPasswordChanged(password: String) {
        this.password = password
    }

    fun emailIsValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordIsValid(): Boolean {
        return password.length >= 6
    }

    fun signIn() {
        viewModelScope.launch {
            try {
                val firebaseUser = authRepository.signIn(email, password)
                    ?: throw IllegalStateException("Failed to sign in")

                val user = storeRepository.getUser(firebaseUser.email ?: "")
                checkNotNull(user) { "Failed to get user" }

                _hasSignInSuccess.postValue(user)
            } catch (e: Exception) {
                Log.e(TAG, "Error signing in", e)
                _hasSignInSuccess.postValue(null)
            }
        }
    }

    companion object {
        private const val TAG = "SignInViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val authRepository = DefaultAuthRepository(application.authFirebase)
                val storeRepository = DefaultUserRepository(application.firestore)

                return SignInViewModel(authRepository, storeRepository) as T
            }
        }
    }
}