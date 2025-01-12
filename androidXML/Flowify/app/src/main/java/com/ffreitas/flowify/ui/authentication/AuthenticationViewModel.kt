package com.ffreitas.flowify.ui.authentication

import androidx.lifecycle.ViewModel
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun hasCurrentUser() = authRepository.getCurrentUser()

}