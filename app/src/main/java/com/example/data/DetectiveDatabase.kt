package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CompletedCase::class, GridCellState::class, CaseNotes::class],
    version = 1,
    exportSchema = false
)
abstract class DetectiveDatabase : RoomDatabase() {
    abstract fun detectiveDao(): DetectiveDao

    companion object {
        @Volatile
        private var INSTANCE: DetectiveDatabase? = null

        fun getDatabase(context: Context): DetectiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DetectiveDatabase::class.java,
                    "detective_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
