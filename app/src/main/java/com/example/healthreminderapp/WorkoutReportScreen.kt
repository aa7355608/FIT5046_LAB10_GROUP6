package com.example.healthreminderapp

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun WorkoutReportScreen(
    navController: NavHostController? = null
) {
    val context = LocalContext.current
    var recentWorkouts by remember { mutableStateOf<List<WorkoutEntry>>(emptyList()) }
    var chartData by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }

    LaunchedEffect(Unit) {

        val all = AppDatabase.getDatabase(context).workoutDao().getAllWorkouts()
        

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        

        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, currentYear)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }


        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)


        val last7 = all.filter { workout ->
            val workoutMonth = workout.month
            val workoutDay = workout.day
            

            if (workoutMonth == currentMonth) {
                workoutDay > (currentDay - 7) && workoutDay <= currentDay
            }

            else if (workoutMonth == currentMonth - 1 || (currentMonth == 1 && workoutMonth == 12)) {
                val daysInLastMonth = calendar.apply { 
                    add(java.util.Calendar.MONTH, -1) 
                }.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                val daysFromLastMonth = 7 - (currentDay)
                workoutDay > (daysInLastMonth - daysFromLastMonth)
            }
            else false
        }


        val last30 = all.filter { workout ->
            val workoutMonth = workout.month
            val workoutDay = workout.day
            

            if (workoutMonth == currentMonth) {
                workoutDay > (currentDay - 30) && workoutDay <= currentDay
            }

            else if (workoutMonth == currentMonth - 1 || (currentMonth == 1 && workoutMonth == 12)) {
                val daysInLastMonth = calendar.apply { 
                    add(java.util.Calendar.MONTH, -1) 
                }.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                val daysFromLastMonth = 30 - currentDay
                workoutDay > (daysInLastMonth - daysFromLastMonth)
            }
            else false
        }

        println("📊 Data counts:")
        println("Total workouts: ${all.size}")
        println("Last 7 days: ${last7.size}")
        println("Last 30 days: ${last30.size}")

        recentWorkouts = last7.sortedByDescending { 
            it.month * 100 + it.day
        }

        if (last30.isEmpty()) {
            println("⚠️ Warning: No workout data found for the last 30 days")
            chartData = mapOf()
        } else {
            chartData = last30
                .groupBy { it.workoutType }
                .mapValues { entry ->
                    val total = entry.value.sumOf {
                        val minutes = durationTextToMinutes(it.duration)
                        println("⏱ Parsed [${it.duration}] → $minutes min for type: ${it.workoutType}")
                        minutes.toLong()
                    }
                    println("📊 Workout Type: ${entry.key} → Total Minutes: $total")
                    total.toFloat()
                }
            println("📊 Generated chart data from last 30 days: $chartData")
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController?.navigateUp() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "return"
                )
            }
            Text(
                text = "sports report",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text("Workout Summary (Last 7 Days)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.height(200.dp)) {
            items(recentWorkouts) { workout ->
                Text("${workout.workoutType} - ${workout.duration} on ${workout.month}/${workout.day}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Workout Distribution (Last 30 Days)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        WorkoutPieChart(data = chartData)
    }
}

@Composable
fun WorkoutPieChart(data: Map<String, Float>) {
    println("🥧 Creating PieChart with data: $data")

    if (data.isEmpty()) {
        Text(
            text = "No workout data available for the last 30 days.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            PieChart(context).apply {
                val entries = data.map {
                    println("📊 Creating PieEntry: value=${it.value}, label=${it.key}")
                    PieEntry(it.value, it.key)
                }
                val dataSet = PieDataSet(entries, "Workout Types").apply {
                    setColors(ColorTemplate.MATERIAL_COLORS, 255)
                    valueTextSize = 14f
                    valueTextColor = android.graphics.Color.BLACK
                }
                this.data = PieData(dataSet)
                this.description.isEnabled = false
                this.legend.isEnabled = true
                this.legend.textSize = 12f
                this.setUsePercentValues(true)
                this.centerText = "Workout\nDistribution"
                this.setCenterTextSize(16f)
                this.animateY(1000)
                this.invalidate()
            }
        }
    )
}

fun durationTextToMinutes(duration: String): Int {
    val lower = duration.lowercase().replace(" ", "")
    return when {
        "1.5" in lower -> 90
        "2hour" in lower -> 120
        "1hour" in lower -> 60
        "45" in lower -> 45
        "30" in lower -> 30
        "15" in lower -> 15
        else -> 0
    }
}

