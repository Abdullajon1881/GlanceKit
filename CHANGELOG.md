# Changelog

## 0.1.0-alpha.0 - 2026-05-23

Initial GlanceKit alpha release.

### Packages

- `@glancekit/android-core` - Pure Android Glance/DataStore core module
- `@glancekit/react-native` - React Native bridge for Android widget updates
- `@glancekit/expo-plugin` - Expo config plugin for automatic Android wiring

### Added

- `ProgressCardWidget` template powered by Jetpack Glance
- `AndroidWidgets.updateWidget(widgetId, data)` JavaScript API
- DataStore-backed widget state persistence
- deep link support when widget is tapped
- input validation with clear error messages
- Expo config plugin that generates AndroidManifest receiver, widget provider XML, layout, preview drawable, and strings
- pure Android core module (`@glancekit/android-core`) extracted for standalone use
- native Android demo, bare React Native example, and Expo development-build example
- alpha docs, troubleshooting guide, contributing guide, and release checklist

### Changed

- `ProgressCardWidget` uses a stable text-first layout with safe fallback rendering
- package tarballs exclude test sources

### Known Limitations

- Android only, no iOS support
- one widget template: `ProgressCardWidget`
- no JSX or arbitrary component rendering inside widgets
- no background/periodic updates (WorkManager integration planned)
- progress is text-only (no `LinearProgressIndicator`)
- non-numeric widget IDs update all active `ProgressCardWidget` instances
- no animations, WebView, video, or complex gestures
