package com.example.healthreminderapp

import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text("Home (Dashboard)", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            // Place your water/exercise reminders or any UI
            ReminderItem(
                iconRes = R.drawable.ic_water,
                iconDesc = "Water Icon",
                title = "Water Reminders",
                line1 = "Daily goal: 2000 ml",
                line2 = "Notifications every 2 hours"
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            ReminderItem(
                iconRes = R.drawable.ic_run,
                iconDesc = "Run Icon",
                title = "Exercise Reminders",
                line1 = "Custom time: 6:00 PM daily",
                line2 = "Delay if raining"
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
}

@Composable
fun ReminderItem(
    iconRes: Int,
    iconDesc: String,
    title: String,
    line1: String,
    line2: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = iconDesc,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(line1)
            Text(line2)
        }
    }
}
