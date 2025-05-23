package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.shape.RoundedCornerShape

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

        // Weekly Hours Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Weekly Exercise Time", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${weeklyHours.toInt()} hours",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = weeklyHours,
                    onValueChange = {
                        weeklyHours = it
                        prediction = predictor.predict(weeklyHours)
                    },
                    valueRange = 0f..15f,
                    steps = 14
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Distribution Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Training Type Distribution", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cardio: ${(prediction[0] * 100).toInt()}%")
                Text("Balanced: ${(prediction[1] * 100).toInt()}%")
                Text("Strength: ${(prediction[2] * 100).toInt()}%")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recommendation Card
        val mainRecommendation = when {
            prediction[0] > prediction[1] && prediction[0] > prediction[2] -> "Cardio"
            prediction[1] > prediction[0] && prediction[1] > prediction[2] -> "Balanced"
            else -> "Strength"
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Personalized Recommendation", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Based on your ${weeklyHours.toInt()} hours of weekly exercise,",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "We recommend you focus on $mainRecommendation training.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Suggested Exercises:", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
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

