@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun FormScreen(
    navController: NavHostController,
    viewModel: ReminderSettingsViewModel
) {
    // Shared ViewModel 状态
    var waterGoal by remember { mutableStateOf(viewModel.waterGoal.value) }
    var waterInterval by remember { mutableStateOf(viewModel.waterInterval.value) }
    var exerciseTime by remember { mutableStateOf(viewModel.exerciseTime.value) }
    var delayIfRaining by remember { mutableStateOf(viewModel.delayIfRaining.value) }

    // Room Database
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {

        Text("Edit Reminder Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Water Goal
        OutlinedTextField(
            value = waterGoal,
            onValueChange = { waterGoal = it },
            label = { Text("Daily Water Goal (ml)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Water Interval Dropdown
        IntervalDropdown(
            selectedOption = waterInterval,
            onOptionSelected = { waterInterval = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Exercise Time
        OutlinedTextField(
            value = exerciseTime,
            onValueChange = { exerciseTime = it },
            label = { Text("Exercise Time (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Delay If Raining Switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Delay if raining", modifier = Modifier.weight(1f))
            Switch(checked = delayIfRaining, onCheckedChange = { delayIfRaining = it })
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
        Button(
            onClick = {
                // Update ViewModel
                viewModel.updateSettings(
                    goal = waterGoal,
                    interval = waterInterval,
                    time = exerciseTime,
                    delay = delayIfRaining
                )

                // Save to Room
                scope.launch {
                    db.reminderSettingsDao().insert(
                        ReminderSettings(
                            waterGoal = waterGoal,
                            waterInterval = waterInterval,
                            exerciseTime = exerciseTime,
                            delayIfRaining = delayIfRaining
                        )
                    )
                }

                // Back to previous screen
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
}

@Composable
fun IntervalDropdown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("1 hour", "2 hours", "3 hours")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Water Reminder Interval") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
