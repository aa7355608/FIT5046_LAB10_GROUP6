package com.example.healthreminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rootNav = rememberNavController()

            NavHost(navController = rootNav, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onNavigateToRegister = { rootNav.navigate("register") },
                        onLoginSuccess        = { rootNav.navigate("main") { popUpTo("login") { inclusive = true } } }
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onBackToLogin      = { rootNav.popBackStack() },
                        onRegisterSuccess  = { rootNav.navigate("login") { popUpTo("register") { inclusive = true } } }
                    )
                }
                // Enter the "Home Page" and switch to the bottom navigation
                composable("main") {
                    BottomNavigationScreen()
                }
                // (Optional) If you still want to open a separate form/report/settings globally
                composable("form")   { FormScreen(rootNav)   }
                composable("report") { ReportScreen(rootNav) }
                composable("settings"){ SettingsScreen(rootNav) }
            }
        }
    }
}
