package com.onepercent.habits

import android.app.Application
import com.onepercent.habits.data.HabitRepository
import com.onepercent.habits.notifications.NotificationHelper

class HabitsApp : Application() {
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        try {
            repository = HabitRepository(this)
            NotificationHelper.createChannel(this)
        } catch (t: Throwable) {
            // Never crash the whole process during Application start
            t.printStackTrace()
            repository = HabitRepository(this)
        }
    }
}
