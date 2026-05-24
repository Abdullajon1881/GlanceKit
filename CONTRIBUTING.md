# Contributing to GlanceKit

Thanks for your interest in GlanceKit. This guide covers everything you need to clone, build, test, and submit a pull request.

## Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| Node.js | 18+ | `node -v` |
| pnpm | 9+ | `pnpm -v` |
| JDK | 17 or 21 | `java -version` |
| Android SDK | API 34+ | `$ANDROID_HOME/platforms/` |
| Android emulator or device | — | `adb devices` |

> **pnpm is required.** This is a pnpm workspace monorepo. Do not use `npm install` or `yarn install` at the root — they will not resolve workspace dependencies correctly.

Install pnpm if you don't have it:

```bash
npm install -g pnpm
```

## 1. Fork and Clone

```bash
# Fork on GitHub, then:
git clone https://github.com/<your-username>/GlanceKit.git
cd GlanceKit
```

Add the upstream remote so you can sync later:

```bash
git remote add upstream https://github.com/Abdullajon1881/GlanceKit.git
```

## 2. Install Dependencies

```bash
pnpm install
```

This installs dependencies for all packages and examples in the workspace.

## 3. Repository Structure

```
GlanceKit/
├── packages/
│   ├── android-core/     Pure Kotlin widget engine (no RN dependency)
│   ├── react-native/     JS API + React Native Android bridge
│   ├── expo-plugin/      Expo config plugin (auto-wires Android manifest/resources)
│   ├── templates/        Widget template resources (placeholder)
│   └── cli/              CLI tooling (placeholder)
├── examples/
│   ├── expo-app/                Expo managed workflow
│   ├── bare-react-native/      Bare React Native
│   └── native-android-demo/    Standalone Android (no RN)
├── docs/
├── .github/
│   └── ISSUE_TEMPLATE/
├── CONTRIBUTING.md              ← you are here
├── TROUBLESHOOTING.md
├── CHANGELOG.md
└── README.md
```

### Package dependency graph

```
@glancekit/expo-plugin
    └── peers: expo >=51

@glancekit/react-native
    ├── depends on: @glancekit/android-core
    └── peers: react >=18, react-native >=0.74

@glancekit/android-core
    └── standalone (no JS runtime dependency)
```

## 4. Build and Test

### Native Android Demo (fastest way to verify core changes)

```bash
cd examples/native-android-demo

# Linux/macOS
./gradlew assembleDebug

# Windows
.\gradlew.bat assembleDebug
```

Install the APK, then long-press the home screen → Widgets → add **Progress Card Widget**.

### Bare React Native Example

Terminal 1 — build:

```bash
cd examples/bare-react-native
pnpm install
cd android

# Linux/macOS
./gradlew assembleDebug

# Windows
.\gradlew.bat assembleDebug
```

Terminal 2 — Metro:

```bash
cd examples/bare-react-native
pnpm start
```

Then install the APK and run:

```bash
adb reverse tcp:8081 tcp:8081
```

### Expo Example

```bash
cd examples/expo-app
pnpm install
npx expo prebuild --clean --platform android
cd android

# Linux/macOS
./gradlew :app:assembleDebug

# Windows
.\gradlew.bat :app:assembleDebug
```

Start the dev server:

```bash
cd examples/expo-app
npx expo start --clear --dev-client
```

### Verify package contents

Before submitting changes to any package, verify no unintended files leak into the tarball:

```bash
cd packages/react-native && npm pack --dry-run
```

Confirm that test sources (`src/test/`, `android/src/test/`) do **not** appear.

## 5. Making Changes

### Create a branch

```bash
git checkout -b feat/your-feature-name
```

Branch naming conventions:

| Prefix | Use for |
|--------|---------|
| `feat/` | New features |
| `fix/` | Bug fixes |
| `docs/` | Documentation only |
| `refactor/` | Code restructuring |
| `test/` | Adding or fixing tests |
| `chore/` | Build, CI, tooling |

### Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add WorkManager background refresh
fix: widget not updating when progress is 0
docs: add bare RN wiring guide
refactor: extract validation into shared utility
```

### Code style

- **TypeScript** for all JavaScript-side code
- **Kotlin** for all Android-side code
- Keep files under 400 lines where possible
- Prefer immutable data patterns
- Handle errors explicitly with clear messages — never silently swallow errors
- No hardcoded secrets or credentials

## 6. Submitting a Pull Request

1. **Sync with upstream** before pushing:

   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Push your branch:**

   ```bash
   git push -u origin feat/your-feature-name
   ```

3. **Open a PR** on GitHub against `Abdullajon1881/GlanceKit:main`.

4. **Fill in the PR template.** Every PR should include:

   - A summary of what changed and why
   - Which packages are affected
   - How you tested the changes (which example app, which device/emulator)
   - Whether it's a breaking change

5. **Verify before requesting review:**

   - [ ] All three example apps still build
   - [ ] `npm pack --dry-run` for any changed package looks correct
   - [ ] No test sources, build artifacts, or secrets in the diff
   - [ ] Commit messages follow conventional commits

## 7. What to Contribute

### Good first issues

Look for issues labeled [`good first issue`](https://github.com/Abdullajon1881/GlanceKit/labels/good%20first%20issue).

### Ideas welcome

- Bug reports with reproduction steps
- Documentation improvements
- New widget template proposals (open an issue first to discuss)
- Example app improvements
- Troubleshooting guide additions

### Alpha scope boundaries

The following are **intentionally out of scope** for the alpha:

- iOS support
- JSX or arbitrary component rendering inside widgets
- Background/periodic updates (WorkManager — planned)
- Animations, WebView, video, or complex gestures
- Additional widget templates beyond `ProgressCardWidget`

If you want to work on any of these, open an issue to discuss the approach first.

## 8. Publishing (Maintainers Only)

The root `package.json` is `private: true` — it is not published. Only `@glancekit/react-native` is published:

```bash
cd packages/react-native
npm publish --access public --tag alpha
```

## 9. Getting Help

- [Troubleshooting Guide](TROUBLESHOOTING.md)
- [Open an issue](https://github.com/Abdullajon1881/GlanceKit/issues/new/choose)

## License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).
