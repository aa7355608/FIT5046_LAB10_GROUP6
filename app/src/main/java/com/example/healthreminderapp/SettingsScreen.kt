// File: app/src/main/java/com/example/healthreminderapp/SettingsScreen.kt
package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    /* editable state */
    var nickname   by remember { mutableStateOf("Shadow") }
    var avatarDesc by remember { mutableStateOf("Not set") }
    var account    by remember { mutableStateOf("153****69") }
    var identity   by remember { mutableStateOf("Not linked") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profile Information",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                /* Avatar section */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Spacer(Modifier.width(16.dp))
                    Text("Tap to change avatar (not implemented yet)", fontSize = 14.sp)
                }

                Spacer(Modifier.height(16.dp))

                /* Editable fields */
                ProfileTextField(label = "Nickname",        value = nickname)   { nickname   = it }
                ProfileTextField(label = "Digital Avatar",  value = avatarDesc) { avatarDesc = it }
                ProfileTextField(label = "Alipay Account",  value = account)    { account    = it }
                ProfileTextField(label = "Identity Info",   value = identity)   { identity   = it }
            }
        }

        Spacer(Modifier.height(24.dp))

        /* Save button */
        Button(
            onClick = {
                // TODO: persist data
                println("Saved: $nickname / $account")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Spacer(Modifier.height(16.dp))

        /* Log-out button */
        OutlinedButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("main") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        singleLine = true
    )
}
