package com.onepercent.habits.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.onepercent.habits.HabitsApp

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val app = context.applicationContext as HabitsApp
        NotificationHelper.scheduleAll(context, app.repository.getAllHabits())
    }
}
