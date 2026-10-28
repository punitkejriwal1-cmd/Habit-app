# 1% Habits — Native Android (Kotlin)

Habit tracker built with **Kotlin + Jetpack Compose** for Android phones and tablets.

## Features

- Timeline home: start time, end time, colored name pill, duration
- Separate session times vs reminder time
- Sticky ongoing notifications (`AlarmManager` + `NotificationCompat`)
- Alarms rescheduled after reboot / app update
- Done / Partial / Missed marking
- OLED pure-black dark theme + light theme in Settings
- JSON export / import
- Room database (data survives APK updates)

## Build with Android Studio (recommended)

1. Install [Android Studio](https://developer.android.com/studio)
2. **File → Open** → select this repository folder
3. Wait for Gradle sync
4. Connect your phone (USB debugging) or use an emulator
5. Press **Run**, or **Build → Build APK(s)**

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Realme / battery tips

**Settings → Apps → 1% Habits → Battery → No restrictions**

## Package

`com.onepercent.habits`
