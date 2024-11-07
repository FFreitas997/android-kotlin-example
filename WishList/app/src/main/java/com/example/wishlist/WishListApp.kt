package com.example.wishlist

import android.app.Application

class WishListApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Graph.initDatabase(this)
    }
}