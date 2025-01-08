package com.ffreitas.flowify.data.repository.storage

import android.net.Uri
import java.io.File

interface StorageRepository {

    suspend fun uploadProfilePicture(file: File): Uri

    suspend fun uploadBoardPicture(file: File): Uri

    suspend fun deleteFile(uri: Uri)
}