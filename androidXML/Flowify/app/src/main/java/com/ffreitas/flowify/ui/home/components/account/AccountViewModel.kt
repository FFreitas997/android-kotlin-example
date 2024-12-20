package com.ffreitas.flowify.ui.home.components.account

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.ffreitas.flowify.FlowifyApplication
import com.ffreitas.flowify.data.repository.storage.DefaultStorageRepository
import com.ffreitas.flowify.data.repository.storage.StorageRepository
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.repository.user.DefaultUserRepository
import com.ffreitas.flowify.data.repository.user.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class AccountViewModel(
    private val userRepository: UserRepository,
    private val storage: StorageRepository
) : ViewModel() {

    private var currentUser: User = User()
    private val _accountUpdated = MutableLiveData<Boolean>()
    val accountUpdated: LiveData<Boolean> = _accountUpdated

    private var name: String = ""
    private var phone: String = ""
    private var picture: String = ""

    private var currentImageFile: File? = null

    fun handleChangeName(text: CharSequence?) {
        name = (text ?: return).toString().trim()
    }

    fun handleChangePhone(text: CharSequence?) {
        phone = (text ?: return).toString().trim()
    }

    fun isNameValid(): Boolean {
        return name.isNotEmpty()
    }

    fun isPhoneValid(): Boolean {
        return phone.isNotEmpty() && phone.length == 9
    }

    fun updateUserInformation() {
        viewModelScope.launch {
            try {
                if (currentUser.id.isEmpty())
                    throw Exception("User has not been initialized yet (ID is empty)")
                currentUser.name = name
                currentUser.mobile = phone.toLong()
                if (currentImageFile != null) {
                    val newURI = storage.uploadFile(currentImageFile!!)
                    if (newURI != null && currentUser.picture.isNotEmpty()) {
                        val hasSuccessDelete = storage.deleteFile(Uri.parse(currentUser.picture))
                        if (!hasSuccessDelete)
                            Log.e(TAG, "Failed to delete old profile picture")
                    }
                    currentUser.picture = (newURI ?: "").toString()
                }
                val hasSuccess = userRepository.updateUser(currentUser)
                if (!hasSuccess)
                    throw Exception("Failed to update user information")
                _accountUpdated.postValue(true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update user information", e)
                _accountUpdated.postValue(false)
            }
        }
    }

    fun setCurrentUser(user: User) {
        currentUser = user
        name = user.name
        phone = user.mobile.toString()
        picture = user.picture
    }

    fun handlePictureSelection(file: File) {
        picture = Uri.fromFile(file).toString()
        currentImageFile = file
    }

    companion object {
        private const val TAG = "AccountViewModel"

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