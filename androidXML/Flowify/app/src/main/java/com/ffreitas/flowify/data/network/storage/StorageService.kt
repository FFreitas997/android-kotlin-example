package com.ffreitas.flowify.data.network.storage

import android.net.Uri
import java.io.File

interface StorageService {

    suspend fun upload(resource: File, resourcePath: String): Uri

    suspend fun delete(uri: Uri)
}