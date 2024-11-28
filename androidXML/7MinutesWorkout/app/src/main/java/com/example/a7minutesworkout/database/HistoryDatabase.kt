package com.example.a7minutesworkout.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.a7minutesworkout.constants.Constants.DATABASE_NAME


@Database(entities = [HistoryEntity::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object Factory {

        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getDatabase(context: Context): HistoryDatabase {
            if (INSTANCE != null)
                return INSTANCE!!

            synchronized(this) {
                val instance = androidx.room.Room
                    .databaseBuilder(
                        context = context.applicationContext,
                        klass = HistoryDatabase::class.java,
                        name = DATABASE_NAME
                    )
                    .build()

                INSTANCE = instance

                return instance
            }

        }
    }
}