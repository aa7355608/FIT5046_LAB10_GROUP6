package com.example.healthreminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {

                // 登录页
                composable("login") {
                    LoginScreen(
                        onNavigateToRegister = {
                            navController.navigate("register")
                        },
                        onLoginSuccess = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // 注册页：传入两个 lambda
                composable("register") {
                    RegisterScreen(
                        onBackToLogin = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onRegisterSuccess = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // 主界面（Dashboard + Logout）
                composable("main") {
                    MainScreen(navController)
                }

                // 表单页
                composable("form") {
                    FormScreen(navController)
                }

                // 报告页
                composable("report") {
                    ReportScreen(navController)
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    label = { Text("Dashboard") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Dashboard"
                        )
                    }
                )
                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    label = { Text("Logout") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text("Home (Dashboard)", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            ReminderItem(
                iconRes = R.drawable.ic_water,
                iconDesc = "Water Icon",
                title = "Water Reminders",
                line1 = "Daily goal: 2000 ml",
                line2 = "Notifications every 2 hours"
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            ReminderItem(
                iconRes = R.drawable.ic_run,
                iconDesc = "Run Icon",
                title = "Exercise Reminders",
                line1 = "Custom time: 6:00 PM daily",
                line2 = "Delay if raining"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate("form") }) {
                Text("Go to Form Screen")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { navController.navigate("report") }) {
                Text("Go to Report Screen")
            }
        }
    }
}

