package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ReportScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("Report Screen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder for a bar/pie chart
        Text("This screen could display data in chart form, from Room or an API.")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Home")
        }
    }
}



