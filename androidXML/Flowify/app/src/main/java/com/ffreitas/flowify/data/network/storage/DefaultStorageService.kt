package com.ffreitas.flowify.data.network.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

class DefaultStorageService(private val service: FirebaseStorage) : StorageService {

    private val reference = service.reference

    override suspend fun uploadFile(file: File): Uri? {
        val fileReference = reference.child("images/${file.name}")
        val stream = FileInputStream(file)

        fileReference
            .putStream(stream, storageMetadata { contentType = "image/jpg" })
            .await()

        return fileReference.downloadUrl.await()
    }

    override suspend fun deleteFile(uri: Uri): Boolean {
        val fileReference = service.getReferenceFromUrl(uri.toString())
        fileReference.delete().await()
        return true
    }
}