# @glancekit/android-core

Pure Android core module for GlanceKit alpha.

This module owns the pure Android implementation:

- `ProgressCardData`
- `WidgetStateRepository`
- `WidgetUpdateManager`
- `ProgressCardWidget`
- `ProgressCardWidgetReceiver`

It intentionally does not depend on React Native.

## Intended Use

Most React Native and Expo consumers should install:

- `@glancekit/react-native`
- `@glancekit/expo-plugin` for Expo managed builds

This package exists so the Android implementation has a clean boundary and can be consumed directly by:

- the native Android demo in `examples/native-android-demo`
- `@glancekit/react-native` as a bridge dependency

## Alpha Notes

- Android only
- internal/power-user package for alpha
- `ProgressCardWidget` is the only widget template
- no React Native JS API is exposed from this package
