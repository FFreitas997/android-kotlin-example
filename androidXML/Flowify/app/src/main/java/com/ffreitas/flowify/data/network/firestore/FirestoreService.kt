package com.ffreitas.flowify.data.network.firestore

/**
 * Firestore Service interface contains all CRUD operations for Firestore
 */

interface FirestoreService<S> {

    suspend fun create(documentID: String, model: S)

    suspend fun read(documentID: String): S

    suspend fun update(documentID: String, fields: Map<String, Any>)

    suspend fun delete(documentID: String)
}