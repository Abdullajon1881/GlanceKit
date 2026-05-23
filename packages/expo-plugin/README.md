# @glancekit/expo-plugin

Expo config plugin for GlanceKit Android widget wiring.

## What It Does

The plugin automates the Android setup that bare React Native apps currently do by hand.

It registers:

- `dev.glancekit.reactnative.android.widget.ProgressCardWidgetReceiver`
- the shared `:glancekit-android-core` Gradle project include for Expo-generated Android builds
- the `appwidget-provider` XML metadata
- the widget loading layout resource
- the widget preview drawable resource
- widget name and description strings
- the Android deep-link intent filter for the configured scheme and host

## Install

```bash
npm install @glancekit/react-native @glancekit/expo-plugin
```

## Plugin Config

```js
module.exports = {
  expo: {
    name: 'GlanceKit Expo Example',
    slug: 'glancekit-expo-app',
    scheme: 'glancekit',
    plugins: [
      [
        '@glancekit/expo-plugin',
        {
          deepLinkScheme: 'glancekit',
          deepLinkHost: 'progress',
          widgetName: 'Progress Card Widget',
          widgetDescription:
            'A GlanceKit progress widget powered by Jetpack Glance.',
        },
      ],
    ],
  },
};
```

## Generated Android Files

During `expo prebuild`, the plugin generates or updates:

- `android/settings.gradle`
- `android/app/src/main/AndroidManifest.xml`
- `android/app/src/main/res/xml/progress_card_widget_info.xml`
- `android/app/src/main/res/layout/glancekit_widget_loading_layout.xml`
- `android/app/src/main/res/drawable/glancekit_progress_card_widget_preview.xml`
- `android/app/src/main/res/values/strings.xml`

## Prebuild

From your Expo app:

```powershell
npm install
npx expo prebuild --clean --platform android
```

## Android Test Flow

```powershell
npx expo start --clear --dev-client
adb reverse tcp:8081 tcp:8081
cd android
.\gradlew.bat :app:assembleDebug
```

Then:

1. install the development build
2. add `ProgressCardWidget` from the launcher widget picker
3. open the Expo app
4. trigger `AndroidWidgets.updateWidget(...)`
5. confirm the widget updates and the deep link opens the app

## Common Issues

- If `expo prebuild` fails, verify `JAVA_HOME`, `ANDROID_HOME`, and the Android SDK installation.
- If Metro cannot resolve `@glancekit/react-native`, add a monorepo-aware `metro.config.js` with `watchFolders`, `nodeModulesPaths`, and `extraNodeModules`.
- If port `8081` is already in use, start Expo on another port such as `npx expo start --clear --dev-client --port 8082`.
- If the widget says `Can't show content`, inspect logcat for `GlanceKitReceiver` and `GlanceKitWidget`.

See [TROUBLESHOOTING.md](../../TROUBLESHOOTING.md) for the full issue guide.

## Scope

Current alpha scope is intentionally narrow:

- Android only
- one widget template: `ProgressCardWidget`
- no custom JSX or arbitrary component rendering

## Publish Behavior

After npm publishing:

- Expo apps install `@glancekit/react-native`
- `@glancekit/react-native` installs `@glancekit/android-core`
- the plugin wires `settings.gradle` to include the installed `@glancekit/android-core` package as `:glancekit-android-core`
