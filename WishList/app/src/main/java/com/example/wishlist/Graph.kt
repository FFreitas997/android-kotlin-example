package com.example.wishlist

import android.content.Context
import androidx.room.Room
import com.example.wishlist.database.WishDatabase
import com.example.wishlist.repository.WishRepository

object Graph {

    lateinit var database: WishDatabase

    fun initDatabase(context: Context){
        database = Room
            .databaseBuilder(context, WishDatabase::class.java, "wishlist.db")
            .build()
    }

    val repository by lazy { WishRepository(wishDao = database.wishDao()) }
}