# Demo Script

Step-by-step walkthrough for demonstrating GlanceKit alpha.

## Prerequisites

- Android device or emulator running API 26+
- Expo example app built and installed (see CONTRIBUTING.md)

## Steps

### 1. Launch the Expo Example App

Start the dev server and open the app on the device.

```bash
cd examples/expo-app
npx expo start --clear --dev-client
```

### 2. Add the Widget to the Home Screen

- Long-press the Android home screen.
- Tap "Widgets".
- Find "Progress Card Widget" under the app name.
- Drag it onto the home screen.
- The widget should appear with default/empty state.

### 3. Update the Widget from the App

- Open the Expo example app.
- Tap the "Update Widget" button.
- Switch to the home screen.
- The widget should now show the updated title, subtitle, and progress value.

### 4. Show Invalid Progress Error

- In the app code or a debug console, call:

```ts
await AndroidWidgets.updateWidget('progress-demo', {
  title: 'Test',
  subtitle: 'Invalid',
  progress: 101,
});
```

- The call should reject with a clear validation error.
- The widget should remain unchanged (no crash).

### 5. Tap the Widget to Open the App

- Tap the widget on the home screen.
- The app should open via the configured deep link scheme.
- If a deep link path was set, verify it routes to the correct screen.

## What to Highlight

- Widget updates are driven by a single TypeScript API call.
- Validation happens on the native side with clear error messages.
- The Expo plugin handles all Android manifest and resource wiring automatically.
- No custom native code is required for Expo apps.
