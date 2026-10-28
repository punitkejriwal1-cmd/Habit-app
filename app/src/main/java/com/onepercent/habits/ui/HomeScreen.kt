package com.onepercent.habits.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onepercent.habits.data.Habit
import com.onepercent.habits.data.HabitLog
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HabitsViewModel,
    onAdd: () -> Unit,
    onEdit: (Habit) -> Unit,
    onOpenStatus: (habitId: String, dateKey: String) -> Unit,
    onSettings: () -> Unit,
) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Timetable", fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.width(8.dp))
                        val todayKey = state.dateKeys.firstOrNull().orEmpty()
                        val done = state.habits.count { h ->
                            val status = state.logsByKey[todayKey]?.get(h.id)?.status
                            status == "done" || status == "partial"
                        }
                        Text(
                            "$done/${state.habits.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Time", modifier = Modifier.width(56.dp), style = MaterialTheme.typography.labelSmall)
                Text("Habit", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                state.dateKeys.forEach { key ->
                    val d = runCatching { LocalDate.parse(key) }.getOrNull()
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(44.dp)) {
                        Text(
                            d?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)?.uppercase().orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            d?.dayOfMonth?.toString().orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            if (state.habits.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No habits yet. Tap + to add one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn {
                    items(state.habits, key = { it.id }) { habit ->
                        HabitRow(
                            habit = habit,
                            dateKeys = state.dateKeys,
                            logsByKey = state.logsByKey,
                            onEdit = { onEdit(habit) },
                            onCell = { onOpenStatus(habit.id, it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitRow(
    habit: Habit,
    dateKeys: List<String>,
    logsByKey: Map<String, Map<String, HabitLog>>,
    onEdit: () -> Unit,
    onCell: (String) -> Unit,
) {
    val start = format12(habit.startTime)
    val end = if (habit.endTime.isNotBlank()) format12(habit.endTime) else ""
    val dur = durationLabel(habit.startTime, habit.endTime)
    val pill = Color(habit.color.toInt())
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onEdit).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.width(56.dp)) {
            Text(start, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (end.isNotEmpty()) {
                Text(end, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            }
        }
        Column(Modifier.weight(1f)) {
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(pill).padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(habit.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (dur.isNotEmpty()) {
                Text(dur, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            dateKeys.forEach { key ->
                StatusCell(logsByKey[key]?.get(habit.id)) { onCell(key) }
            }
        }
    }
}

@Composable
private fun StatusCell(log: HabitLog?, onClick: () -> Unit) {
    val bg = when (log?.status) {
        "done" -> Color(0xFF34D399UL)
        "partial" -> Color(0xFFFBBF24UL)
        "missed" -> Color(0xFFFB7185UL)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val fg = when (log?.status) {
        "done" -> Color(0xFF052E22UL)
        "partial" -> Color(0xFF1C1404UL)
        "missed" -> Color(0xFF2A0A12UL)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(bg).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (log?.status) {
            "done" -> Icon(Icons.Default.Check, null, tint = fg, modifier = Modifier.size(18.dp))
            "missed" -> Icon(Icons.Default.Close, null, tint = fg, modifier = Modifier.size(18.dp))
            "partial" -> Text("${log.partialPct.coerceAtLeast(10)}%", color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            else -> Text(".", color = fg)
        }
    }
}

fun format12(time24: String): String {
    val p = time24.split(":")
    if (p.size < 2) return time24
    var h = p[0].toIntOrNull() ?: return time24
    val m = p[1]
    val ampm = if (h >= 12) "pm" else "am"
    h = h % 12
    if (h == 0) h = 12
    return "$h:$m $ampm"
}

fun durationLabel(start: String, end: String): String {
    if (end.isBlank()) return ""
    val a = start.split(":").mapNotNull { it.toIntOrNull() }
    val b = end.split(":").mapNotNull { it.toIntOrNull() }
    if (a.size < 2 || b.size < 2) return ""
    var mins = b[0] * 60 + b[1] - (a[0] * 60 + a[1])
    if (mins <= 0) mins += 24 * 60
    val h = mins / 60
    val m = mins % 60
    return when {
        h == 0 -> "$m m"
        m == 0 -> "$h h"
        else -> "$h h $m m"
    }
}
