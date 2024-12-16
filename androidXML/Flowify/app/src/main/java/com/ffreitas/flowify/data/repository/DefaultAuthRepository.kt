package com.ffreitas.flowify.data.repository

import android.util.Log
import com.ffreitas.flowify.data.network.FirebaseService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "DefaultAuthRepository"

class DefaultAuthRepository(
    private val firebase: FirebaseService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String): Boolean =
        withContext(dispatcher) {
            try {
                firebase.signUp(name, email, password)
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error Sign Up ${e.message}")
                false
            }
        }

    override suspend fun signIn(email: String, password: String): Boolean =
        withContext(dispatcher) {
            try {
                firebase.signIn(email, password)
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error Sign In ${e.message}")
                false
            }
        }
}