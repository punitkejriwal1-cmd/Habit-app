package com.onepercent.habits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.onepercent.habits.data.Habit
import com.onepercent.habits.ui.HabitEditorScreen
import com.onepercent.habits.ui.HabitsViewModel
import com.onepercent.habits.ui.HomeScreen
import com.onepercent.habits.ui.SettingsScreen
import com.onepercent.habits.ui.StatusScreen
import com.onepercent.habits.ui.theme.OnePercentTheme

class MainActivity : ComponentActivity() {
    private val vm: HabitsViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotifPermission()
        vm.refreshAlarms()

        setContent {
            var dark by remember { mutableStateOf(true) }
            var screen by remember { mutableStateOf<Screen>(Screen.Home) }

            OnePercentTheme(darkTheme = dark) {
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
                            vm.saveHabit(id, name, q, start, end, remind, days, color)
                            screen = Screen.Home
                        },
                        onDelete = { id ->
                            vm.deleteHabit(id)
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

    override fun onResume() {
        super.onResume()
        vm.refreshAlarms()
    }

    private fun requestNotifPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private sealed class Screen {
    data object Home : Screen()
    data class Edit(val habit: Habit?) : Screen()
    data class Status(val habitId: String, val dateKey: String) : Screen()
    data object Settings : Screen()
}
