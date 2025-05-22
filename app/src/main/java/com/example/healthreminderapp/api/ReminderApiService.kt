package com.example.healthreminderapp.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReminderApiService {

    @POST("api/reminders")
    suspend fun uploadSettings(@Body settings: RemoteReminderSettings)

    @GET("api/reminders/latest")
    suspend fun getLatestSettings(): RemoteReminderSettings
}