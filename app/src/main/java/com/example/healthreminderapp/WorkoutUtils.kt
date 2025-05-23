package com.example.healthreminderapp

object WorkoutUtils {
    fun durationTextToMinutes(duration: String): Int {
        val parts = duration.split(" ")
        var totalMinutes = 0
        
        for (i in 0 until parts.size - 1) {
            val value = parts[i].toIntOrNull() ?: continue
            when (parts[i + 1].lowercase()) {
                "hour", "hours", "hr", "hrs" -> totalMinutes += value * 60
                "minute", "minutes", "min", "mins" -> totalMinutes += value
            }
        }
        
        return totalMinutes
    }
} 