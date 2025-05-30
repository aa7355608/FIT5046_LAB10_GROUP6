package com.example.healthreminderapp.workoutplan

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthreminderapp.WorkoutEntry
import com.example.healthreminderapp.WorkoutFormViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutFormScreen(
    navController: NavHostController? = null
) {
    val workoutOptions = listOf("Walking", "Running", "Swimming", "Ball Sports", "Strength Training")
    val durationOptions = listOf("15 minutes", "30 minutes", "45 minutes", "1 hour", "1.5 hours", "2 hours")
    val months = (1..12).map { "$it" }
    val days = (1..31).map { "$it" }

    var selectedWorkout by remember { mutableStateOf("") }
    var workoutExpanded by remember { mutableStateOf(false) }

    var selectedDuration by remember { mutableStateOf("") }
    var durationExpanded by remember { mutableStateOf(false) }

    var selectedMonth by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf("") }

    val context = LocalContext.current
    val viewModel: WorkoutFormViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Set Workout Goals") },
            navigationIcon = {
                IconButton(onClick = { navController?.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Workout Type Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Workout Type", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = workoutExpanded, onExpandedChange = { workoutExpanded = !workoutExpanded }) {
                    OutlinedTextField(
                        value = selectedWorkout,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Workout") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(workoutExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = workoutExpanded, onDismissRequest = { workoutExpanded = false }) {
                        workoutOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    selectedWorkout = it
                                    workoutExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Duration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Workout Duration", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = durationExpanded, onExpandedChange = { durationExpanded = !durationExpanded }) {
                    OutlinedTextField(
                        value = selectedDuration,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Duration") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(durationExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = durationExpanded, onDismissRequest = { durationExpanded = false }) {
                        durationOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    selectedDuration = it
                                    durationExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Date", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DropdownSelector(
                        label = "Month",
                        options = months,
                        selectedOption = selectedMonth,
                        onOptionSelected = { selectedMonth = it }
                    )
                    DropdownSelector(
                        label = "Day",
                        options = days,
                        selectedOption = selectedDay,
                        onOptionSelected = { selectedDay = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = {
                if (selectedWorkout.isNotEmpty() && selectedDuration.isNotEmpty()
                    && selectedMonth.isNotEmpty() && selectedDay.isNotEmpty()
                ) {
                    val entry = WorkoutEntry(
                        workoutType = selectedWorkout,
                        duration = selectedDuration,
                        month = selectedMonth.toInt(),
                        day = selectedDay.toInt()
                    )
                    viewModel.saveWorkout(entry)
                    navController?.navigateUp()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Workout")
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.width(150.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (selectedOption.isNotEmpty()) selectedOption else "Select $label")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
