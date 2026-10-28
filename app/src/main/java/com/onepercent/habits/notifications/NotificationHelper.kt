package com.onepercent.habits.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.onepercent.habits.MainActivity
import com.onepercent.habits.data.Habit
import java.util.Calendar

object NotificationHelper {
    private const val TAG = "TimetableReview"
    const val CHANNEL_ID = "habits_sticky"

    fun createChannel(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Habit reminders",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Habit reminders at the times you choose"
                }
                context.getSystemService(NotificationManager::class.java)
                    ?.createNotificationChannel(channel)
            }
        } catch (t: Exception) {
            Log.e(TAG, "createChannel", t)
        }
    }

    fun notificationId(habitId: String, dateKey: String): Int {
        var h = 0
        val s = "$habitId|$dateKey"
        for (c in s) h = 31 * h + c.code
        return h and 0x7fffffff
    }

    fun showSticky(context: Context, habit: Habit, dateKey: String) {
        try {
            createChannel(context)
            val id = notificationId(habit.id, dateKey)
            val open = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentPi = PendingIntent.getActivity(
                context, id, open,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(habit.name)
                .setContentText("Time for your habit")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setOngoing(true)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentPi)
                .build()
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (t: SecurityException) {
            // Notifications may be disabled by the user; do not crash the app.
            Log.w(TAG, "Notification permission unavailable", t)
        } catch (t: Exception) {
            Log.e(TAG, "showSticky", t)
        }
    }

    fun cancel(context: Context, habitId: String, dateKey: String) {
        try {
            NotificationManagerCompat.from(context).cancel(notificationId(habitId, dateKey))
        } catch (t: Exception) {
            Log.e(TAG, "cancel", t)
        }
    }

    fun schedule(context: Context, habit: Habit) {
        val triggerAt = nextTriggerMillis(habit) ?: run {
            cancelSchedule(context, habit.id)
            return
        }
        try {
            val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("habitId", habit.id)
            }
            val pi = PendingIntent.getBroadcast(
                context, habit.id.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    // Exact-alarm access is user-controlled on modern Android.
                    // Keep reminders working with an inexact alarm instead of silently losing them.
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (t: SecurityException) {
            Log.w(TAG, "Exact alarm denied; trying inexact alarm", t)
            try {
                val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("habitId", habit.id)
                }
                val pi = PendingIntent.getBroadcast(
                    context, habit.id.hashCode(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                }
            } catch (fallback: Exception) {
                Log.e(TAG, "Unable to schedule reminder", fallback)
            }
        } catch (t: Exception) {
            Log.e(TAG, "schedule", t)
        }
    }

    fun scheduleAll(context: Context, habits: List<Habit>) {
        habits.forEach { schedule(context, it) }
    }

    fun cancelSchedule(context: Context, habitId: String) {
        try {
            val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ReminderReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                context, habitId.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            am.cancel(pi)
            pi.cancel()
        } catch (t: Exception) {
            Log.e(TAG, "cancelSchedule", t)
        }
    }

    private fun nextTriggerMillis(habit: Habit): Long? {
        val parts = habit.reminderTime.split(":")
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull()?.takeIf { it in 0..23 } ?: return null
        val minute = parts[1].toIntOrNull()?.takeIf { it in 0..59 } ?: return null
        val days = habit.reminderDays.split(",")
            .mapNotNull { it.trim().toIntOrNull()?.takeIf { day -> day in 0..6 } }
            .toSet()
        if (days.isEmpty()) return null

        val now = Calendar.getInstance()
        val candidate = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        for (i in 0..7) {
            if (i > 0) candidate.add(Calendar.DAY_OF_YEAR, 1)
            val ourDow = candidate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
            if (ourDow in days && candidate.timeInMillis > now.timeInMillis + 2_000) {
                return candidate.timeInMillis
            }
        }
        return null
    }
}
