package com.onepercent.habits.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.onepercent.habits.HabitsApp
import com.onepercent.habits.data.HabitRepository

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra("habitId") ?: return
        val app = context.applicationContext as HabitsApp
        val habit = app.repository.getAllHabits().find { it.id == habitId } ?: return
        val today = HabitRepository.dateKey(0)
        NotificationHelper.showSticky(context, habit, today)
        NotificationHelper.schedule(context, habit)
    }
}
