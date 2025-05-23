package com.example.healthreminderapp

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.healthreminderapp.workoutplan.WorkoutFormScreen
import com.example.healthreminderapp.WorkoutAiRecommendationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationScreen(
    rootNav: NavHostController,
    settingsViewModel: ReminderSettingsViewModel
) {
    val bottomNav = rememberNavController()
    var selectedTab by remember { mutableStateOf(3) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        bottomNav.navigate("dashboard") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        bottomNav.navigate("log") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Analysis") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        bottomNav.navigate("workout") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    label = { Text("Plan") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        bottomNav.navigate("reminder") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("Reminder") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = {
                        selectedTab = 4
                        bottomNav.navigate("settings") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNav,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") { HomeDashboardScreen(bottomNav) }
            composable("log") { RunningLogScreen() }
            composable("workout") { WorkoutPlanScreen(navController = bottomNav) }
            composable("workout_form") { WorkoutFormScreen(navController = bottomNav) }
            composable("workout_report") { WorkoutReportScreen(navController = bottomNav) }
            composable("workout_ai") { WorkoutAiRecommendationScreen(navController = bottomNav) }
            composable("reminder") {
                ReminderMainScreen(
                    navController = bottomNav,
                    viewModel = settingsViewModel
                )
            }
            composable("form") {
                FormScreen(
                    navController = bottomNav,
                    viewModel = settingsViewModel
                )
            }
            composable("settings") { SettingsScreen(bottomNav) }
            composable("meals") { MealsScreen() }
            composable("foodRankings") { FoodRankingsScreen() }
            composable("weightTest") { WeightTestScreen() }
        }
    }
}

