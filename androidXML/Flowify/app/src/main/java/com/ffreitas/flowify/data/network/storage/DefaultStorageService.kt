package com.ffreitas.flowify.data.network.storage

import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.tasks.await
import java.io.File

class DefaultStorageService(private val service: FirebaseStorage) : StorageService {

    private val reference = service.reference

    override suspend fun upload(resource: File, resourcePath: String): Uri {
        val childReference = reference.child("$resourcePath/${resource.name}")

        childReference
            .putFile(Uri.fromFile(resource), storageMetadata { contentType = getContentType(resource) })
            .await()

        return childReference.downloadUrl.await()
    }

    override suspend fun delete(uri: Uri) {
        service
            .getReferenceFromUrl(uri.toString())
            .delete()
            .await()
    }

    private fun getContentType(file: File): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
    }
}