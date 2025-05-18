package com.example.healthreminderapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationScreen() {

    val nav = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        /* —— 直接去掉顶栏 —— */
        /* topBar = { TopAppBar(title = { Text("Health Reminder") }) }, */

        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0; nav.navigate("dashboard") { launchSingleTop = true } },
                    icon  = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1; nav.navigate("log") { launchSingleTop = true } },
                    icon  = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Analysis") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick  = { selectedTab = 2; nav.navigate("workout") { launchSingleTop = true } },
                    icon  = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    label = { Text("Plan") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick  = { selectedTab = 3; nav.navigate("reminder") { launchSingleTop = true } },
                    icon  = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("Reminder") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick  = { selectedTab = 4; nav.navigate("settings") { launchSingleTop = true } },
                    icon  = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController    = nav,
            startDestination = "dashboard",
            modifier         = Modifier.padding(padding)
        ) {
            composable("dashboard") { HomeDashboardScreen(nav) }
            composable("log")       { RunningLogScreen() }
            composable("workout")   { WorkoutPlanScreen() }
            composable("reminder")  { ReminderMainScreen(nav) }
            composable("settings")  { SettingsScreen(nav) }

            /* Dashboard cards */
            composable("meals")        { MealsScreen() }
            composable("foodRankings") { FoodRankingsScreen() }
            composable("weightTest")   { WeightTestScreen() }
        }
    }
}

