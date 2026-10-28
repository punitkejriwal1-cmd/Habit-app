package com.onepercent.habits.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

data class Habit(
    val id: String,
    val name: String,
    val question: String = "",
    val startTime: String,
    val endTime: String = "",
    val reminderTime: String,
    val reminderDays: String = "0,1,2,3,4,5,6",
    val color: Long = 0xFF3B82F6L,
)

data class HabitLog(
    val dateKey: String,
    val habitId: String,
    val status: String,
    val partialPct: Int = 0,
)

class HabitRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("timetable_review", Context.MODE_PRIVATE)
    private val _habits = MutableStateFlow(loadHabits())
    private val _logs = MutableStateFlow(loadLogs())

    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()
    val logs: StateFlow<List<HabitLog>> = _logs.asStateFlow()

    fun getAllHabits(): List<Habit> = _habits.value

    fun saveHabit(habit: Habit) {
        val list = _habits.value.toMutableList()
        val idx = list.indexOfFirst { it.id == habit.id }
        if (idx >= 0) list[idx] = habit else list.add(habit)
        list.sortBy { it.startTime }
        _habits.value = list
        persistHabits()
    }

    fun deleteHabit(id: String) {
        _habits.value = _habits.value.filter { it.id != id }
        _logs.value = _logs.value.filter { it.habitId != id }
        persistHabits()
        persistLogs()
    }

    fun setLog(log: HabitLog) {
        val list = _logs.value.toMutableList()
        list.removeAll { it.dateKey == log.dateKey && it.habitId == log.habitId }
        list.add(log)
        _logs.value = list
        persistLogs()
    }

    fun clearLog(dateKey: String, habitId: String) {
        _logs.value = _logs.value.filterNot { it.dateKey == dateKey && it.habitId == habitId }
        persistLogs()
    }

    fun exportJson(): String {
        val root = JSONObject()
        val hArr = JSONArray()
        _habits.value.forEach { h ->
            hArr.put(
                JSONObject()
                    .put("id", h.id)
                    .put("name", h.name)
                    .put("question", h.question)
                    .put("startTime", h.startTime)
                    .put("endTime", h.endTime)
                    .put("reminderTime", h.reminderTime)
                    .put("reminderDays", h.reminderDays)
                    .put("color", h.color),
            )
        }
        val lArr = JSONArray()
        _logs.value.forEach { l ->
            lArr.put(
                JSONObject()
                    .put("dateKey", l.dateKey)
                    .put("habitId", l.habitId)
                    .put("status", l.status)
                    .put("partialPct", l.partialPct),
            )
        }
        root.put("habits", hArr)
        root.put("logs", lArr)
        return root.toString(2)
    }

    fun importJson(raw: String) {
        val root = JSONObject(raw)
        val habits = mutableListOf<Habit>()
        val hArr = root.optJSONArray("habits") ?: JSONArray()
        for (i in 0 until hArr.length()) {
            val o = hArr.getJSONObject(i)
            habits.add(
                Habit(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    question = o.optString("question", ""),
                    startTime = o.optString("startTime", "09:00"),
                    endTime = o.optString("endTime", ""),
                    reminderTime = o.optString("reminderTime", "09:00"),
                    reminderDays = o.optString("reminderDays", "0,1,2,3,4,5,6"),
                    color = o.optLong("color", 0xFF3B82F6L),
                ),
            )
        }
        val logs = mutableListOf<HabitLog>()
        val lArr = root.optJSONArray("logs") ?: JSONArray()
        for (i in 0 until lArr.length()) {
            val o = lArr.getJSONObject(i)
            logs.add(
                HabitLog(
                    dateKey = o.getString("dateKey"),
                    habitId = o.getString("habitId"),
                    status = o.getString("status"),
                    partialPct = o.optInt("partialPct", 0),
                ),
            )
        }
        _habits.value = habits.sortedBy { it.startTime }
        _logs.value = logs
        persistHabits()
        persistLogs()
    }

    private fun loadHabits(): List<Habit> {
        val raw = prefs.getString("habits_json", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        Habit(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            question = o.optString("question", ""),
                            startTime = o.optString("startTime", "09:00"),
                            endTime = o.optString("endTime", ""),
                            reminderTime = o.optString("reminderTime", "09:00"),
                            reminderDays = o.optString("reminderDays", "0,1,2,3,4,5,6"),
                            color = o.optLong("color", 0xFF3B82F6L),
                        ),
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun loadLogs(): List<HabitLog> {
        val raw = prefs.getString("logs_json", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        HabitLog(
                            dateKey = o.getString("dateKey"),
                            habitId = o.getString("habitId"),
                            status = o.getString("status"),
                            partialPct = o.optInt("partialPct", 0),
                        ),
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persistHabits() {
        val arr = JSONArray()
        _habits.value.forEach { h ->
            arr.put(
                JSONObject()
                    .put("id", h.id)
                    .put("name", h.name)
                    .put("question", h.question)
                    .put("startTime", h.startTime)
                    .put("endTime", h.endTime)
                    .put("reminderTime", h.reminderTime)
                    .put("reminderDays", h.reminderDays)
                    .put("color", h.color),
            )
        }
        prefs.edit().putString("habits_json", arr.toString()).apply()
    }

    private fun persistLogs() {
        val arr = JSONArray()
        _logs.value.forEach { l ->
            arr.put(
                JSONObject()
                    .put("dateKey", l.dateKey)
                    .put("habitId", l.habitId)
                    .put("status", l.status)
                    .put("partialPct", l.partialPct),
            )
        }
        prefs.edit().putString("logs_json", arr.toString()).apply()
    }

    companion object {
        private val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        fun dateKey(offsetDays: Long = 0): String =
            LocalDate.now().plusDays(offsetDays).format(fmt)

        fun newId(): String = UUID.randomUUID().toString()
    }
}
