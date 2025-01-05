package com.ffreitas.flowify.data.repository.storage

import android.net.Uri
import com.ffreitas.flowify.data.network.storage.ResourceType
import com.ffreitas.flowify.data.network.storage.StorageService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DefaultStorageRepository(
    private val service: StorageService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StorageRepository {

    override suspend fun uploadProfilePicture(file: File): Uri? =
        withContext(dispatcher) { service.uploadFile(file, ResourceType.PROFILE_IMAGE) }

    override suspend fun deleteFile(uri: Uri): Boolean =
        withContext(dispatcher) { service.deleteFile(uri) }
}