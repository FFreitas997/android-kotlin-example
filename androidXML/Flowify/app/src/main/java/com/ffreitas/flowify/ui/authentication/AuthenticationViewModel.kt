package com.ffreitas.flowify.ui.authentication

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

class AuthenticationViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _hasUser = MutableLiveData<User?>()
    val hasUser: LiveData<User?> = _hasUser

    fun hasCurrentUser() = authRepository.getCurrentUser()

    fun getUser(email: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(email)
                checkNotNull(user) { "Failed to get user" }
                _hasUser.postValue(user)
            }catch (e: Exception) {
                Log.d(TAG, "Error getting user", e)
                _hasUser.postValue(null)
            }
        }
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication

                val authRepository = DefaultAuthRepository(application.authFirebase)
                val userRepository = DefaultUserRepository(application.firestore)

                return AuthenticationViewModel(authRepository, userRepository) as T
            }
        }
    }
}