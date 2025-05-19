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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Google Maps Compose
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


@Composable
fun RunningLogScreen() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // SensorManager and accelerometer setup
    val sensorManager = remember {
        context.getSystemService(SensorManager::class.java)
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // State to indicate if motion is detected
    var isMoving by remember { mutableStateOf(false) }

    // Start and end positions selected by user
    var startLocation by remember { mutableStateOf<LatLng?>(null) }
    var endLocation by remember { mutableStateOf<LatLng?>(null) }
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var distanceText by remember { mutableStateOf("--") }
    var durationText by remember { mutableStateOf("--") }

    // Initial camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-37.9, 145.05), 12f)
    }

    // Register accelerometer listener
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

                        // Start the coroutine delay to clear the motion state
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Start\n${startLocation?.let { formatLatLng(it) } ?: "--"}")
            Text("End\n${endLocation?.let { formatLatLng(it) } ?: "--"}")
            Text("Distance\n$distanceText")
            Text("Duration\n$durationText")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Motion Detected: ${if (isMoving) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
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
                } else if (endLocation == null) {
                    endLocation = latLng
                    coroutineScope.launch {
                        val result = fetchRouteFromGoogle(startLocation!!, endLocation!!)
                        polylinePoints = result.polylinePoints
                        distanceText = result.distanceText
                        durationText = result.durationText
                    }
                } else {
                    startLocation = latLng
                    endLocation = null
                    polylinePoints = emptyList()
                    distanceText = "--"
                    durationText = "--"
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
        Text("Tap two locations on the map to fetch a real path.", style = MaterialTheme.typography.bodySmall)
    }
}

fun formatLatLng(latLng: LatLng): String {
    return "%.3f, %.3f".format(latLng.latitude, latLng.longitude)
}

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
