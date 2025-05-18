@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun WeightTestScreen() {
    /* ---- 状态 ---- */
    var input by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var weightRecords by remember { mutableStateOf<List<Double>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val snack = remember { SnackbarHostState() }

    /* 首次加载历史记录 */
    LaunchedEffect(Unit) {
        weightRecords = HealthRepository.loadWeights()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Weight Tracker") }) },
        snackbarHost = { SnackbarHost(snack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /* ------ 输入区（不依赖 KeyboardOptions） ------ */
            OutlinedTextField(
                value = input,
                onValueChange = { text ->
                    // 仅允许数字和小数点
                    if (text.all { it.isDigit() || it == '.' }) input = text
                },
                label = { Text("Current Weight (kg)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            /* 保存按钮 */
            Button(
                onClick = {
                    val value = input.toDoubleOrNull()
                    if (value != null && value in 30.0..300.0) {
                        scope.launch {
                            submitting = true
                            HealthRepository.saveWeight(value)
                            weightRecords = HealthRepository.loadWeights()
                            submitting = false
                            input = ""
                        }
                    } else {
                        scope.launch { snack.showSnackbar("请输入合理体重") }
                    }
                },
                enabled = !submitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }

            Divider(modifier = Modifier.padding(top = 8.dp))

            /* ------ 历史列表 ------ */
            Text("History", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(weightRecords) { w ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f kg", w),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


