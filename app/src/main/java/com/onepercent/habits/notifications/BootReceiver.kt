package com.onepercent.habits.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.onepercent.habits.data.HabitRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val repo = HabitRepository(context.applicationContext)
            NotificationHelper.scheduleAll(context, repo.getAllHabits())
        } catch (t: Throwable) {
            Log.e("TimetableReview", "BootReceiver", t)
        }
    }
}
