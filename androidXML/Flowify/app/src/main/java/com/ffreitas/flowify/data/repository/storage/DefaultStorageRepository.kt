package com.ffreitas.flowify.data.repository.storage

import android.net.Uri
import com.ffreitas.flowify.data.network.storage.StorageService
import com.ffreitas.flowify.utils.Constants.BOARD_PICTURE_PATH
import com.ffreitas.flowify.utils.Constants.PROFILE_PICTURE_PATH
import java.io.File

class DefaultStorageRepository(private val service: StorageService) : StorageRepository {

    override suspend fun uploadProfilePicture(file: File): Uri {
        return service.upload(file, PROFILE_PICTURE_PATH)
    }

    override suspend fun uploadBoardPicture(file: File): Uri {
        return service.upload(file, BOARD_PICTURE_PATH)
    }

    override suspend fun deleteFile(uri: Uri) {
        service.delete(uri)
    }

}