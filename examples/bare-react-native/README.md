# Bare React Native Example

This example app wires the local `@glancekit/react-native` package into a bare React Native Android project.

## What It Demonstrates

- A React Native screen that updates `ProgressCardWidget`
- Native module registration through React Native autolinking
- Android widget manifest wiring in the example app
- Widget receiver registration in `AndroidManifest.xml`
- Deep link support for `glancekit://progress/...`

## JavaScript Call

The main screen calls:

```ts
AndroidWidgets.updateWidget('progress-demo', {
  title: 'Worker is coming',
  subtitle: 'Arriving in 12 min',
  progress: 72,
  deepLink: 'glancekit://progress/progress-demo',
});
```

At the current MVP stage, non-numeric widget IDs are treated as an alias that updates all added `ProgressCardWidget` instances.

## Android Wiring

The local package is consumed through `file:../../packages/react-native` and React Native autolinking.

Manual setup is still required for widget-specific Android wiring:

- `AndroidManifest.xml` registers the widget receiver
- `android/app/src/main/res/xml/progress_card_widget_info.xml` defines the provider metadata
- the app declares the deep link intent filter for `glancekit://progress/...`

## Build

From `examples/bare-react-native`:

```powershell
npm install
npm start
adb reverse tcp:8081 tcp:8081
cd android
.\gradlew.bat :app:assembleDebug
```

You can also rebuild from a clean state:

```powershell
cd android
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
```

To install the debug APK:

```powershell
cd android
.\gradlew.bat :app:installDebug
```

## Manual Runtime Verification

1. Make sure an Android emulator or device is available.
2. Install the app.
3. Add `Progress Card Widget` to the home screen.
4. Open the app.
5. Press `Update Widget`.
6. Confirm the widget updates.
7. Tap the widget and confirm the app opens through the deep link.
8. Enter `101` as progress and confirm the app shows a readable error.

## Runtime Debugging

If the app shows `Unable to load script`, the debug APK is running without Metro. Start Metro and forward port `8081`:

```powershell
npm start
adb reverse tcp:8081 tcp:8081
```

The Android widget render path is native and should render even when Metro is down. If the widget shows `Can't show content`, inspect GlanceKit logs:

```powershell
adb logcat | findstr GlanceKit
```

Current log tags:

- `GlanceKitReceiver`
- `GlanceKitWidget`
- `GlanceKitState`
- `GlanceKitUpdate`
- `GlanceKitModule`

## Notes

- Build verification passes with `.\gradlew.bat clean` and `.\gradlew.bat :app:assembleDebug`.
- Runtime verification remains manual if no device is connected.
- Runtime verification is currently marked manual pending in automation because device checks are intentionally skipped.
- The app does not implement JSX widget rendering.
- Widget business logic stays inside `WidgetUpdateManager`.
- Known issue: `"progress-demo"` currently updates all `ProgressCardWidget` instances.
- The widget receiver must remain `dev.glancekit.reactnative.android.widget.ProgressCardWidgetReceiver`.
