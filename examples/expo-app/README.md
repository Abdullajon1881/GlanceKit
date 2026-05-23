# Expo Example

Managed Expo development-build example for GlanceKit.

## What This Example Proves

- `@glancekit/react-native` works inside an Expo Android development build
- `@glancekit/expo-plugin` generates the Android widget wiring during `expo prebuild`
- `AndroidWidgets.updateWidget(...)` updates the home-screen widget through the shared native module
- DataStore-backed widget state renders correctly through Glance

## Clean Install

From `examples/expo-app`:

```powershell
Remove-Item -LiteralPath .\node_modules -Recurse -Force
Remove-Item -LiteralPath .\package-lock.json -Force
npm install
```

If Metro still resolves stale workspace paths, run the clean install again after closing Metro.

## Prebuild

```powershell
npx expo prebuild --clean --platform android
```

This applies `@glancekit/expo-plugin` and writes the Android widget receiver, provider XML, preview drawable, loading layout, and string resources.

## Build The Android App

```powershell
cd android
.\gradlew.bat :app:assembleDebug
```

## Metro And Dev Client Setup

From `examples/expo-app`:

```powershell
npx expo start --clear --dev-client
```

If `8081` is already in use:

```powershell
npx expo start --clear --dev-client --port 8082
```

For Android USB debugging, reverse the Metro port:

```powershell
adb reverse tcp:8081 tcp:8081
```

If you start Expo on another port, reverse that port instead.

## Widget Test Checklist

1. Install the Android development build.
2. Add `ProgressCardWidget` from the launcher widget picker.
3. Open the Expo app.
4. Press `Update Widget`.
5. Confirm title, subtitle, and progress text update on the widget.
6. Tap the widget and confirm the deep link opens the app.
7. Enter `101` for progress and confirm the app shows a readable validation error.

## Known Alpha Limitations

- `ProgressCardWidget` is the only supported widget template.
- Progress is rendered as text only.
- Non-numeric widget IDs such as `progress-demo` currently update all active `ProgressCardWidget` instances.
- The widget render path is native; Metro is required for the app UI in debug, not for the widget itself.

## Local Package Notes

This example consumes local workspace packages:

```json
{
  "dependencies": {
    "@glancekit/react-native": "file:../../packages/react-native",
    "@glancekit/expo-plugin": "file:../../packages/expo-plugin"
  }
}
```

On Windows, npm often installs these as junctions. The included `metro.config.js` is configured for workspace-aware resolution.

## Troubleshooting

- Metro load failures, port conflicts, and local package resolution issues are documented in [../../TROUBLESHOOTING.md](../../TROUBLESHOOTING.md).
- Native widget render issues should be debugged with Android logs for `GlanceKitReceiver` and `GlanceKitWidget`.
