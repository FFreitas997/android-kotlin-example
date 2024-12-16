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
import com.ffreitas.flowify.data.repository.AuthRepository
import com.ffreitas.flowify.data.repository.DefaultAuthRepository
import kotlinx.coroutines.launch


class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {

    private var name = ""
    private var email = ""
    private var password = ""

    private val _hasSignSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val hasSignSuccess: LiveData<Boolean> = _hasSignSuccess

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
                val success = repository.signUp(name, email, password)
                _hasSignSuccess.postValue(success)
            } catch (e: Exception) {
                Log.e(TAG, "Error signing up", e)
                _hasSignSuccess.postValue(false)
            }
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

                val application = checkNotNull(extras[APPLICATION_KEY])

                val repository =
                    DefaultAuthRepository((application as FlowifyApplication).firebase)

                return SignUpViewModel(repository) as T
            }
        }
    }
}