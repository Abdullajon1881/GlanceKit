# Changelog

## 0.1.0-alpha.0 - 2026-05-24

Initial GlanceKit alpha release. One package: `glancekit`.

### Added

- `ProgressCardWidget` template powered by Jetpack Glance
- `AndroidWidgets.updateWidget(widgetId, data)` JavaScript API
- DataStore-backed widget state persistence
- Deep link support when widget is tapped
- Input validation with clear error messages
- Built-in Expo config plugin that generates AndroidManifest receiver, widget provider XML, layout, preview drawable, and strings
- Native Android demo, bare React Native example, and Expo development-build example
- Alpha docs, troubleshooting guide, contributing guide, and release checklist

### Known Limitations

- Android only, no iOS support
- One widget template: `ProgressCardWidget`
- No JSX or arbitrary component rendering inside widgets
- No background/periodic updates (WorkManager integration planned)
- Progress is text-only (no `LinearProgressIndicator`)
- Non-numeric widget IDs update all active `ProgressCardWidget` instances
- No animations, WebView, video, or complex gestures
