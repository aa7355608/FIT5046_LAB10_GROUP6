package com.example.healthreminderapp

class WorkoutRecommender {
    data class Recommendation(
        val cardio: Float,
        val balanced: Float,
        val strength: Float
    )

    fun getRecommendation(weeklyHours: Float): Recommendation {
        return when {
            weeklyHours < 5 -> Recommendation(0.8f, 0.1f, 0.1f)  // 低强度 -> 主要推荐有氧
            weeklyHours < 10 -> Recommendation(0.1f, 0.8f, 0.1f)  // 中强度 -> 主要推荐平衡训练
            else -> Recommendation(0.1f, 0.1f, 0.8f)  // 高强度 -> 主要推荐力量训练
        }
    }

    fun getTopRecommendation(weeklyHours: Float): String {
        val rec = getRecommendation(weeklyHours)
        return when {
            rec.cardio > rec.balanced && rec.cardio > rec.strength -> "Cardio Training"
            rec.balanced > rec.cardio && rec.balanced > rec.strength -> "Balanced Training"
            else -> "Strength Training"
        }
    }
} 