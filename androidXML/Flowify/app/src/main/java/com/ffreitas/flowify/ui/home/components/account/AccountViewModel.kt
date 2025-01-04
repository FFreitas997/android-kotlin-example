package com.ffreitas.flowify.ui.home.components.account

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.repository.storage.DefaultStorageRepository
import com.ffreitas.flowify.data.repository.storage.StorageRepository
import com.ffreitas.flowify.data.repository.user.DefaultUserRepository
import com.ffreitas.flowify.data.repository.user.UserRepository
import com.ffreitas.flowify.utils.Constants
import com.ffreitas.flowify.utils.Constants.PHONE_LENGTH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AccountViewModel(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private var currentUser: User = User()

    private val _uiState = MutableStateFlow<UIState>(UIState.None)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private var name: String = ""
    private var phone: String = ""
    private var picture: String = ""

    private var currentImageFile: File? = null

    fun handleChangeName(name: CharSequence?) {
        name?.let { this.name = it.toString().trim() }
    }

    fun handleChangePhone(phone: CharSequence?) {
        phone?.let { this.phone = it.toString().trim() }
    }

    fun handlePictureSelection(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val filename = "${Constants.PROFILE_FILE_PREFIX}_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, filename)
                context
                    .contentResolver
                    .openInputStream(uri)
                    .use { input ->
                        FileOutputStream(file)
                            .use { output -> input?.copyTo(output) }
                    }
                currentImageFile = file
            } catch (e: Exception) {
                _uiState.value = UIState.FileError(e.message ?: "Failed to load image")
            }
        }
    }

    fun isNameValid() = name.isNotEmpty()

    fun isPhoneValid() = phone.isNotEmpty() && phone.length == PHONE_LENGTH

    fun setCurrentUser(user: User) {
        currentUser = user
        name = user.name
        phone = user.mobile.toString()
        picture = user.picture
    }

    private suspend fun handleSelectedImage(currentFile: File) {
        val newURI = storageRepository.uploadFile(currentFile)
        checkNotNull(newURI) { "Failed to upload new profile picture" }
        if (currentUser.picture.isNotEmpty())
            storageRepository.deleteFile(Uri.parse(currentUser.picture))
        currentUser.picture = newURI.toString()
    }

    fun updateUserInformation() {
        viewModelScope.launch {
            try {
                _uiState.value = UIState.Loading
                if (!isNameValid() || !isPhoneValid())
                    throw Exception("Invalid name or phone number")
                if (currentUser.id.isEmpty())
                    throw Exception("User has not been initialized yet (ID is empty)")
                currentUser.name = name
                currentUser.mobile = phone.toLong()
                currentImageFile?.let { file -> handleSelectedImage(file) }
                if (!userRepository.updateUser(currentUser))
                    throw Exception("Failed to update user information")
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "An error occurred")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val storeRepository = DefaultUserRepository(application.firestore)
                val storage = DefaultStorageRepository(application.storage)

                return AccountViewModel(storeRepository, storage) as T
            }
        }
    }
}

sealed class UIState {
    data object Loading : UIState()
    data object None : UIState()
    data object Success : UIState()
    data class Error(val message: String) : UIState()
    data class FileError(val message: String) : UIState()
}