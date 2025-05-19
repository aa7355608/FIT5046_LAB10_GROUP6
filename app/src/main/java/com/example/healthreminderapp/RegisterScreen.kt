package com.example.healthreminderapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var passwordsMatch by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Register", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                label = { Text("Email") },
                isError = !isEmailValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isEmailValid) {
                Text("Please enter the correct email address", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isPasswordValid = it.length >= 6
                    passwordsMatch = (it == confirm)
                },
                label = { Text("Password") },
                isError = !isPasswordValid,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isPasswordValid) {
                Text("The password should be at least 6 characters long", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = {
                    confirm = it
                    passwordsMatch = (it == password)
                },
                label = { Text("Confirm Password") },
                isError = !passwordsMatch,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (!passwordsMatch) {
                Text("The passwords entered twice are not the same", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar("Registration failed：${e.localizedMessage}")
                            }
                        }
                },
                enabled = isEmailValid && isPasswordValid && passwordsMatch && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Sign Up")
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onBackToLogin) {
                Text("Back to Login")
            }
        }
    }
}
