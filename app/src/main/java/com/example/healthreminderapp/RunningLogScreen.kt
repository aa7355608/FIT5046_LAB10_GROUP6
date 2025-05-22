package com.example.healthreminderapp

// Android/Compose UI and utility imports
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Google Maps Compose
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Coroutine, JSON, HTTP libraries
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request

// Math for polyline decoding
import kotlin.math.*
import kotlinx.coroutines.*
import java.net.URLEncoder

@Composable
fun RunningLogScreen() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val sensorManager = remember {
        context.getSystemService(SensorManager::class.java)
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    var isMoving by remember { mutableStateOf(false) }
    var startLocation by remember { mutableStateOf<LatLng?>(null) }
    var endLocation by remember { mutableStateOf<LatLng?>(null) }
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var distanceText by remember { mutableStateOf("--") }
    var durationText by remember { mutableStateOf("--") }
    var energyText by remember { mutableStateOf("--") }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-37.9, 145.05), 12f)
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    val magnitude = sqrt(x * x + y * y + z * z)
                    if (magnitude > 15f) {
                        isMoving = true
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(3000)
                            isMoving = false
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Running Log & Analysis", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Start Location:", style = MaterialTheme.typography.labelMedium)
                Text(startLocation?.let { formatLatLng(it) } ?: "--", fontSize = 16.sp)
                Text("End Location:", style = MaterialTheme.typography.labelMedium)
                Text(endLocation?.let { formatLatLng(it) } ?: "--", fontSize = 16.sp)
                Text("Distance:", style = MaterialTheme.typography.labelMedium)
                Text(distanceText, fontSize = 16.sp)
                Text("Duration:", style = MaterialTheme.typography.labelMedium)
                Text(durationText, fontSize = 16.sp)
                Text("Energy Burned: $energyText kcal", fontSize = 16.sp)
                Text("Motion Detected: ${if (isMoving) "Yes" else "No"}", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            coroutineScope.launch {
                val location = geocodePlace(searchQuery.text)
                location?.let {
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 15f))
                }
            }
        }) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                if (startLocation == null) {
                    startLocation = latLng
                    endLocation = null
                    polylinePoints = emptyList()
                    distanceText = "--"
                    durationText = "--"
                    energyText = "--"
                } else if (endLocation == null) {
                    endLocation = latLng

                    coroutineScope.launch {
                        val result = fetchRouteFromGoogle(startLocation!!, endLocation!!)
                        polylinePoints = result.polylinePoints
                        distanceText = result.distanceText
                        durationText = result.durationText

                        val distanceKm = extractKm(result.distanceText)
                        val timeMin = extractMinutes(result.durationText)
                        val kcal = estimateEnergyBurn(distanceKm, timeMin)
                        energyText = "%.1f".format(kcal)


                    }
                } else {
                    startLocation = latLng
                    endLocation = null
                    polylinePoints = emptyList()
                    distanceText = "--"
                    durationText = "--"
                    energyText = "--"
                }
            }
        ) {
            startLocation?.let {
                Marker(state = MarkerState(position = it), title = "Start")
            }
            endLocation?.let {
                Marker(state = MarkerState(position = it), title = "End")
            }
            if (polylinePoints.isNotEmpty()) {
                Polyline(points = polylinePoints)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Tap two locations on the map to fetch a real path.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

suspend fun geocodePlace(place: String): LatLng? {
    return withContext(Dispatchers.IO) {
        try {
            val apiKey = "AIzaSyAgTkkfpmndt9eqvzFf12kq4c6QI1GXkQk"
            val encodedPlace = URLEncoder.encode(place, "UTF-8")
            val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedPlace&key=$apiKey"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: return@withContext null
            val root = Json.parseToJsonElement(json).jsonObject
            val results = root["results"]?.jsonArray ?: return@withContext null
            val location = results.firstOrNull()?.jsonObject
                ?.get("geometry")?.jsonObject
                ?.get("location")?.jsonObject
            val lat = location?.get("lat")?.jsonPrimitive?.doubleOrNull
            val lng = location?.get("lng")?.jsonPrimitive?.doubleOrNull
            if (lat != null && lng != null) LatLng(lat, lng) else null
        } catch (e: Exception) {
            Log.e("Geocode", "Error", e)
            null
        }
    }
}

fun formatLatLng(latLng: LatLng): String {
    return "%.3f, %.3f".format(latLng.latitude, latLng.longitude)
}

fun estimateEnergyBurn(distanceKm: Double, timeMin: Int): Double {
    val weightKg = 70  // you can later make this dynamic
    val speedKmh = distanceKm / (timeMin / 60.0)
    val met = if (speedKmh < 6) 3.5 else 7.0
    return met * weightKg * (timeMin / 60.0)
}

fun extractKm(text: String): Double {
    return text.replace("km", "").trim().toDoubleOrNull() ?: 0.0
}

fun extractMinutes(text: String): Int {
    return text.replace("mins", "").replace("min", "").trim().toIntOrNull() ?: 0
}

// Assume fetchRouteFromGoogle and decodePolyline already implemented elsewhere

data class RouteResult(
    val polylinePoints: List<LatLng>,
    val distanceText: String,
    val durationText: String
)

suspend fun fetchRouteFromGoogle(start: LatLng, end: LatLng): RouteResult {
    return withContext(Dispatchers.IO) {
        try {
            val apiKey = "AIzaSyAgTkkfpmndt9eqvzFf12kq4c6QI1GXkQk"
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${start.latitude},${start.longitude}" +
                    "&destination=${end.latitude},${end.longitude}" +
                    "&mode=walking&key=$apiKey"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: return@withContext RouteResult(emptyList(), "--", "--")
            if (json.isBlank()) return@withContext RouteResult(emptyList(), "--", "--")
            val result = Json.parseToJsonElement(json).jsonObject
            val routes = result["routes"]?.jsonArray ?: return@withContext RouteResult(emptyList(), "--", "--")
            if (routes.isEmpty()) return@withContext RouteResult(emptyList(), "--", "--")
            val overviewPolyline = routes[0].jsonObject["overview_polyline"]
                ?.jsonObject?.get("points")?.jsonPrimitive?.content ?: ""
            val legs = routes[0].jsonObject["legs"]!!.jsonArray[0].jsonObject
            val distance = legs["distance"]?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "--"
            val duration = legs["duration"]?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "--"
            val decodedPoints = decodePolyline(overviewPolyline)
            return@withContext RouteResult(decodedPoints, distance, duration)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext RouteResult(emptyList(), "--", "--")
        }
    }
}

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0
    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat
        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng
        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }
    return poly
}
