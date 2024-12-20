package com.ffreitas.flowify.ui.home

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

class HomeActivityViewModel(
    private val repository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init { getCurrentUser() }

    fun signOut() = repository.signOut()

    private fun getCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            if (user == null) {
                Log.e(TAG, "Current user not found")
                return@launch
            }
            Log.d(TAG, "Current User found: ${user.email}")
            _user.postValue(user)
        }
    }

    companion object {
        private const val TAG = "HomeActivityViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val authRepository = DefaultAuthRepository(application.authentication)
                val storeRepository = DefaultUserRepository(
                    firestore = application.firestore,
                    authentication = application.authentication
                )
                return HomeActivityViewModel(authRepository, storeRepository) as T
            }
        }
    }
}