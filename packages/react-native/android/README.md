# GlanceKit Android Core

Reusable Android widget code for GlanceKit.

## Included

- `ProgressCardData`
- `WidgetUpdateManager`
- `GlanceKitModule`
- `GlanceKitPackage`
- Preferences DataStore-backed widget state
- Jetpack Glance `ProgressCardWidget`
- `ProgressCardWidgetReceiver`

## Responsibilities

- Validate progress card data.
- Persist state per `widgetId`.
- Refresh the targeted widget instance after writes.
- Render safe defaults when state is missing.
- Keep the React Native bridge thin by forwarding writes into `WidgetUpdateManager`.

## React Native Bridge

The first Android bridge skeleton is included and exposes:

- `updateWidget(widgetId: String, data: ReadableMap, promise: Promise)`

The bridge reads `title`, `subtitle`, `progress`, and optional `deepLink`, then delegates to `WidgetUpdateManager`.

## Not Included Yet

- Expo config plugin integration
- Additional widget templates
