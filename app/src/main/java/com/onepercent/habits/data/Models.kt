package com.onepercent.habits.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val question: String = "",
    /** Session start — shown on home timeline */
    val startTime: String,
    /** Session end — shown on home timeline */
    val endTime: String = "",
    /** When the notification fires (independent of start/end) */
    val reminderTime: String,
    /** CSV of weekday ints: 0=Sun … 6=Sat */
    val reminderDays: String = "0,1,2,3,4,5,6",
    val color: Long = 0xFF3B82F6,
)

@Entity(tableName = "logs", primaryKeys = ["dateKey", "habitId"])
data class LogEntity(
    val dateKey: String,
    val habitId: String,
    val status: String,
    val partialPct: Int = 0,
    val reasons: String = "",
    val note: String = "",
)
