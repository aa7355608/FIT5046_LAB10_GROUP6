package com.example.healthreminderapp.api

import kotlinx.serialization.Serializable

@Serializable
data class RemoteReminderSettings(
    val waterGoal: String,
    val waterInterval: String,
    val exerciseTime: String,
    val delayIfRaining: Boolean
)