package com.ffreitas.flowify.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class SignUpViewState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
)

class SignUpViewModel : ViewModel() {

    private val _state = MutableLiveData(SignUpViewState())
    val state: LiveData<SignUpViewState> = _state

    fun onClickSignUp() {}
}