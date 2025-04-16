package com.example.helloworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.helloworld.ui.theme.HelloWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWorldTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { DietTrackingScreen(navController) }
                    composable("foodLogging") { FoodLoggingScreen(navController) }
                    composable("nutritionalAnalysis") { NutritionalAnalysisScreen(navController) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietTrackingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Diet & Nutrition Tracking") })
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Text("Food Logging", style = MaterialTheme.typography.titleMedium)
            Text("• Supports photo upload or text input\n• Optional: Connect to external food recognition API for food identification")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate("foodLogging") }) {
                Text("Go to Food Logging")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nutritional Analysis", style = MaterialTheme.typography.titleMedium)
            Text("• Based on daily goal, provides calorie and nutrient suggestions\n• Can be personalized")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate("nutritionalAnalysis") }) {
                Text("Go to Nutritional Analysis")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLoggingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Logging") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Here you can upload photos or enter food manually.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* TODO: Upload food photo or enter food */ }) {
                Text("Upload Food")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionalAnalysisScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutritional Analysis") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Get personalized calorie and nutrient suggestions.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* TODO: Show analysis results */ }) {
                Text("Show Analysis")
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar {
        NavigationBarItem(
            icon = {  },
            label = { Text("Dashboard") },
            selected = false,
            onClick = { /* No action */ }
        )
        NavigationBarItem(
            icon = { },
            label = { Text("About Us") },
            selected = false,
            onClick = { /* No action */ }
        )
        NavigationBarItem(
            icon = {  },
            label = { Text("Logout") },
            selected = false,
            onClick = { /* No action */ }
        )
    }
}