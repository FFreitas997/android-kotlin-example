package com.example.happyplaces

import android.app.Application
import com.example.happyplaces.database.HappyPlaceDatabase

class HappyPlaceApplication: Application() {

    val db by lazy { HappyPlaceDatabase.getDatabase(this) }

}