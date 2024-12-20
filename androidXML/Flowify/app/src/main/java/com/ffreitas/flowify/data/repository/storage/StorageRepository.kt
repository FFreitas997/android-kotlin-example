package com.ffreitas.flowify.data.repository.storage

import android.net.Uri
import java.io.File

interface StorageRepository {

    suspend fun uploadFile(file: File): Uri?

    suspend fun deleteFile(uri: Uri): Boolean
}