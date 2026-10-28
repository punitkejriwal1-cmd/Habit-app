# Timetable Review — diagnostic build 1.1.3-diag

This build uses a **plain Android Activity** (no Compose UI on launch).

## What you should see

A dark screen with white text:

```
Timetable Review

v1.1.3-diag OK

If you see this, the app can open.
Next step: re-enable Compose UI.
```

## Tell Grok the result

1. **Opens and shows that text** → packaging is fine; crash was in Compose/ViewModel.
2. **Still crashes immediately** → packaging/manifest/OEM issue; reply with your phone model + Android version.

## Download

GitHub → Actions → latest green **Android APK** → artifact **one-percent-habits-native**

Uninstall the previous app before installing this one.
