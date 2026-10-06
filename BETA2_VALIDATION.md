# Empeg Remote 1.98.0-beta.2

API 37 tester update, version code 14. Minimum API 21 retained.

## Changes

- Replace the A tab with Keyboard / Close keyboard. Remove the animated drawer and shared static view references. Back dismisses the keyboard; on-screen Enter closes it and sends Menu. Letters map to numeric search keys; opening the keyboard does not start search on the player.
- Reapply the selected playlist font when rows resize. This mitigates the reported split-screen font change; confirmation on the tester device is still needed.
- Label notification buttons Up, Left, Right and Down, preserving their original directional commands. Correct light/dark contrast and control sizing.
- Rebuild notification RemoteViews per update to prevent setter actions accumulating indefinitely. Existing emulator logs showed TransactionTooLargeException during notification updates.
- Fix white discovered-player names on a light background using theme text colour.

## Validation - 6 October 2026

Release build, lint and four parser tests pass (legacy lint warnings remain). Signed APK upgrades on Android 10/API29 tablet and Android 15/API35 phone-sized emulators; version 1.98.0-beta.2 (14), target 37 verified.

Keyboard opening/closing checked on both layouts, including Android 15 Back and Android 10 on-screen Enter. Live display and playlists remained available. Pixel font retained when Android 10 display width changed from 1024 to 700 and back. Notification rendering reviewed in light/dark appearance; final dark-mode labels fit. No new AndroidRuntime crash during the short smoke test.

Limits: no physical device tested. The exact Android 17 split-screen transition and reported keyboard misbehaviour were not reproduced. Six photos supplied; the seventh and exact post-keyboard failure details were missing. Android 17 emulator graphics failures remain documented in API37_VALIDATION.md. Long-duration notification testing remains requested.

## Please retest

Check pixel font enabled/disabled when entering, resizing and leaving split screen. Repeatedly open/close Keyboard, type while the player is in search mode, press Enter and use the remote afterward. Check notifications in both themes and over an extended session, plus discovery name readability.

Report remaining problems with About - Copy details, reproduction steps and screenshots in https://github.com/Token-Eater/empeg-remote/issues.

Install over beta 1 or stable 1.97.0; signing key unchanged. Stable 1.97.0 remains recommended for general use. Downgrading normally requires uninstalling the beta, removing saved settings.
