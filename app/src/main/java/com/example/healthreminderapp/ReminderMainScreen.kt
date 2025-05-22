package com.example.healthreminderapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import org.json.JSONObject
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ReminderMainScreen(
    navController: NavHostController,
    viewModel: ReminderSettingsViewModel
) {
    val waterGoal by viewModel.waterGoal.collectAsState()
    val waterInterval by viewModel.waterInterval.collectAsState()
    val exerciseTime by viewModel.exerciseTime.collectAsState()
    val delayIfRaining by viewModel.delayIfRaining.collectAsState()

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Health Reminder System", style = MaterialTheme.typography.headlineSmall)

        WeatherInfoTextBlock()

        Spacer(modifier = Modifier.height(16.dp))

        ReminderItem(
            iconRes = R.drawable.ic_water,
            iconDesc = "Water Icon",
            title = "Water Reminders",
            line1 = "Daily goal: $waterGoal ml",
            line2 = "Notifications every $waterInterval"
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        ReminderItem(
            iconRes = R.drawable.ic_run,
            iconDesc = "Run Icon",
            title = "Exercise Reminders",
            line1 = "Custom time: $exerciseTime daily",
            line2 = if (delayIfRaining) "Delay if raining" else "No delay"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.navigate("form") }) {
            Text("Go to Form Screen")
        }

        Spacer(modifier = Modifier.height(8.dp))


    }
}
@Composable
fun WeatherInfoTextBlock() {
    var temperature by remember { mutableStateOf<String?>(null) }
    var weatherDesc by remember { mutableStateOf<String>("Loading...") }

    LaunchedEffect(Unit) {
        try {
            val json = withContext(Dispatchers.IO) {
                URL("https://api.open-meteo.com/v1/forecast?latitude=35.0&longitude=139.0&current_weather=true")
                    .readText()
            }
            val root = JSONObject(json)
            val current = root.getJSONObject("current_weather")
            temperature = current.getDouble("temperature").toString()
            val weatherCode = current.getInt("weathercode")
            weatherDesc = when (weatherCode) {
                0 -> "Clear"
                1, 2, 3 -> "Partly Cloudy"
                45, 48 -> "Fog"
                51, 53, 55 -> "Drizzle"
                61, 63, 65 -> "Rain"
                66, 67 -> "Freezing Rain"
                71, 73, 75, 77 -> "Snow"
                80, 81, 82 -> "Rain Showers"
                95, 96, 99 -> "Thunderstorm"
                else -> "Unknown"
            }
        } catch (e: Exception) {
            weatherDesc = "Failed to fetch weather"
        }
    }

    if (temperature != null) {
        Text(
            text = "Weather: $weatherDesc, $temperature°C",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    } else {
        Text(
            text = weatherDesc,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

