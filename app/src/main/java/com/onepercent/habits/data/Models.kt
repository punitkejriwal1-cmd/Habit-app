package com.onepercent.habits.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val question: String = "",
    val startTime: String,
    val endTime: String = "",
    val reminderTime: String,
    val reminderDays: String = "0,1,2,3,4,5,6",
    val color: Long = 0xFF3B82F6L,
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
