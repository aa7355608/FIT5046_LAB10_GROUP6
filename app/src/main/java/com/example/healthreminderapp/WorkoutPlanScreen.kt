package com.example.healthreminderapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.abs
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanScreen(navController: NavController) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("个性化运动计划", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        // 三个主要功能卡片
        // 设置目标卡片
        Card(
            onClick = { navController.navigate("workout_form") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("设置目标", style = MaterialTheme.typography.titleMedium)
                Text("点击输入您的健身目标和偏好")
            }
        }

        // 查看报告卡片
        Card(
            onClick = { navController.navigate("workout_report") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("查看周报告", style = MaterialTheme.typography.titleMedium)
                Text("查看您的运动总结和进度")
            }
        }

        // AI推荐卡片
        Card(
            onClick = { navController.navigate("workout_ai") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI推荐", style = MaterialTheme.typography.titleMedium)
                Text("根据您的运动时间获取建议")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 运动状态检测框
        @Composable
        fun WorkoutMotionStatusBox() {
            val context = LocalContext.current
            val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
            val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

            var status by remember { mutableStateOf("🔄 检测中...") }
            var lastMagnitude by remember { mutableStateOf(0f) }
            var message by remember { mutableStateOf("") }

            val funnyLines = listOf(
                "连树懒都说你太懒了",
                "你的能量可以发电！",
                "动作平稳如度假中的忍者",
                "跑步机可能会吃醋",
                "静如雕像...差不多！"
            )

            val backgroundColor by animateColorAsState(
                targetValue = if (status.contains("静止")) MaterialTheme.colorScheme.tertiaryContainer
                else MaterialTheme.colorScheme.errorContainer,
                label = "motionColor"
            )

            DisposableEffect(Unit) {
                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        val current = sqrt(x * x + y * y + z * z)
                        val delta = abs(current - lastMagnitude)
                        lastMagnitude = current

                        status = if (delta < 0.5f) "🧘 状态：静止" else "🏃 状态：运动中"
                        if (status.contains("静止")) {
                            message = funnyLines.random()
                        } else {
                            message = ""
                        }
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }

                sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
                onDispose { sensorManager.unregisterListener(listener) }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("运动检测", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(status, style = MaterialTheme.typography.bodyLarge)
                    if (message.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(message, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}