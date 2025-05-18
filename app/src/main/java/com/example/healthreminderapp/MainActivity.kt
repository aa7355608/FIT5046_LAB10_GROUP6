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
                // 进入“主页”，切换到底部导航
                composable("main") {
                    BottomNavigationScreen()
                }
                // （可选）如果你还想全局再开个独立 form/report/settings
                composable("form")   { FormScreen(rootNav)   }
                composable("report") { ReportScreen(rootNav) }
                composable("settings"){ SettingsScreen(rootNav) }
            }
        }
    }
}
