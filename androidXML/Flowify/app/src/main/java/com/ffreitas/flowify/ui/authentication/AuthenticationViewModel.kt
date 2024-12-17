package com.ffreitas.flowify.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.auth.DefaultAuthRepository

class AuthenticationViewModel(private val repository: AuthRepository) : ViewModel() {

    fun hasCurrentUser() = repository.getCurrentUser()

    companion object {
        private const val TAG = "AuthenticationViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

                val application = checkNotNull(extras[APPLICATION_KEY])

                val repository = DefaultAuthRepository((application as FlowifyApplication).firebase)

                return AuthenticationViewModel(repository) as T
            }
        }
    }
}