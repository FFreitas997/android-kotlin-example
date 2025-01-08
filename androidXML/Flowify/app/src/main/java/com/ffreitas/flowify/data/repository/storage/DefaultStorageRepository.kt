package com.ffreitas.flowify.data.repository.storage

import android.net.Uri
import com.ffreitas.flowify.data.network.storage.StorageService
import com.ffreitas.flowify.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DefaultStorageRepository(
    private val service: StorageService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StorageRepository {

    override suspend fun uploadProfilePicture(file: File): Uri =
        withContext(dispatcher) { service.upload(file, Constants.PROFILE_PICTURE_PATH) }

    override suspend fun uploadBoardPicture(file: File): Uri =
        withContext(dispatcher) { service.upload(file, Constants.BOARD_PICTURE_PATH) }

    override suspend fun deleteFile(uri: Uri) =
        withContext(dispatcher) {service.delete(uri)}

}