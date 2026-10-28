package com.onepercent.habits

import android.app.Application
import android.util.Log
import com.onepercent.habits.notifications.NotificationHelper

class HabitsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            NotificationHelper.createChannel(this)
        } catch (t: Throwable) {
            Log.e("TimetableReview", "channel create failed", t)
        }
    }
}
