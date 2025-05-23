package com.example.healthreminderapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.*
import com.example.healthreminderapp.ui.theme.HealthReminderAppTheme

class MainActivity : ComponentActivity() {

    private val settingsViewModel: ReminderSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }


        settingsViewModel.loadLatestFromDatabase(applicationContext)

        setContent {
            val rootNav = rememberNavController()


            LaunchedEffect(Unit) {
                settingsViewModel.exerciseTime.value?.let { time ->
                    val (hour, minute) = time.split(":").map { it.toInt() }
                    scheduleDailyExerciseReminder(this@MainActivity, hour, minute)
                }
            }

            HealthReminderAppTheme {
                NavHost(navController = rootNav, startDestination = "login") {

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
