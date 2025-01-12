package com.ffreitas.flowify.data.network.firestore

import com.google.firebase.firestore.CollectionReference

/**
 * Firestore Service interface contains all CRUD operations for Firestore
 */

interface FirestoreService<S> {

    val collection: CollectionReference

    suspend fun create(documentID: String, model: S)

    suspend fun read(documentID: String): S

    suspend fun readWhereEquals(field: String, value: String): List<S>

    suspend fun update(documentID: String, fields: Map<String, Any>)

    suspend fun delete(documentID: String)
}