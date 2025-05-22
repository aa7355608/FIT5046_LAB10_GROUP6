package com.example.healthreminderapp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
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

        WeatherInfoWithCityList()


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
fun WeatherInfoWithCityList() {
    val cityOptions = mapOf(
        "Melbourne" to Pair(-37.8136, 144.9631),
        "Sydney" to Pair(-33.8688, 151.2093),
        "Brisbane" to Pair(-27.4698, 153.0251),
        "Tokyo" to Pair(35.6762, 139.6503),
        "New York" to Pair(40.7128, -74.0060),
        "London" to Pair(51.5072, -0.1276),
        "Paris" to Pair(48.8566, 2.3522),
        "Beijing" to Pair(39.9042, 116.4074)
    )

    var selectedCity by remember { mutableStateOf("Melbourne") }
    val (latitude, longitude) = cityOptions[selectedCity] ?: Pair(-37.8136, 144.9631)

    var temperature by remember { mutableStateOf("--") }
    var windSpeed by remember { mutableStateOf("--") }
    var humidity by remember { mutableStateOf("--") }
    var weatherDesc by remember { mutableStateOf("--") }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(selectedCity) {
        loading = true
        try {
            val response = withContext(Dispatchers.IO) {
                val url =
                    "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true&hourly=relativehumidity_2m"
                URL(url).readText()
            }

            val root = JSONObject(response)
            val current = root.getJSONObject("current_weather")
            temperature = current.getDouble("temperature").toString()
            windSpeed = current.getDouble("windspeed").toString()

            val weatherCode = current.getInt("weathercode")
            weatherDesc = when (weatherCode) {
                0 -> "☀️ Clear"
                1, 2, 3 -> "⛅ Partly Cloudy"
                45, 48 -> "🌫️ Fog"
                51, 53, 55 -> "🌦️ Drizzle"
                61, 63, 65 -> "🌧️ Rain"
                66, 67 -> "🌨️ Freezing Rain"
                71, 73, 75, 77 -> "❄️ Snow"
                80, 81, 82 -> "🌧️ Rain Showers"
                95, 96, 99 -> "🌩️ Thunderstorm"
                else -> "❓ Unknown"
            }

            humidity = try {
                val hourly = root.getJSONObject("hourly")
                val humidityArray = hourly.getJSONArray("relativehumidity_2m")
                humidityArray.getDouble(0).toString()
            } catch (e: Exception) {
                "--"
            }

        } catch (e: Exception) {
            temperature = "--"
            windSpeed = "--"
            humidity = "--"
            weatherDesc = "Failed"
        } finally {
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        // Weather Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weather in $selectedCity",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Condition: $weatherDesc", style = MaterialTheme.typography.bodyLarge)
                    Text("Temperature: $temperature°C", style = MaterialTheme.typography.bodyLarge)
                    Text("Humidity: $humidity%", style = MaterialTheme.typography.bodyLarge)
                    Text("Wind Speed: $windSpeed km/h", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Select a City", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Styled city list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            items(cityOptions.keys.toList()) { city ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedCity = city },
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = city,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}




