package com.ffreitas.flowify.ui.main.components.account

import android.content.Context
import android.net.Uri
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AccountViewModel(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state: MutableLiveData<AccountUIState> = MutableLiveData()
    val state: LiveData<AccountUIState> = _state

    private var currentUser: User? = null

    private var name: String = ""
    private var mobile: String = ""
    private var profilePictureSelected: File? = null
    private var profilePictureSelectedURI: String = ""

    fun handleChangeName(editable: Editable?) {
        editable?.let { name -> this.name = name.toString() }
    }

    fun handleChangePhone(editable: Editable?) {
        editable?.let { mobile -> this.mobile = mobile.toString() }
    }

    fun handlePictureSelection(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.postValue(AccountUIState.Loading)
                val filename = "${Constants.PROFILE_FILE_PREFIX}_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, filename)
                context
                    .contentResolver
                    .openInputStream(uri)
                    .use { input ->
                        FileOutputStream(file)
                            .use { output -> input?.copyTo(output) }
                    }
                profilePictureSelected = file
                profilePictureSelectedURI = uri.toString()
                _state.postValue(AccountUIState.None)
            } catch (e: Exception) {
                _state.postValue(AccountError.FileError(e.message ?: "Failed to select image"))
            }
        }
    }

    fun isNameValid() = name.isNotEmpty()

    fun isPhoneValid() = mobile.length == PHONE_LENGTH

    fun setCurrentUser(user: User?, notifyUI: (User) -> Unit) {
        user ?: throw IllegalArgumentException("User must not be null")
        currentUser = user.copy()
        name = name.ifEmpty { user.name }
        mobile = mobile.ifEmpty { user.mobile.toString() }
        notifyUI(
            currentUser!!.copy(
                name = name,
                mobile = mobile.toLong(),
                picture = profilePictureSelectedURI.ifEmpty { user.picture }
            )
        )
    }

    private suspend fun handleSelectedImage(currentFile: File): String {
        val newURI = storageRepository.uploadProfilePicture(currentFile)
        if (currentUser!!.picture.isNotEmpty())
            storageRepository.deleteFile(Uri.parse(currentUser!!.picture))
        return newURI.toString()
    }

    fun updateUserInformation() {
        viewModelScope.launch {
            try {
                _state.postValue(AccountUIState.Loading)
                checkNotNull(currentUser) { "User must be set" }
                if (!isNameValid() || !isPhoneValid())
                    throw Exception("Invalid name or phone number")
                if (currentUser!!.id.isEmpty())
                    throw Exception("User has not been initialized yet (ID is empty)")

                profilePictureSelected
                    ?.let { currentUser!!.picture = handleSelectedImage(it) }

                currentUser!!.name = name
                currentUser!!.mobile = mobile.toLong()

                userRepository.updateUser(currentUser!!)

                _state.postValue(AccountUIState.Success)
            } catch (e: Exception) {
                _state.postValue(AccountError.SubmitError(e.message ?: "Failed to submit user information"))
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
    data object None : AccountUIState
    data object Loading : AccountUIState
    data object Success : AccountUIState
    data class Error(val message: String) : AccountError
}

sealed interface AccountError: AccountUIState {
    data class SubmitError(val message: String) : AccountError
    data class FileError(val message: String) : AccountError
}