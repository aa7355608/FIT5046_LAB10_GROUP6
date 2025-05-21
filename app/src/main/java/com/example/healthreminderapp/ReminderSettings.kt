package com.example.healthreminderapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_settings")
data class ReminderSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val waterGoal: String,
    val waterInterval: String,
    val exerciseTime: String,
    val delayIfRaining: Boolean
)
