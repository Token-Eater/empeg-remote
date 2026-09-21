# Tester record

## Physical devices tested by Kieran

| Device reported by Android | Android | API | Installed app | Result |
| --- | --- | --- | --- | --- |
| Lenovo TB-8505F | 10 | 29 | 1.97.0-dev, version code 8, debug-signed | Automatic discovery, playlist loading and live track notification confirmed during setup. Kieran reported no issues in initial hands-on testing. |
| Lenovo TB330XU | 15 | 35 | 1.97.0-dev, version code 8, debug-signed | Normal installation, automatic discovery, playlist loading and notification permission confirmed during setup. Kieran reported no issues in initial hands-on testing. |

Both tablets ran the **same APK**, SHA-256 `AED766D2DEB2C60D34F79FB0DC9391DFCA5DCFC842F104D0B40E338B56E0CE75`. These were initial tests, not an exhaustive pass of every feature. Tablet firmware build numbers/security patch dates were not captured; neither tablet was connected when the release record was prepared. Device serial numbers are omitted from this public record.

**No physical Android phone has been tested. Kieran does not have one available.** Android 15 phone layout and playlist navigation were checked using an emulator display override, which does not replace physical-phone testing.

## Emulator and automated coverage

Android 10/API29 tablet (1024x600) and Android 15/API35 tablet (2560x1600): installation, real-player playlists, play/pause and volume changes. Android 15: live display, notification track/time, settings, message dialog with keyboard, phone layout and playlist tab. Network loss/recovery was simulated on Android 15: live status resumed without relaunch; interrupted playlist loads recovered with Retry.

Four status-parser unit tests and the standalone HTTP fixture cover field ordering, extra fields, HTTP errors, stalled headers, stale command expiry, command ordering and recovery. See MODERNIZATION.md for reproduction commands. Lint passes with existing warnings; it is not a clean bill of health for all legacy code.

## Release distinction

The tester APK is **1.97.0-beta.1, version code 9**, signed with a dedicated release key and built as a non-debuggable release. The two physical-tablet reports above apply to development build 8, not to a subsequent installation of this release. Release smoke-test results are recorded in RELEASE_VALIDATION.md.

## Please test

- State your device model, Android version, app version, empeg player/Hijack version and network arrangement.
- Discovery and manual IP entry; distinguish discovery failure from failure after connecting.
- Live display, playlists and nested playlists, play/pause, previous/next and volume.
- T9/fuzzy search, keyboard search, gestures and long presses.
- Notification buttons used repeatedly, app background/foreground, screen off/on and device rotation where supported.
- Network loss/recovery. Status/display should resume; an interrupted playlist load may need Retry. Old queued controls should not replay after a long stall.
- Streaming, longer listening sessions, physical phones, different resolutions and font sizes.

For a bug, include reproduction steps, expected/actual result, whether Retry or reopening helps, and a screenshot or relevant log if available. Do not post private device identifiers or unrelated log contents.
