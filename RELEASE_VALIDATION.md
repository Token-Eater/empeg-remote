# About dialog: 1.97.0-beta.3

[Feature preview and emulator screenshots](docs/releases/1.97.0-beta.3.md).

Validated on 2026-09-24. Version code 11; minimum API 21, target API 35.

- Added a shared native About dialog to the phone/tablet main menus and discovery menu. Copy details copies the displayed report without dismissing the dialog.
- Reports installed version/code, package, manufacturer/model, Android release/API, security patch, OS build, CPU architectures, app display size/density, font scale and locale; includes project credits/source URL.
- No new permissions or automatic uploads. No device serial, Android ID, accounts or network identifiers collected.
- assembleRelease, testReleaseUnitTest (4 tests), lintRelease: PASS; existing legacy lint warnings remain.
- Release-signed APK installed over existing releases on Android 10/API29 tablet emulator, Android 15/API35 emulator configured as a 720x1280 phone, and physical Lenovo TB330XU / Android 15.
- Dialog visually checked on both emulators and report values checked on the physical tablet. Phone dialog scrolling exercised. Copy details verified by pasting the full report into an unsent message and cancelling. Discovery-menu About opened successfully on Android 10.
- No physical Android phone tested. Physical tablet left with beta 3 installed and About open.

APK SHA-256: d7402f899146f62f4f9b02df0afd531225ff826157f0112c32c813810a364878

# Lens selector fix: 1.97.0-beta.2

Validated on 2026-09-23. Version code 10; minimum API 21, target API 35.

- Issue #1 reproduced on the Android 15/API35 tablet emulator: five blank preview rows.
- Replaced filename parsing and getIdentifier with TypedArray.getResourceId for the existing drawable array.
- All five previews visually verified on the Android 15 emulator and Lenovo TB330XU physical tablet (Android 15, serial HA220KRL).
- Physical tablet: selected green, reopened the dialog, force-stopped/restarted the app, and confirmed green remained selected. Restored blue afterwards.
- Signed beta 2 installed as an update on both devices, preserving settings and using the existing release certificate.
- assembleRelease, testReleaseUnitTest (4 tests), lintRelease: PASS. Legacy lint warnings remain. Final version-only rebuild passed.
- Android 10 and a physical phone were not retested for this patch. The reporter's stated Android 17/Pixel environment was not available.

APK SHA-256: f6f07dbeed9088fd6835b081ea26f1b1b95685c199b2f5235d670fa9a32b18cd

# Release validation: 1.97.0-beta.1

Validated on 2026-09-21. Version code 9; minimum API 21, target API 35. This is the release-signed, non-debuggable APK, separate from the development APK used for the physical tablet tests in TESTING.md.

- assembleRelease, testReleaseUnitTest (4 tests, no failures), lintRelease: PASS. Legacy lint warnings remain.
- APK signature verification: PASS, v1/v2/v3 signatures. Certificate SHA-256: 0102b424c37267c76d01a6f83e892d9a004d2379f354f4e05bbe6a770db61db3.
- Normal install and launch: PASS on Android 10/API29 and Android 15/API35 tablet emulators; no low-target-SDK bypass.
- Both release installs connected by manual IP to the physical empeg and loaded All Music, Historic Sounds and mer06 playlists.
- Android 15 notification permission accepted; neither installed package has the DEBUGGABLE flag.
- Physical tablets were not connected during release preparation. No claim is made that the signed beta itself has been tested on them yet. No physical phone tested.

APK SHA-256: 932f7220f2ee101650a27d2cf6180529b769e373391006e131f66992e0193cfc

Public source: release tag v1.97.0-beta.1. The repository still contains old upstream-generated build artifacts; they were not updated in the modernization commit. Use the release asset, not the historical APK under app/build/outputs/apk/app-debug.apk.
