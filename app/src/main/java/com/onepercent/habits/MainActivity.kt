package com.onepercent.habits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.onepercent.habits.data.Habit
import com.onepercent.habits.ui.HabitEditorScreen
import com.onepercent.habits.ui.HabitsViewModel
import com.onepercent.habits.ui.HomeScreen
import com.onepercent.habits.ui.SettingsScreen
import com.onepercent.habits.ui.StatusScreen
import com.onepercent.habits.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    // Lazy so a factory failure does not kill class init
    private val vm: HabitsViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        try {
            vm.refreshAlarms()
        } catch (t: Throwable) {
            Log.e(TAG, "after permission", t)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContent {
                var dark by remember { mutableStateOf(true) }
                var screen by remember { mutableStateOf<Screen>(Screen.Home) }
                var bootError by remember { mutableStateOf<String?>(null) }

                AppTheme(darkTheme = dark) {
                    if (bootError != null) {
                        Box(
                            Modifier.fillMaxSize().padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Error:\n$bootError",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        when (val s = screen) {
                            Screen.Home -> HomeScreen(
                                vm = vm,
                                onAdd = { screen = Screen.Edit(null) },
                                onEdit = { screen = Screen.Edit(it) },
                                onOpenStatus = { id, date -> screen = Screen.Status(id, date) },
                                onSettings = { screen = Screen.Settings },
                            )
                            is Screen.Edit -> HabitEditorScreen(
                                existing = s.habit,
                                onSave = { id, name, q, start, end, remind, days, color ->
                                    try {
                                        vm.saveHabit(id, name, q, start, end, remind, days, color)
                                    } catch (t: Throwable) {
                                        Log.e(TAG, "save", t)
                                    }
                                    screen = Screen.Home
                                },
                                onDelete = { id ->
                                    try {
                                        vm.deleteHabit(id)
                                    } catch (t: Throwable) {
                                        Log.e(TAG, "delete", t)
                                    }
                                    screen = Screen.Home
                                },
                                onClose = { screen = Screen.Home },
                            )
                            is Screen.Status -> StatusScreen(
                                habitId = s.habitId,
                                dateKey = s.dateKey,
                                vm = vm,
                                onClose = { screen = Screen.Home },
                            )
                            Screen.Settings -> SettingsScreen(
                                dark = dark,
                                onToggleDark = { dark = it },
                                vm = vm,
                                onClose = { screen = Screen.Home },
                            )
                        }
                    }
                }
            }

            window.decorView.post {
                try {
                    requestNotifPermission()
                    vm.refreshAlarms()
                } catch (t: Throwable) {
                    Log.e(TAG, "post start", t)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "onCreate fatal", t)
            // Last-resort non-Compose UI so the process does not just die silently
            val tv = TextView(this).apply {
                text = "Timetable Review failed to start:\n\n${t.javaClass.simpleName}: ${t.message}\n\nCheck logcat tag TimetableReview"
                setTextColor(0xFFFFFFFF.toInt())
                setBackgroundColor(0xFF000000.toInt())
                setPadding(48, 48, 48, 48)
                textSize = 14f
            }
            setContentView(tv)
        }
    }

    private fun requestNotifPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        private const val TAG = "TimetableReview"
    }
}

private sealed class Screen {
    data object Home : Screen()
    data class Edit(val habit: Habit?) : Screen()
    data class Status(val habitId: String, val dateKey: String) : Screen()
    data object Settings : Screen()
}
