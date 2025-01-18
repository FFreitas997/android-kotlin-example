package com.ffreitas.flowify.di

import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.data.network.auth.DefaultFirebaseAuthService
import com.ffreitas.flowify.data.network.auth.FirebaseAuthService
import com.ffreitas.flowify.data.network.firestore.BoardFirestoreService
import com.ffreitas.flowify.data.network.firestore.FirestoreService
import com.ffreitas.flowify.data.network.firestore.UserFirestoreService
import com.ffreitas.flowify.data.network.storage.DefaultStorageService
import com.ffreitas.flowify.data.network.storage.StorageService
import com.ffreitas.flowify.data.repository.auth.AuthRepository
import com.ffreitas.flowify.data.repository.auth.DefaultAuthRepository
import com.ffreitas.flowify.data.repository.board.BoardRepository
import com.ffreitas.flowify.data.repository.board.DefaultBoardRepository
import com.ffreitas.flowify.data.repository.storage.DefaultStorageRepository
import com.ffreitas.flowify.data.repository.storage.StorageRepository
import com.ffreitas.flowify.data.repository.user.DefaultUserRepository
import com.ffreitas.flowify.data.repository.user.UserRepository
import com.ffreitas.flowify.utils.Constants.CLOUD_STORAGE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance(CLOUD_STORAGE_URL)
    }

    @Provides
    @Singleton
    fun provideBoardFirestoreService(instance: FirebaseFirestore): FirestoreService<Board> {
        return BoardFirestoreService(instance)
    }

    @Provides
    @Singleton
    fun provideUserFirestoreService(instance: FirebaseFirestore): FirestoreService<User> {
        return UserFirestoreService(instance)
    }

    @Provides
    @Singleton
    fun provideAuthenticationService(instance: FirebaseAuth): FirebaseAuthService {
        return DefaultFirebaseAuthService(instance)
    }

    @Provides
    @Singleton
    fun provideStorageService(instance: FirebaseStorage): StorageService {
        return DefaultStorageService(instance)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(service: StorageService): StorageRepository {
        return DefaultStorageRepository(service)
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(service: FirebaseAuthService): AuthRepository {
        return DefaultAuthRepository(service)
    }

    @Provides
    @Singleton
    fun provideBoardRepository(service: FirestoreService<Board>): BoardRepository {
        return DefaultBoardRepository(service)
    }

    @Provides
    @Singleton
    fun provideUserRepository(service: FirestoreService<User>): UserRepository {
        return DefaultUserRepository(service)
    }
}