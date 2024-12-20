package com.ffreitas.flowify.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.auth.DefaultAuthRepository

class AuthenticationViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun hasCurrentUser() = authRepository.getCurrentUser()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication

                val authRepository = DefaultAuthRepository(application.authentication)

                return AuthenticationViewModel(authRepository) as T
            }
        }
    }
}