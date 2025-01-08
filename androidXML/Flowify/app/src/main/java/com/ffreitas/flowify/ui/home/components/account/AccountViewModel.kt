package com.ffreitas.flowify.ui.home.components.account

import android.content.Context
import android.net.Uri
import android.text.Editable
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AccountViewModel(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private var currentUser: User = User()

    private val _state = MutableStateFlow<AccountUIState?>(null)
    val state: StateFlow<AccountUIState?> = _state.asStateFlow()

    private var name: String = ""
    private var phone: String = ""
    private var picture: String = ""

    private var currentImageFile: File? = null

    fun handleChangeName(name: Editable?) {
        name?.let { this.name = it.toString() }
    }

    fun handleChangePhone(phone: Editable?) {
        phone?.let { this.phone = it.toString() }
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
                _state.update { AccountUIState.FileError(e.message ?: "Failed to load image") }
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
        val newURI = storageRepository.uploadProfilePicture(currentFile)
        if (currentUser.picture.isNotEmpty())
            storageRepository.deleteFile(Uri.parse(currentUser.picture))
        currentUser.picture = newURI.toString()
    }

    fun updateUserInformation() {
        viewModelScope.launch {
            try {
                _state.update { AccountUIState.Loading }
                if (!isNameValid() || !isPhoneValid())
                    throw Exception("Invalid name or phone number")
                if (currentUser.id.isEmpty())
                    throw Exception("User has not been initialized yet (ID is empty)")
                currentUser.name = name
                currentUser.mobile = phone.toLong()
                currentImageFile?.let { file -> handleSelectedImage(file) }
                userRepository.updateUser(currentUser)
                _state.update { AccountUIState.Success }
            } catch (e: Exception) {
                _state.update {
                    AccountUIState.Error(
                        e.message ?: "Failed to update user information"
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as FlowifyApplication
                val userRepository = DefaultUserRepository(application.userStorage)
                val storage = DefaultStorageRepository(application.resourceStorage)

                return AccountViewModel(userRepository, storage) as T
            }
        }
    }
}

sealed interface AccountUIState {
    data object Loading : AccountUIState
    data object Success : AccountUIState
    data class Error(val message: String) : AccountUIState
    data class FileError(val message: String) : AccountUIState
}