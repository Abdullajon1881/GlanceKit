# Contributing to GlanceKit

Thanks for your interest in contributing. This guide covers the local development setup and how to build and test the project.

## Prerequisites

- Node.js 18+
- pnpm 8+
- JDK 17 or 21
- Android SDK with API 34+
- Android emulator or physical device

## Repository Structure

```
packages/
  android-core/    Pure Android Glance/DataStore module (Kotlin)
  react-native/    React Native bridge (TypeScript + Kotlin)
  expo-plugin/     Expo config plugin (TypeScript)
  templates/       Widget template resources (placeholder)
  cli/             CLI tooling (placeholder)
examples/
  native-android-demo/    Standalone Android app
  bare-react-native/      Bare React Native example
  expo-app/               Expo managed example
docs/
```

## Local Setup

```bash
git clone https://github.com/user/glancekit.git
cd glancekit
pnpm install
```

## Building the Native Android Demo

```bash
cd examples/native-android-demo
./gradlew assembleDebug
```

Install the APK on a device or emulator, then add the `ProgressCardWidget` from the launcher widget picker.

## Building the Bare React Native Example

```bash
cd examples/bare-react-native
pnpm install
cd android
./gradlew assembleDebug
```

Start Metro in a separate terminal:

```bash
cd examples/bare-react-native
pnpm start
```

Then install the debug APK and run `adb reverse tcp:8081 tcp:8081`.

## Building the Expo Example

```bash
cd examples/expo-app
pnpm install
npx expo prebuild --clean --platform android
cd android
./gradlew :app:assembleDebug
```

Start the dev server:

```bash
cd examples/expo-app
npx expo start --clear --dev-client
```

## Running npm pack dry-run

Verify package contents before publishing:

```bash
cd packages/android-core && npm pack --dry-run
cd packages/react-native && npm pack --dry-run
cd packages/expo-plugin && npm pack --dry-run
```

Confirm no test sources (`src/test/`, `android/src/test/`) appear in the output.

## Alpha Limitations

This is an early alpha. The scope is intentionally narrow:

- Android only
- One widget template: `ProgressCardWidget`
- No JSX or arbitrary component rendering inside widgets
- No background/periodic updates yet
- No iOS support

## Submitting Changes

1. Fork the repository.
2. Create a feature branch from `main`.
3. Make your changes with clear commit messages.
4. Verify all three example apps still build.
5. Run `npm pack --dry-run` for any changed package.
6. Open a pull request with a description of what changed and why.

## Code Style

- TypeScript for all JavaScript-side code.
- Kotlin for all Android-side code.
- Keep files under 400 lines where possible.
- Prefer immutable data patterns.
- Handle errors explicitly with clear messages.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
