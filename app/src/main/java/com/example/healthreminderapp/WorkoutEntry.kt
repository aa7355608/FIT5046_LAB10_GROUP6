package com.example.healthreminderapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutType: String,
    val duration: String,
    val month: Int,
    val day: Int,
    val timestamp: Long = System.currentTimeMillis()
)
