package com.example.healthreminderapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.healthreminderapp.WorkoutEntry


class WorkoutFormViewModel(application: Application) : AndroidViewModel(application) {

    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()

    fun saveWorkout(entry: WorkoutEntry) {
        viewModelScope.launch {
            workoutDao.insertWorkout(entry)
        }
    }

    // ✅ Now marked as suspend — can be called from LaunchedEffect
    suspend fun getWorkoutsInLastDays(context: Context, days: Int): List<WorkoutEntry> {
        val all = AppDatabase.getDatabase(context).workoutDao().getAllWorkouts()
        val cutoff = System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L
        return all.filter { it.timestamp >= cutoff }
    }
}
