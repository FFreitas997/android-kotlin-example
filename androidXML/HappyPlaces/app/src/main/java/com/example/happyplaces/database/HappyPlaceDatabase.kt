package com.example.happyplaces.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.happyplaces.utils.Constants.DATABASE_NAME

@Database(entities = [HappyPlaceEntity::class], version = 1)
abstract class HappyPlaceDatabase: RoomDatabase() {

    abstract fun happyPlaceDao(): HappyPlaceDao

    companion object {

        @Volatile
        private var instance: HappyPlaceDatabase? = null

        fun getDatabase(context: Context): HappyPlaceDatabase {
            return instance ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        HappyPlaceDatabase::class.java, DATABASE_NAME
                    ).build()
                this.instance = instance
                instance
            }
        }
    }
}