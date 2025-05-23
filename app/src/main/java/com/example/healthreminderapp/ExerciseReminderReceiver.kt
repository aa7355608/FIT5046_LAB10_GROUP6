package com.example.healthreminderapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.example.healthreminderapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ReminderDebug", "Alarm triggered")

        val channelId = "exercise_reminder"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Exercise Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("🏃 Time to Exercise!")
            .setContentText("Custom time: ${getTimeLabel()} Stay active!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        Log.d("ReminderDebug", "Sending notification...") // 第二步日志

        notificationManager.notify(1, notification)
    }


    private fun getTimeLabel(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}


