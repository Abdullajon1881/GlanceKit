# @glancekit/react-native

Android home-screen widgets for React Native and Expo, powered by Jetpack Glance.

One package — JS API, native bridge, Expo config plugin, and Kotlin widget engine.

## Install

```bash
npm install @glancekit/react-native
```

### Expo Setup

Add the plugin to `app.config.js`:

```js
plugins: [
  [
    '@glancekit/react-native',
    {
      deepLinkScheme: 'myapp',
      deepLinkHost: 'progress',
    },
  ],
],
```

Then rebuild:

```bash
npx expo prebuild --clean --platform android
```

### Bare React Native Setup

Bare React Native apps need manual Android widget wiring:

- Register `dev.glancekit.reactnative.android.widget.ProgressCardWidgetReceiver` in `AndroidManifest.xml`
- Add `android.appwidget.action.APPWIDGET_UPDATE` intent filter and `android.appwidget.provider` metadata
- Add `res/xml/progress_card_widget_info.xml`
- Add `res/layout/glancekit_widget_loading_layout.xml`
- Add `res/drawable/glancekit_progress_card_widget_preview.xml`
- Add a deep-link intent filter if widget taps should reopen the app

See [examples/bare-react-native](../../examples/bare-react-native) for a complete reference.

## API

```ts
import { AndroidWidgets } from '@glancekit/react-native';

await AndroidWidgets.updateWidget('my-widget', {
  title: 'Steps Today',
  subtitle: '7,432 of 10,000',
  progress: 74,
  deepLink: 'myapp://progress/my-widget',
});
```

### Types

```ts
type ProgressCardWidgetData = {
  title: string;
  subtitle: string;
  progress: number;     // 0-100
  deepLink?: string;
};

AndroidWidgets.updateWidget(
  widgetId: string,
  data: ProgressCardWidgetData
): Promise<void>;
```

## Error Handling

```ts
try {
  await AndroidWidgets.updateWidget('my-widget', { ... });
} catch (error) {
  console.error('Widget update failed', error);
}
```

Errors are clear and specific:
- Invalid payloads reject from the native layer
- Progress outside `0..100` rejects cleanly
- Empty `title` or `subtitle` rejects cleanly
- Missing native module rejects with a linking error

## Alpha Limitations

- Android only
- `ProgressCardWidget` template only
- No JSX or arbitrary component rendering in widgets
- No background updates yet
- Non-numeric widget IDs update all active widget instances

## Troubleshooting

See [TROUBLESHOOTING.md](../../TROUBLESHOOTING.md) for common issues.

## License

[MIT](../../LICENSE)
