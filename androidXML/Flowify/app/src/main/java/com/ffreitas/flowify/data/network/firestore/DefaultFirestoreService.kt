package com.ffreitas.flowify.data.network.firestore

import android.util.Log
import com.ffreitas.flowify.data.network.models.User
import com.ffreitas.flowify.utils.Constants.USER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class DefaultFirestoreService(private val service: FirebaseFirestore) : FirestoreService {

    override suspend fun storeUser(user: User): Boolean {
        try {
            require(user.id.isNotEmpty()) { "User id cannot be empty" }
            service
                .collection(USER_COLLECTION)
                .document(user.id)
                .set(user, SetOptions.merge())
                .await()
            Log.d(TAG, "User with ID ${user.id} stored successfully")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store user with ID ${user.id}: ${e.message}", e)
            return false
        }
    }

    override suspend fun getUser(email: String): User? {
        try {
            val snapshot = service
                .collection(USER_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .await()
            if (snapshot.isEmpty) {
                Log.d(TAG, "User with email $email not found")
                return null
            }
            val user = snapshot.documents[0].toObject(User::class.java)
            Log.d(TAG, "User with email $email found")
            return user
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user with email $email: ${e.message}", e)
            return null
        }
    }

    companion object {
        private const val TAG = "DefaultFirestoreService"
    }
}