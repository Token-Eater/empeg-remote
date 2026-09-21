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
