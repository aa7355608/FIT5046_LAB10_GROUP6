package com.example.healthreminderapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.abs
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanScreen(navController: NavController) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("Personalized Workout Plan", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        // First card: Set your goals
        Card(
            onClick = { navController.navigate("workout_form") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Set Your Goals", style = MaterialTheme.typography.titleMedium)
                Text("Tap to input your fitness goals and preferences")
            }
        }

        // Second card: View weekly report
        Card(
            onClick = { navController.navigate("workout_report") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("View Weekly Report", style = MaterialTheme.typography.titleMedium)
                Text("Check your workout summary and progress")
            }
        }

        // Third card: AI Recommendation
        Card(
            onClick = { navController.navigate("workout_ai") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI Recommendation", style = MaterialTheme.typography.titleMedium)
                Text("Get suggestions based on your weekly workout time")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        WorkoutMotionStatusBox()
    }
}

@Composable
fun WorkoutMotionStatusBox() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    var status by remember { mutableStateOf("🔄 Detecting...") }
    var lastMagnitude by remember { mutableStateOf(0f) }
    var message by remember { mutableStateOf("") }
    var lastUpdateTime by remember { mutableStateOf(0L) }
    var messageIndex by remember { mutableStateOf(0) }
    var isStill by remember { mutableStateOf(false) }

    val funnyLinesStill = listOf(
        "Even a sloth would call this lazy.",
        "Sometimes stillness is strength.",
        "You could win a statue contest!",
        "Taking rest like a champ.",
        "Channeling your inner rock."
    )

    val funnyLinesMoving = listOf(
        "You're moving like lightning!",
        "Fueling the world with your energy.",
        "Momentum never looked better.",
        "You're on fire! 🔥",
        "Crushing it, one step at a time."
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (status.contains("Still")) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.errorContainer,
        label = "motionColor"
    )

    LaunchedEffect(Unit) {
        if (isStill) {
            message = funnyLinesStill.random()
        }
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val current = sqrt(x * x + y * y + z * z)
                val delta = abs(current - lastMagnitude)
                lastMagnitude = current

                val currentTime = System.currentTimeMillis()
                val currentIsStill = delta < 0.5f

                if (currentIsStill != isStill) {
                    isStill = currentIsStill
                    status = if (isStill) "🧘 Status: Still" else "🏃 Status: Moving"
                    message = if (isStill) {
                        funnyLinesStill.random()
                    } else {
                        funnyLinesMoving[messageIndex]
                    }
                }

                if (!isStill && currentTime - lastUpdateTime >= 2 * 60 * 60 * 1000) {
                    messageIndex = (messageIndex + 1) % funnyLinesMoving.size
                    lastUpdateTime = currentTime
                    message = funnyLinesMoving[messageIndex]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Motion Detection", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(status, style = MaterialTheme.typography.bodyLarge)
            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(message, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
