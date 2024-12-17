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
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.auth.DefaultAuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SignInViewModel(private val repository: AuthRepository) : ViewModel() {

    private var email = ""
    private var password = ""

    private val _hasSignInSuccess: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val hasSignInSuccess: LiveData<FirebaseUser?> = _hasSignInSuccess


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
                val user = repository.signIn(email, password)
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

                val application = checkNotNull(extras[APPLICATION_KEY])

                val repository = DefaultAuthRepository((application as FlowifyApplication).firebase)

                return SignInViewModel(repository) as T
            }
        }
    }
}