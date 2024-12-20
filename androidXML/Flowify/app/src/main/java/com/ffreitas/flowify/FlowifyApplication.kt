package com.ffreitas.flowify

import android.app.Application
import com.ffreitas.flowify.data.network.auth.DefaultFirebaseAuthService
import com.ffreitas.flowify.data.network.firestore.DefaultFirestoreService
import com.ffreitas.flowify.data.network.storage.DefaultStorageService
import com.ffreitas.flowify.utils.Constants
import com.ffreitas.flowify.utils.Constants.CLOUD_STORAGE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FlowifyApplication : Application() {

    val authentication by lazy { DefaultFirebaseAuthService(FirebaseAuth.getInstance()) }
    val firestore by lazy { DefaultFirestoreService(FirebaseFirestore.getInstance()) }
    val storage by lazy { DefaultStorageService(FirebaseStorage.getInstance(CLOUD_STORAGE_URL)) }


}