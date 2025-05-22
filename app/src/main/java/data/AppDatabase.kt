package com.example.healthreminderapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReminderSettings::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderSettingsDao(): ReminderSettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reminder_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}