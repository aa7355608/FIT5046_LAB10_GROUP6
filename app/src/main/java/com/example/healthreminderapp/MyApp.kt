package com.example.healthreminderapp

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase Initialize
        Firebase.initialize(this)
    }
}
