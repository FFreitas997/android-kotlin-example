package com.example.wishlist.repository

import com.example.wishlist.data.Wish
import com.example.wishlist.database.WishDao
import kotlinx.coroutines.flow.Flow

class WishRepository(private val wishDao: WishDao) {

    suspend fun createWish(entity: Wish) = wishDao.createWish(entity)

    suspend fun updateWish(entity: Wish) = wishDao.updateWish(entity)

    suspend fun deleteWish(entity: Wish) = wishDao.deleteWish(entity)

    fun getAllWishes(): Flow<List<Wish>> = wishDao.getAllWishes()

    fun getWishById(id: Long): Flow<Wish> = wishDao.getWishById(id)

}