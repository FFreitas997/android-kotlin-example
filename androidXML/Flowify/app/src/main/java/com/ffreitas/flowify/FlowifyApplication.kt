package com.ffreitas.flowify

import android.app.Application
import com.ffreitas.flowify.data.network.auth.DefaultFirebaseAuthService
import com.ffreitas.flowify.data.network.firestore.BoardFirestoreService
import com.ffreitas.flowify.data.network.firestore.UserFirestoreService
import com.ffreitas.flowify.data.network.storage.DefaultStorageService
import com.ffreitas.flowify.utils.Constants.CLOUD_STORAGE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FlowifyApplication : Application() {

    val authentication by lazy { DefaultFirebaseAuthService(FirebaseAuth.getInstance()) }
    val userStorage by lazy { UserFirestoreService(FirebaseFirestore.getInstance()) }
    val boardStorage by lazy { BoardFirestoreService(FirebaseFirestore.getInstance()) }
    val resourceStorage by lazy { DefaultStorageService(FirebaseStorage.getInstance(CLOUD_STORAGE_URL)) }

}