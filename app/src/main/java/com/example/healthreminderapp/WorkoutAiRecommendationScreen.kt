package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutAiRecommendationScreen(navController: NavController) {
    var weeklyHours by remember { mutableStateOf(3f) }
    var prediction by remember { mutableStateOf(listOf(0f, 0f, 0f)) }
    val predictor = remember { WorkoutPredictor() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("AI Workout Recommendation") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Slider
        Text("Weekly Exercise Time: ${weeklyHours.toInt()} hours")
        Slider(
            value = weeklyHours,
            onValueChange = {
                weeklyHours = it
                // Make prediction
                prediction = predictor.predict(weeklyHours)
            },
            valueRange = 0f..15f,
            steps = 14
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Prediction Distribution
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Recommended Training Type Distribution:")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cardio: ${(prediction[0] * 100).toInt()}%")
                Text("Balanced: ${(prediction[1] * 100).toInt()}%")
                Text("Strength: ${(prediction[2] * 100).toInt()}%")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Recommendation
        val mainRecommendation = when {
            prediction[0] > prediction[1] && prediction[0] > prediction[2] -> "Cardio"
            prediction[1] > prediction[0] && prediction[1] > prediction[2] -> "Balanced"
            else -> "Strength"
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Main Recommendation:")
                Text("Based on your ${weeklyHours.toInt()} hours of weekly exercise,")
                Text("We recommend you focus on $mainRecommendation training.")

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Suggestions
                Text("Detailed Suggestions:")
                when (mainRecommendation) {
                    "Cardio" -> {
                        Text("• Jogging or brisk walking (30–45 minutes)")
                        Text("• Swimming (20–30 minutes)")
                        Text("• Cycling (30–45 minutes)")
                    }
                    "Balanced" -> {
                        Text("• Yoga or Pilates")
                        Text("• Core strength training")
                        Text("• Balance ball exercises")
                    }
                    else -> {
                        Text("• Dumbbell workouts")
                        Text("• Machine strength training")
                        Text("• Bodyweight exercises")
                    }
                }
            }
        }
    }
}

