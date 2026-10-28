# Timetable Review (native Android)

Kotlin + Jetpack Compose habit / timetable app.

- **App name:** Timetable Review
- **Package ID:** `com.timetablereview.app` (installs next to the old 1% Habits app)
- **Version:** 1.1.1

## Download APK

GitHub → **Actions** → latest green run → artifact **one-percent-habits-native**

Or build locally:

```bash
./gradlew :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Install on phone

1. Enable **Install unknown apps** for your browser / Files.
2. Install the APK.
3. Open the app once.

### Realme / OPPO / Xiaomi battery (important)

**Settings → Apps → Timetable Review → Battery → No restrictions** (or "Allow background activity").

Otherwise reminders are killed by the OEM battery optimizer.

### Notifications (Android 13+)

When the app opens it will ask for notification permission. Allow it for sticky habit reminders.

## Stability fixes in 1.1.1

- Replaced system drawable launcher icon (crashed / blank on many OEM launchers) with proper adaptive + legacy icons.
- Hardened exact-alarm scheduling so missing `SCHEDULE_EXACT_ALARM` grant no longer risks a crash.
- Safer Compose `Color` construction from stored Long values.
- Stable window theme for ColorOS / Realme splash path.
- Catalan `values-ca` strings for app name and notification channel.
