# Native Android Demo

Reference Android app for the GlanceKit alpha.

## What It Covers

- the shared Android widget module in `packages/android-core`
- a single Glance widget: `ProgressCardWidget`
- DataStore-backed widget state per widget instance
- validation through `WidgetUpdateManager`
- deep-link handling when the widget is tapped

## Run In Android Studio

1. Open `examples/native-android-demo`.
2. Sync Gradle.
3. Run the `app` configuration on a device or emulator.
4. Add `ProgressCardWidget` from the launcher widget picker.
5. Update the widget from the demo activity form.

## Command-Line Build

```powershell
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
```

## Update Flow

1. Build `ProgressCardData` from the form.
2. Call `WidgetUpdateManager.updateProgressCardWidget(...)`.
3. Validate title, subtitle, progress, and optional deep link.
4. Persist the state in Preferences DataStore.
5. Refresh the matching Glance widget instance.
6. Render the latest state from DataStore.

## Notes

- The widget is template-based, not a general React Native renderer.
- Progress is rendered as text for alpha stability.
- Shared validation tests live in `packages/react-native/android/src/test`.
- See [../../TROUBLESHOOTING.md](../../TROUBLESHOOTING.md) for environment and Gradle issues.
