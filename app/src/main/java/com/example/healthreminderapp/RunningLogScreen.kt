package com.example.healthreminderapp

// Android/Compose UI and utility imports
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

@Composable
fun RunningLogScreen() {
    // Coroutine scope for launching background tasks
    val coroutineScope = rememberCoroutineScope()

    // Start and end positions selected by user
    var startLocation by remember { mutableStateOf<LatLng?>(null) }
    var endLocation by remember { mutableStateOf<LatLng?>(null) }

    // Polyline points for drawing route
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // Display values for distance and duration
    var distanceText by remember { mutableStateOf("--") }
    var durationText by remember { mutableStateOf("--") }

    // Initial map camera position (Monash Clayton area)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-37.9, 145.05), 12f)
    }

    // Main layout
    Column(modifier = Modifier.padding(24.dp)) {
        Text("Running Log & Analysis", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        // Display selected locations and calculated data
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Start\n${startLocation?.let { formatLatLng(it) } ?: "--"}")
            Text("End\n${endLocation?.let { formatLatLng(it) } ?: "--"}")
            Text("Distance\n$distanceText")
            Text("Duration\n$durationText")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map component with click handling
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            cameraPositionState = cameraPositionState,

            // Handle tap to select start/end point
            onMapClick = { latLng ->
                if (startLocation == null) {
                    // First click: set start
                    startLocation = latLng
                    endLocation = null
                    polylinePoints = emptyList()
                    distanceText = "--"
                    durationText = "--"
                } else if (endLocation == null) {
                    // Second click: set end and fetch route
                    endLocation = latLng

                    // Launch coroutine to fetch route via Directions API
                    coroutineScope.launch {
                        val result = fetchRouteFromGoogle(startLocation!!, endLocation!!)
                        polylinePoints = result.polylinePoints
                        distanceText = result.distanceText
                        durationText = result.durationText
                    }
                } else {
                    // Third click: reset and start again
                    startLocation = latLng
                    endLocation = null
                    polylinePoints = emptyList()
                    distanceText = "--"
                    durationText = "--"
                }
            }
        ) {
            // Add start marker
            startLocation?.let {
                Marker(state = MarkerState(position = it), title = "Start")
            }

            // Add end marker
            endLocation?.let {
                Marker(state = MarkerState(position = it), title = "End")
            }

            // Draw polyline if available
            if (polylinePoints.isNotEmpty()) {
                Polyline(points = polylinePoints)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Tap two locations on the map to fetch a real path.", style = MaterialTheme.typography.bodySmall)
    }
}

// Format coordinates as simple string for display
fun formatLatLng(latLng: LatLng): String {
    return "%.3f, %.3f".format(latLng.latitude, latLng.longitude)
}

// Data class to hold route result (decoded points, distance text, duration text)
data class RouteResult(
    val polylinePoints: List<LatLng>,
    val distanceText: String,
    val durationText: String
)

// Suspend function that calls Google Directions API and parses result
suspend fun fetchRouteFromGoogle(start: LatLng, end: LatLng): RouteResult {
    return withContext(Dispatchers.IO) {
        try {
            val apiKey = "AIzaSyAgTkkfpmndt9eqvzFf12kq4c6QI1GXkQk"

            // Build API request URL
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${start.latitude},${start.longitude}" +
                    "&destination=${end.latitude},${end.longitude}" +
                    "&mode=walking&key=$apiKey"

            // Use OkHttp to perform the request
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            val json = response.body?.string() ?: return@withContext RouteResult(emptyList(), "--", "--")
            if (json.isBlank()) return@withContext RouteResult(emptyList(), "--", "--")

            // Parse JSON response
            val result = Json.parseToJsonElement(json).jsonObject
            val routes = result["routes"]?.jsonArray ?: return@withContext RouteResult(emptyList(), "--", "--")
            if (routes.isEmpty()) return@withContext RouteResult(emptyList(), "--", "--")

            // Get encoded polyline and distance/duration from response
            val overviewPolyline = routes[0].jsonObject["overview_polyline"]
                ?.jsonObject?.get("points")?.jsonPrimitive?.content ?: ""

            val legs = routes[0].jsonObject["legs"]!!.jsonArray[0].jsonObject
            val distance = legs["distance"]?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "--"
            val duration = legs["duration"]?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "--"

            // Decode polyline string into LatLng list
            val decodedPoints = decodePolyline(overviewPolyline)

            // Return route result
            return@withContext RouteResult(decodedPoints, distance, duration)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext RouteResult(emptyList(), "--", "--")
        }
    }
}

// Decode Google Maps encoded polyline into list of LatLng
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

        // Decode latitude
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        // Decode longitude
        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        // Convert to LatLng and add to list
        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }

    return poly
}
