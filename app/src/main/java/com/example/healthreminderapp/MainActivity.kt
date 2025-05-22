package com.example.healthreminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.*
import com.example.healthreminderapp.ui.theme.HealthReminderAppTheme

class MainActivity : ComponentActivity() {

    private val settingsViewModel: ReminderSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        settingsViewModel.loadLatestFromDatabase(applicationContext)

        setContent {
            val rootNav = rememberNavController()

            HealthReminderAppTheme {
                NavHost(navController = rootNav, startDestination = "login") {

                    // 登录页
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = { rootNav.navigate("register") },
                            onLoginSuccess = {
                                rootNav.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }


                    composable("register") {
                        RegisterScreen(
                            onBackToLogin = { rootNav.popBackStack() },
                            onRegisterSuccess = {
                                rootNav.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }


                    composable("main") {
                        BottomNavigationScreen(
                            rootNav = rootNav,
                            settingsViewModel = settingsViewModel
                        )
                    }


                    composable("form") {
                        FormScreen(navController = rootNav, viewModel = settingsViewModel)
                    }

                    composable("weather") {
                        WeatherScreen()
                    }


                    composable("settings") {
                        SettingsScreen(navController = rootNav)
                    }
                }
            }
        }
    }
}
