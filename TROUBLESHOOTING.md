# Troubleshooting

## JAVA_HOME Missing Or Wrong

Symptoms:

- Gradle fails before compilation
- `jlink` or JDK tools are missing
- Android builds work in Android Studio but fail from the terminal

Fix:

1. Install a full JDK 21.
2. Set `JAVA_HOME` to the JDK root, not a JRE.
3. Put `%JAVA_HOME%\\bin` first in `PATH` for the current shell.

PowerShell example:

```powershell
$env:JAVA_HOME = 'D:\GlanceKit\.jdks\temurin21\jdk-21.0.5+11'
$env:Path = "$env:JAVA_HOME\\bin;" + $env:Path
```

## Android SDK Location Missing

Symptoms:

- Gradle cannot find platform tools or build tools
- `local.properties` is missing or stale

Fix:

- set `ANDROID_HOME` or `ANDROID_SDK_ROOT`
- or add `sdk.dir=...` to `android/local.properties`

PowerShell example:

```powershell
$env:ANDROID_HOME = 'D:\Android\Sdk'
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
```

## Metro Unable To Load Script

Symptoms:

- red screen with `Unable to load script`
- debug APK opens but JavaScript never loads

Fix:

1. Start Metro.
2. Reverse the Metro port for Android USB debugging.
3. Reopen the app.

```powershell
npx react-native start --reset-cache
adb reverse tcp:8081 tcp:8081
```

Expo dev client:

```powershell
npx expo start --clear --dev-client
adb reverse tcp:8081 tcp:8081
```

## adb reverse

Use `adb reverse` for Android debug builds when the device cannot reach the local Metro host directly.

```powershell
adb reverse tcp:8081 tcp:8081
```

If Metro is on another port, reverse that port instead.

## Port 8081 Already In Use

Symptoms:

- Metro prompts for another port
- Expo start fails in non-interactive mode

Fix:

- stop the existing Metro process
- or start on another port explicitly

```powershell
npx expo start --clear --dev-client --port 8082
```

If you change the port, update `adb reverse` to match it.

## Metro Cannot Resolve glancekit

Symptoms:

- `Unable to resolve module glancekit`
- app works after copying files manually but fails with `file:` workspace dependencies

Cause:

- npm often installs local `file:` packages as Windows junctions
- Metro only searches the app root unless it is configured for monorepo resolution

Fix:

1. Ensure the app depends on `"glancekit": "file:../../packages/react-native"`.
2. Add a monorepo-aware `metro.config.js`.
3. Clean install dependencies.

Metro config pieces that matter:

- `watchFolders`
- `resolver.nodeModulesPaths`
- `resolver.extraNodeModules`
- `resolver.unstable_enableSymlinks = true`

## Widget Says Can't Show Content

Symptoms:

- widget appears in the picker but renders `Can't show content`

Most likely causes:

- receiver class mismatch in `AndroidManifest.xml`
- missing `appwidget-provider` metadata
- missing loading layout or preview drawable resources
- Glance render exception in `provideGlance`

Fix:

1. Verify the receiver points to `dev.glancekit.reactnative.android.widget.ProgressCardWidgetReceiver`.
2. Verify the provider XML exists and matches the manifest metadata.
3. Check logcat for:
   - `GlanceKitReceiver`
   - `GlanceKitWidget`
4. Remove and re-add the widget after manifest or resource changes.

## Widget Renders But Does Not Update

Symptoms:

- widget appears
- app reports success
- content stays stale

Checks:

1. Confirm `AndroidWidgets.updateWidget(...)` resolves without error.
2. Inspect `GlanceKitUpdate` and `GlanceKitState` logs.
3. Re-add the widget after large native changes.
4. Remember that non-numeric IDs such as `progress-demo` currently broadcast to all active `ProgressCardWidget` instances.

## Local file: Package And Windows Junction Issues

Symptoms:

- Metro resolves one run and fails the next
- package contents look correct but the bundler still cannot see them

Cause:

- local `file:` installs often become Windows junctions
- stale `node_modules` or stale Metro cache can hide updates

Fix:

```powershell
Remove-Item -LiteralPath .\node_modules -Recurse -Force
Remove-Item -LiteralPath .\package-lock.json -Force
npm install
```

Then restart Metro with a cleared cache.

## Gradle And Kotlin Cache Issues

Symptoms:

- Kotlin daemon crashes
- Gradle dependency accessors fail unexpectedly
- Expo Android builds fail after dependency changes even though source is correct

Fix options:

- delete the app-local `.gradle` directory
- use an isolated `GRADLE_USER_HOME`
- disable Kotlin incremental compilation for a one-off verification build

Examples:

```powershell
Remove-Item -LiteralPath .\android\.gradle -Recurse -Force
```

```powershell
$env:GRADLE_USER_HOME = 'D:\GlanceKit\examples\expo-app\.gradle-user-home'
.\gradlew.bat --no-daemon -Pkotlin.incremental=false :app:assembleDebug
```

## Expo Gradle Cache Hardening

For the Expo example, the most stable path on this machine was:

1. run expo prebuild`r
2. reuse a Gradle home that already contains the provisioned JDK 17 toolchain
3. run Gradle outside the sandbox when Windows temp-file locks interfere with Groovy DSL cache setup
4. quote the Kotlin property in PowerShell when invoking Gradle directly:

`powershell
'&lt;gradle.bat&gt;' --no-daemon '-Pkotlin.incremental=false' :app:assembleDebug
` 

Without quoting, PowerShell can split the property and Gradle may interpret .incremental=false as a task name.

## Expo Java 17 Toolchain Requirement

The Expo Android build may require a provisioned Java 17 toolchain for the Expo settings plugin even when JAVA_HOME points to JDK 21.

If Gradle fails with a Java 17 toolchain error:

- reuse a Gradle home that already contains the provisioned JDK 17 under jdks/`r
- or install a local JDK 17 and point Gradle toolchain detection at it
- or allow Gradle toolchain download if your environment permits it

