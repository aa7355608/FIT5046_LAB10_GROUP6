package com.example.healthreminderapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FormScreen(navController: NavHostController) {
    var userName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Option A") }
    val options = listOf("Option A", "Option B", "Option C")

    var userList by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Form Screen", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(Date())
        }) {
            Text("Pick Date")
        }
        if (selectedDate.isNotEmpty()) {
            Text("Selected Date: $selectedDate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text("Select Option") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOption = option
                            dropdownExpanded = false
                        }
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 8.dp)
                    .clickable { dropdownExpanded = !dropdownExpanded }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (userName.isNotBlank()) {
                userList = userList + "$userName | $selectedDate | $selectedOption"
            }
        }) {
            Text("Add Entry")
        }

        Spacer(modifier = Modifier.height(16.dp))

        userList.forEach {
            Text(it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Home")
        }
    }
}
