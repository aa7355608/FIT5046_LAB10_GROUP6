package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RunningLogScreen() {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("Running Log & Analysis", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Start\n--:--")
            Text("End\n--:--")
            Text("Distance\n-- mi")
            Text("Duration\n--:--")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Optional", style = MaterialTheme.typography.labelLarge)
        Text("Cadence, stride\n(if connected via wearable fitness tracker)", style = MaterialTheme.typography.bodySmall)
    }
}

