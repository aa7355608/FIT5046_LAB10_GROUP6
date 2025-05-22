package com.example.healthreminderapp

import androidx.room.*

@Dao
interface ReminderSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: ReminderSettings)

    @Query("SELECT * FROM reminder_settings ORDER BY id DESC LIMIT 1")
    suspend fun getLatest(): ReminderSettings?
}
