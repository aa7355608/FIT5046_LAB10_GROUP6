package com.example.healthreminderapp


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(entry: WorkoutEntry)

    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    suspend fun getAllWorkouts(): List<WorkoutEntry>
}
