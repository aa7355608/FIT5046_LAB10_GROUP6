package com.example.healthreminderapp

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

        Button(onClick = { navController.navigate("report") }) {
            Text("Go to Report Screen")
        }
    }
}
