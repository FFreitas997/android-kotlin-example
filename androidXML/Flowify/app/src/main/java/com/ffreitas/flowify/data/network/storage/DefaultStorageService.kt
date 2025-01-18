package com.ffreitas.flowify.data.network.storage

import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DefaultStorageService(
    private val service: FirebaseStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StorageService {

    private val reference = service.reference

    override suspend fun upload(resource: File, resourcePath: String): Uri =
        withContext(ioDispatcher) {
            val childReference = reference.child("$resourcePath/${resource.name}")

            childReference
                .putFile(
                    Uri.fromFile(resource),
                    storageMetadata { contentType = getContentType(resource) })
                .await()

            childReference.downloadUrl.await()
        }


    override suspend fun delete(uri: Uri): Unit =
        withContext(ioDispatcher) {
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