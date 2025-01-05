package com.ffreitas.flowify.data.network.storage

import android.net.Uri
import java.io.File

interface StorageService {

    suspend fun uploadFile(file: File, type: ResourceType): Uri?

    suspend fun deleteFile(uri: Uri): Boolean
}