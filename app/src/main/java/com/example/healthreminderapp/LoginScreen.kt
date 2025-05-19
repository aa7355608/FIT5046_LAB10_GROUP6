package com.example.healthreminderapp

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Can email/password login be triggered?
    val canLogin = remember(email, password, isEmailValid, isPasswordValid, isLoading) {
        email.isNotBlank() &&
                password.isNotBlank() &&
                isEmailValid &&
                isPasswordValid &&
                !isLoading
    }

    // Google Sign-In configuration
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    val googleClient = GoogleSignIn.getClient(context, gso)
    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)!!
                Toast.makeText(context, "Google login successful：${account.email}", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            } catch (e: ApiException) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar("Google login failed：${e.statusCode}")
                }
            }
        } else {
            coroutineScope.launch {
                snackBarHostState.showSnackbar("Google login has been cancelled")
            }
        }
    }

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
            Text("Login", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(24.dp))

            // Email input box
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = Patterns.EMAIL_ADDRESS.matcher(it).matches()
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

            // Password input box
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isPasswordValid = it.length >= 6
                },
                label = { Text("Password") },
                isError = !isPasswordValid,
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
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

            Spacer(Modifier.height(24.dp))

            // Email/password login button
            Button(
                onClick = {
                    // Double verification to prevent accidental transmission of null values
                    if (!canLogin) {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("Please fill in a valid email address and a password of at least 6 digits")
                        }
                        return@Button
                    }
                    isLoading = true
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            isLoading = false
                            onLoginSuccess()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar("Login failed：${e.localizedMessage}")
                            }
                        }
                },
                enabled = canLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Log In")
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Sign up")
            }

            Spacer(Modifier.height(24.dp))

            // Google login button
            Button(
                onClick = { googleLauncher.launch(googleClient.signInIntent) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("Sign in with Google")
            }
        }
    }
}
