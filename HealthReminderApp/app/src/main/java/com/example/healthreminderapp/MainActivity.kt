package com.example.healthreminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.healthreminderapp.ui.theme.HealthReminderAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthReminderAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    RootNavigationGraph(navController)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("home") }
            )
        }
        composable("register") {
            RegisterScreen(
                onBackToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("home") }
            )
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("form") {
            FormScreen(navController)
        }
        composable("report") {
            ReportScreen(navController)
        }
    }
}
