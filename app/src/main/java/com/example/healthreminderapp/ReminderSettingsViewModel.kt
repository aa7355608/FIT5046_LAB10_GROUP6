package com.example.healthreminderapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderSettingsViewModel : ViewModel() {


    private val _waterGoal = MutableStateFlow("2000")
    val waterGoal: StateFlow<String> = _waterGoal

    private val _waterInterval = MutableStateFlow("2 hours")
    val waterInterval: StateFlow<String> = _waterInterval

    private val _exerciseTime = MutableStateFlow("18:00")
    val exerciseTime: StateFlow<String> = _exerciseTime

    private val _delayIfRaining = MutableStateFlow(true)
    val delayIfRaining: StateFlow<Boolean> = _delayIfRaining

    // 更新状态（例如 FormScreen 调用）
    fun updateSettings(
        goal: String,
        interval: String,
        time: String,
        delay: Boolean
    ) {
        _waterGoal.value = goal
        _waterInterval.value = interval
        _exerciseTime.value = time
        _delayIfRaining.value = delay
    }

    // 从数据库加载最新设置（MainActivity 中使用）
    fun loadLatestFromDatabase(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val latest = db.reminderSettingsDao().getLatest()
            latest?.let {
                updateSettings(
                    goal = it.waterGoal,
                    interval = it.waterInterval,
                    time = it.exerciseTime,
                    delay = it.delayIfRaining
                )
            }
        }
    }
}
