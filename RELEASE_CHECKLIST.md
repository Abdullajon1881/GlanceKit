# Release Checklist

## 0.1.0-alpha.0

- [ ] Run a clean build for `examples/native-android-demo`
- [ ] Run a clean build for `examples/bare-react-native/android`
- [ ] Run a clean build for `examples/expo-app/android`
- [ ] Add `ProgressCardWidget` on a real Android launcher
- [ ] Verify widget renders immediately after being added
- [ ] Verify widget updates from the native Android demo
- [ ] Verify widget updates from the bare React Native example
- [ ] Verify widget updates from the Expo development build
- [ ] Verify deep-link tap opens the app
- [ ] Verify invalid progress such as `101` shows a readable error and does not crash the widget
- [x] Confirm `glancekit` package contents with `npm pack --dry-run` (no test sources, includes plugin/)
- [ ] Review package versions
- [x] Update `CHANGELOG.md`
- [x] Prepare GitHub release notes
- [ ] Tag the alpha release only after runtime checks pass
