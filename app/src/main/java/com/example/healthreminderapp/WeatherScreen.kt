package com.example.healthreminderapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@Composable
fun WeatherScreen() {
    var temperature by remember { mutableStateOf<String?>(null) }
    var weatherDesc by remember { mutableStateOf<String?>("Loading...") }

    LaunchedEffect(Unit) {
        try {
            val json = withContext(Dispatchers.IO) {
                val apiUrl =
                    "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m"
                URL(apiUrl).readText()
            }
            val root = JSONObject(json)
            val current = root.getJSONObject("current_weather")
            temperature = current.getDouble("temperature").toString()
            weatherDesc = "Clear / Cloudy / Rain (code ${current.getInt("weathercode")})"
        } catch (e: Exception) {
            Log.e("Weather", "Error: ${e.localizedMessage}")
            weatherDesc = "Failed to fetch weather"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Weather Info", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        if (temperature != null) {
            Text("Temperature: $temperature°C", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Weather: $weatherDesc")
        } else {
            CircularProgressIndicator()
        }
    }
}
