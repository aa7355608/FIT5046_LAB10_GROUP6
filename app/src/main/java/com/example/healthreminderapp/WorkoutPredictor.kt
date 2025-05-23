package com.example.healthreminderapp

class WorkoutPredictor {
    fun predict(weeklyHours: Float): List<Float> {
        // Return prediction result based on input weekly exercise hours
        return when {
            weeklyHours < 5 -> {
                // Low intensity → biased toward cardio training
                listOf(0.7f, 0.2f, 0.1f)
            }
            weeklyHours < 10 -> {
                // Medium intensity → biased toward balanced training
                listOf(0.2f, 0.6f, 0.2f)
            }
            else -> {
                // High intensity → biased toward strength training
                listOf(0.1f, 0.2f, 0.7f)
            }
        }
    }
}
