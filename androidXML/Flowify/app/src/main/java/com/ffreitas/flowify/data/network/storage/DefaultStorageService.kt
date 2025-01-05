package com.ffreitas.flowify.data.network.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

class DefaultStorageService(private val service: FirebaseStorage) : StorageService {

    private val reference = service.reference

    override suspend fun uploadFile(file: File, type: ResourceType): Uri? {
        val resourcePath = when (type) {
            ResourceType.PROFILE_IMAGE -> "profile_images"
        }

        val fileReference = reference.child("resources/$resourcePath/${file.name}")
        val stream = FileInputStream(file)

        val resourceContentType = when (type) {
            ResourceType.PROFILE_IMAGE -> "image/jpg"
        }
        fileReference
            .putStream(stream, storageMetadata { contentType = resourceContentType })
            .await()

        return fileReference.downloadUrl.await()
    }

    override suspend fun deleteFile(uri: Uri): Boolean {
        service
            .getReferenceFromUrl(uri.toString())
            .delete()
            .await()
        return true
    }
}

enum class ResourceType {
    PROFILE_IMAGE
}