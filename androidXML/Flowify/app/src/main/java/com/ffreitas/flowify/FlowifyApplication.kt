package com.ffreitas.flowify

import android.app.Application
import com.ffreitas.flowify.data.network.auth.DefaultFirebaseAuthService
import com.ffreitas.flowify.data.network.firestore.DefaultFirestoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FlowifyApplication : Application() {

    val authFirebase by lazy { DefaultFirebaseAuthService(FirebaseAuth.getInstance()) }
    val firestore by lazy { DefaultFirestoreService(FirebaseFirestore.getInstance()) }


}