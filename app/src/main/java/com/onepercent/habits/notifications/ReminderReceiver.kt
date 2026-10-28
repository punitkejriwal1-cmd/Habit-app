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
            val repo = HabitRepository(context)
            val habit = repo.getAllHabits().find { it.id == habitId } ?: return
            val today = HabitRepository.dateKey(0)

            // A completed/partial/missed mark suppresses today's reminder even if an
            // alarm was already queued before the user marked the habit.
            val alreadyMarked = repo.logs.value.any {
                it.habitId == habitId && it.dateKey == today
            }
            if (alreadyMarked) {
                NotificationHelper.cancel(context, habitId, today)
            } else {
                NotificationHelper.showSticky(context, habit, today)
            }

            // AlarmManager alarms are cleared by reboot and are one-shot.
            NotificationHelper.schedule(context, habit)
        } catch (t: Exception) {
            Log.e("TimetableReview", "ReminderReceiver", t)
        }
    }
}
