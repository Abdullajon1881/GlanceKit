# @glancekit/react-native

React Native Android bridge for GlanceKit.

## What You Get

This package exposes a small JavaScript API and ships the React Native Android bridge used by the bare React Native example and the Expo development-build example.

The pure Android widget implementation now lives in `packages/android-core`.

Current alpha scope:

- one widget template: `ProgressCardWidget`
- DataStore-backed widget state
- Glance-based widget rendering
- deep link support when the widget is tapped
- native validation through `WidgetUpdateManager`

## Installation

Published package shape:

```bash
npm install @glancekit/react-native
```

Monorepo example shape:

```json
{
  "dependencies": {
    "@glancekit/react-native": "file:../../packages/react-native"
  },
  "overrides": {
    "@glancekit/android-core": "file:../../packages/android-core"
  }
}
```

## API

```ts
export type ProgressCardWidgetData = {
  title: string;
  subtitle: string;
  progress: number;
  deepLink?: string;
};

export const AndroidWidgets = {
  updateWidget(widgetId: string, data: ProgressCardWidgetData): Promise<void>;
};
```

## Usage

```ts
import {AndroidWidgets} from '@glancekit/react-native';

await AndroidWidgets.updateWidget('progress-demo', {
  title: 'Worker is coming',
  subtitle: 'Arriving in 12 min',
  progress: 72,
  deepLink: 'glancekit://progress/progress-demo',
});
```

## Error Handling

Keep the JavaScript side thin and handle promise rejections directly:

```ts
try {
  await AndroidWidgets.updateWidget('progress-demo', {
    title: 'Worker is coming',
    subtitle: 'Arriving in 12 min',
    progress: 72,
  });
} catch (error) {
  console.error('Widget update failed', error);
}
```

Current alpha error behavior:

- invalid payloads reject from the native layer
- progress outside `0..100` rejects cleanly
- empty `title` or `subtitle` rejects cleanly
- if the Android native module is not linked, the JS API rejects with a clear linking error

## Android Wiring Notes

Bare React Native apps still need manual Android widget wiring.

Required pieces:

- register `dev.glancekit.reactnative.android.widget.ProgressCardWidgetReceiver` in `AndroidManifest.xml`
- add `android.appwidget.action.APPWIDGET_UPDATE` intent filter and `android.appwidget.provider` metadata
- add `res/xml/progress_card_widget_info.xml`
- add `res/layout/glancekit_widget_loading_layout.xml`
- add `res/drawable/glancekit_progress_card_widget_preview.xml`
- add a deep-link intent filter if widget taps should reopen the app by scheme
- include `:glancekit-android-core` in `settings.gradle`

Reference app:

- [examples/bare-react-native](../../examples/bare-react-native)

Expo apps should use `@glancekit/expo-plugin` instead of wiring these files by hand.

Internally, the Android bridge module depends on the shared `android-core` Gradle project.
For published installs, `@glancekit/react-native` depends on `@glancekit/android-core`.
For local monorepo development, the example apps pin `@glancekit/android-core` with file-based npm overrides.

## Troubleshooting

Common alpha issues:

- Metro cannot resolve `@glancekit/react-native` in a monorepo without a custom Metro config.
- Windows `file:` dependencies are often installed as junctions, so Metro needs workspace-aware `watchFolders` and resolver paths.
- Debug APKs and dev clients need Metro plus `adb reverse tcp:8081 tcp:8081` or they will fail to load JavaScript.
- If the widget renders but does not update, inspect Android logs for `GlanceKitReceiver`, `GlanceKitWidget`, `GlanceKitState`, `GlanceKitUpdate`, and `GlanceKitModule`.

See [TROUBLESHOOTING.md](../../TROUBLESHOOTING.md) for the full guide.

## Alpha Limitations

- Android only
- `ProgressCardWidget` only
- no JSX or arbitrary component rendering
- no background updates yet
- progress is text-only for now
- non-numeric IDs such as `progress-demo` update all active `ProgressCardWidget` instances in the current MVP
