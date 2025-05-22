package com.example.healthreminderapp.api

import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

object ApiClient {
    private const val BASE_URL = "https://your.api.url/"

    private val contentType = "application/json".toMediaType()

    val api: ReminderApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                }.asConverterFactory(contentType)
            )
            .build()
            .create(ReminderApiService::class.java)
    }
}
