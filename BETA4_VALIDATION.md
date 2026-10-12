# Empeg Remote 1.98.0-beta.4

## Changes

- Adds KnobLeft, KnobPress and KnobRight beside Hijack, using the existing empty remote slots. Enable Settings > Show Hijack and knob buttons to show the row.
- Left/right send one web-lite step per tap. Hold KnobPress to hold the player knob; lifting sends its release. Leaving the screen or cancelling the touch also queues a release to the original player.
- Requests use `/?NODATA&BUTTONRAW=KnobLeft`, `KnobRight`, `Knob` and `Knob.R`. Commands stay ordered; release does not expire in the queue. Network timeouts still apply, with no automatic command retry.
- The display size and portrait/playlist navigation are unchanged.

## Validation - 12 October 2026

- Release build, lint and all four parser unit tests pass. Existing lint warnings remain.
- Standalone HTTP regression passes: response/error handling, timeout, stale-command expiry, release surviving queue expiry, ordering and recovery.
- Signed APK upgrades installed successfully on Android 10/API29 tablet and Android 15/API35 phone emulators, using the existing release certificate (v1/v2/v3 verified).
- A local HTTP recorder captured exact left/right commands and a single Knob/Knob.R pair for a tap and hold. Android 15 also verified explicit touch cancellation and leaving the app while held, each producing a release. Android 10 verified left/right and a one-second hold/release.
- Compact phone (720x1280, 320dpi): the full remote fits; Hijack and all three knobs occupy the same row above Keyboard. Tablet (1024x600, 160dpi): all four buttons remain accessible in the scrolling remote, ending at y=500 above Keyboard at y=504.
- No new app crash appeared during these checks. The screenshot shows the layout; live player operation was not verified in this run. Please test knob-only Hijack menus on hardware, particularly long presses.

Android 17 emulator testing remains limited by the previously documented system graphics crashes. Physical Android 17 testing of these new controls is still needed. Earlier tester feedback confirmed the beta 3 layout improvements.

Version 1.98.0-beta.4 (16), target API 37, minimum API 21. Install over earlier releases. Stable 1.97.0 remains the general release.
