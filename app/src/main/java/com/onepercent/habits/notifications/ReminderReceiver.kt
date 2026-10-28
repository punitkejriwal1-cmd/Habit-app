package com.onepercent.habits.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.onepercent.habits.data.HabitRepository

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val habitId = intent.getStringExtra("habitId") ?: return
            val repo = HabitRepository(context.applicationContext)
            val habit = repo.getAllHabits().find { it.id == habitId } ?: return
            val today = HabitRepository.dateKey(0)
            NotificationHelper.showSticky(context, habit, today)
            NotificationHelper.schedule(context, habit)
        } catch (t: Throwable) {
            Log.e("TimetableReview", "ReminderReceiver", t)
        }
    }
}
