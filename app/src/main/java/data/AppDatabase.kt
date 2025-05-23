package com.example.healthreminderapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ReminderSettings::class, WorkoutEntry::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO for reminder settings
    abstract fun reminderSettingsDao(): ReminderSettingsDao

    // DAO for workout entries
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton access to the Room database instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health-db" // Unified local database name
                )
                    .fallbackToDestructiveMigration() // Automatically recreate the database if schema changes
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
