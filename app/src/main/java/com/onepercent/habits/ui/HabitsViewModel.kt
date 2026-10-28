package com.onepercent.habits.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onepercent.habits.HabitsApp
import com.onepercent.habits.data.Habit
import com.onepercent.habits.data.HabitLog
import com.onepercent.habits.data.HabitRepository
import com.onepercent.habits.notifications.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val habits: List<Habit> = emptyList(),
    val logsByKey: Map<String, Map<String, HabitLog>> = emptyMap(),
    val dateKeys: List<String> = listOf(
        HabitRepository.dateKey(0),
        HabitRepository.dateKey(-1),
    ),
)

class HabitsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as HabitsApp).repository
    private val ctx = app.applicationContext

    private val dateKeys = listOf(
        HabitRepository.dateKey(0),
        HabitRepository.dateKey(-1),
    )

    val uiState = combine(repo.habits, repo.logs) { habits, logs ->
        val map = mutableMapOf<String, MutableMap<String, HabitLog>>()
        logs.forEach { log ->
            map.getOrPut(log.dateKey) { mutableMapOf() }[log.habitId] = log
        }
        HomeUiState(habits = habits, logsByKey = map, dateKeys = dateKeys)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun refreshAlarms() {
        NotificationHelper.scheduleAll(ctx, repo.getAllHabits())
    }

    fun saveHabit(
        id: String?,
        name: String,
        question: String,
        startTime: String,
        endTime: String,
        reminderTime: String,
        reminderDays: String,
        color: Long,
    ) {
        val entity = Habit(
            id = id ?: HabitRepository.newId(),
            name = name.trim(),
            question = question.trim(),
            startTime = startTime,
            endTime = endTime,
            reminderTime = reminderTime,
            reminderDays = reminderDays,
            color = color,
        )
        repo.saveHabit(entity)
        NotificationHelper.schedule(ctx, entity)
    }

    fun deleteHabit(id: String) {
        NotificationHelper.cancelSchedule(ctx, id)
        NotificationHelper.cancel(ctx, id, HabitRepository.dateKey(0))
        repo.deleteHabit(id)
    }

    fun mark(habitId: String, dateKey: String, status: String, partialPct: Int = 0) {
        repo.setLog(HabitLog(dateKey, habitId, status, partialPct))
        if (dateKey == HabitRepository.dateKey(0)) {
            NotificationHelper.cancel(ctx, habitId, dateKey)
        }
    }

    fun clearMark(habitId: String, dateKey: String) {
        repo.clearLog(dateKey, habitId)
    }

    fun exportJson(): String = repo.exportJson()

    fun importJson(raw: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repo.importJson(raw)
                NotificationHelper.scheduleAll(ctx, repo.getAllHabits())
                onDone(true)
            } catch (_: Exception) {
                onDone(false)
            }
        }
    }
}
