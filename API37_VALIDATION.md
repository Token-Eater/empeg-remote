# API 37 compatibility build - 1.98.0-beta.1

Based on stable 1.97.0, on branch `android-api37`. Prepared for prerelease `v1.98.0-beta.1`; Android 17 physical-device testers are requested.

## Changes

- compile/target API 37; minimum API 21 retained. Version code 13.
- AGP 9.1.1 and Gradle 9.3.1, Java 17 build runtime.
- Android 17 local-network permission requested before entering the remote. Denial leaves an explanation with Retry and App settings. Returning from settings continues if access was granted. Restoring a remote from Recents after revocation returns to the permission screen; notification polling is guarded.
- AGP 9 compatibility option `android.enableAppCompileTimeRClass=false` retains the existing Java resource-ID switches. This and other legacy build flags should be revisited before AGP 10.
- Replaced the obsolete default ProGuard filename required by AGP 9; shrinking remains disabled.

## Validation

- Release APK builds; `testDebugUnitTest` passes all four parser tests. AGP 9 does not expose the previous release unit-test task by default.
- `lintRelease` passes with legacy warnings. The local SDK property required a correctly escaped drive separator; no lint errors were suppressed.
- Standalone EmpegHttp harness passes response handling, HTTP errors, stalled-header timeout, request expiry, command ordering and recovery.
- Final signed APK upgrades and launches on Android 10/API29 tablet and Android 15/API35 phone-sized emulators. Both load All Music, Historic Sounds and mer06 from the player.
- Android 15: About reports 1.98.0-beta.1 (13); five lens preview rows present.
- APK v1/v2/v3 signatures verified using the existing release key.
- No physical device tested for this build.

## Android 17 emulator investigation

Base API 37 Google APIs image revision 6, its 16KB revision 7, and the newer API 37.2 Google APIs 16KB revision 5 all crash in SurfaceFlinger before reliable app testing, with `Assertion failed: !rcEnc->featureInfo()->hasReadColorBufferDma`.

Upgraded the emulator runtime from 37.1.11 to 37.2.12. Tried software and host renderers and graphics transport feature overrides. These did not resolve the driver assertion. This is a virtual-device failure, not an observed Empeg Remote exception.

The API 37.2 image also failed APK installation while framework services restarted (StorageManagerService reported a null PackageManagerInternal). These failures occurred before the app could run. Tested on 4 October 2026 with emulator 37.2.12 on Windows.

Android 17 permission acceptance, revoked-permission recovery, large-screen rotation and notification control checks remain pending. Permission prompt, denial and retry were subsequently checked using the partial workaround below.

## Artifact

`empeg-remote-1.98.0-beta.1.apk`

SHA-256: `8478c34d26b5c5991061670f8416a3ad4e4db26eff3bab744c19d54cb74cbef7`

## References

- [API 37 tool requirements](https://developer.android.com/build/releases/about-agp)
- [Android 17 behaviour changes, including local-network permission](https://developer.android.com/about/versions/17/behavior-changes-17)

## Follow-up investigation - 5 October 2026

A [first-hand emulator test report](https://github.com/hajisensai/Fushi/blob/main/docs/agent/integration-testing.md) documents the same graphics assertion and a three-button navigation workaround. Applied to the API 37.2 AVD:

- Enabled `com.android.internal.systemui.navbar.threebutton` and disabled `com.android.internal.systemui.navbar.gestural` with `adb shell cmd overlay`.
- SurfaceFlinger stopped restarting during the observed two-minute test. APK installation and launch succeeded.
- Observed the Nearby devices permission prompt. Denial displayed the app's explanation, Retry and App settings; Retry reopened the permission prompt.
- After tapping Allow, Android's `system_server` aborted in `TaskSnapshotPer`, through `TaskSnapshotConvertUtil` and `GoldfishMapper::readFromHost` in `/vendor/lib64/hw/mapper.ranchu.so`, with the same assertion. Permission acceptance and subsequent app operation therefore remain unverified.
- This is a partial workaround for the emulator graphics readback problem, not a complete fix. No app code changed. It does not establish whether all host platforms or API 37 images are affected.
- Google's public emulator troubleshooting and release notes did not identify a confirmed fix for this exact assertion in the material checked. The listed API 37 Play Protect issue is a different failure.

Local crash evidence: `C:/Data/empeg/baseline/api37-threebutton-crash.txt`.