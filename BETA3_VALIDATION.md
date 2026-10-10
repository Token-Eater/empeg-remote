# Empeg Remote 1.98.0-beta.3

## Changes

- Modern phones now use the existing portrait remote with swipe/tabs for playlists. The old six-inch diagonal check incorrectly selected the landscape tablet layout on large phones; routing now uses Android configuration smallest width >= 600dp for tablets.
- Phone remote rows share the available height, displaying all buttons without a scrolling remote panel.
- Reserve space for Keyboard below the controls on phones and tablets, so it no longer covers Hijack. Opening the keyboard uses its own space; Close keyboard restores the full remote.
- Tablet side-by-side browsing is retained. Android may override orientation requests in multi-window/large-screen environments.

## Validation - 10 October 2026

Release build, lint and four parser tests pass (existing lint warnings remain). Signed APK upgrades on Android 10/API29 and Android 15/API35 emulators.

Android 15 phone configurations: 1080x2400 at 420dpi and 720x1280 at 320dpi. All seven remote rows, including Hijack, fit above Keyboard; no remote scrolling required. Swiping to playlists was verified. Keyboard open/close checked on compact layout. Portrait screenshot attached.

Android 10 tablet: side-by-side layout retained. After scrolling to Hijack, its bounds ended at y=500; Keyboard began at y=504, confirming no overlap. The tablet retains its scrolling remote layout.

Tester feedback confirms beta 2 font retention and notification controls work on their device. Beta 3 still needs physical Android 17 verification, including split-screen and small-window usability. No claim of minimum touch-target size on arbitrarily small split-screen windows; controls shrink to the available height.

Version 1.98.0-beta.3 (15), target API 37, minimum API 21. Install over earlier releases with the same signing key. Stable 1.97.0 remains the general release.
