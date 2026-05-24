# GlanceKit

GlanceKit is an open-source Android home-screen widget toolkit for React Native and Expo, powered by Jetpack Glance.

It exists because Android widget development is still too native-heavy for React Native and Expo developers.

> **Alpha** (`0.1.0-alpha.0`) - Small API, intentionally scoped. Expect breaking changes before 1.0.

## Install

```bash
npm install glancekit
```

Add the plugin to `app.config.js`:

```js
plugins: [
  [
    'glancekit',
    {
      deepLinkScheme: 'myapp',
      deepLinkHost: 'progress',
    },
  ],
],
```

Rebuild:

```bash
npx expo prebuild --clean --platform android
```

## Use

```ts
import { AndroidWidgets } from 'glancekit';

await AndroidWidgets.updateWidget('my-widget', {
  title: 'Steps Today',
  subtitle: '7,432 of 10,000',
  progress: 74,
  deepLink: 'myapp://progress/my-widget',
});
```

That's it. The widget updates on the home screen.

## What happens under the hood

1. Your JavaScript call hits the native bridge.
2. The payload is validated (progress 0-100, non-empty title/subtitle).
3. DataStore persists the widget state.
4. Jetpack Glance renders the updated widget.

Invalid data rejects with a clear error message. The widget never crashes.

## API

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

## Bare React Native

No Expo? Manual Android wiring is required. See the [bare React Native example](examples/bare-react-native) and the [glancekit docs](packages/react-native/README.md) for the full setup.

## Package

One package, everything included:

| Package | What it does |
|---------|-------------|
| [`glancekit`](packages/react-native) | JS API, native bridge, Expo config plugin, and Kotlin widget engine |

## Examples

| Example | |
|---------|---|
| [`examples/expo-app`](examples/expo-app) | Expo managed workflow |
| [`examples/bare-react-native`](examples/bare-react-native) | Bare React Native |
| [`examples/native-android-demo`](examples/native-android-demo) | Standalone Android (no RN) |

## Limitations

This is an alpha. Some things are intentionally out of scope:

- **Android only.** iOS doesn't use Jetpack Glance.
- **One template.** `ProgressCardWidget` only. More are planned.
- **No JSX in widgets.** Android widgets can't render arbitrary React Native components. GlanceKit uses templates instead.
- **No background updates yet.** WorkManager integration is planned.
- **No animations, WebView, video, or complex gestures.**
- **Text-only progress.** No graphical progress bar in this alpha.

## Roadmap

- More widget templates
- Background refresh with WorkManager
- CLI scaffolding for widget wiring
- Per-instance widget targeting

## Contributing

```bash
git clone https://github.com/Abdullajon1881/GlanceKit.git
cd GlanceKit
pnpm install
```

> **pnpm is required.** Do not use `npm install` or `yarn install` at the root.

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full guide: building examples, running tests, branch conventions, and submitting pull requests.

## Links

- [Troubleshooting](TROUBLESHOOTING.md)
- [Contributing](CONTRIBUTING.md)
- [Changelog](CHANGELOG.md)

## License

[MIT](LICENSE)
