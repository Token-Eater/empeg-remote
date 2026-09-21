# Empeg Remote 1.97.0-beta.1

First tester release of the Android modernization, based on suomi35/empeg-remote. One APK for Android phones and tablets; initial physical testing covers Android 10 and Android 15 tablets. Android 5/API21 is the declared minimum, but other versions are not yet verified.

## What changed

- Android 15 installation, system-bar layout and notification compatibility.
- HTTP connection/read timeouts: a stalled display fetch no longer holds up the command queue. Controls, playlists and display polling run independently; stale queued requests expire.
- Corrected notification parsing for the actual Hijack status response, including extra fields and different field ordering.
- Replaced unavailable legacy dialog libraries with Android's built-in dialog and number picker.

The **Discovery timeout** preference (default 2 seconds) is the UDP discovery receive timeout used while looking for players. It does not control HTTP requests after connecting. HTTP connect/read timeouts are 3 seconds each; queued requests older than 3 seconds expire before contacting the player. These are not a single total-request deadline.

## Installation

1. Download `empeg-remote-1.97.0-beta.1.apk` from this release. Both Android 10 and 15 use this same file.
2. If you have the original app or our 1.97.0-dev build, record any preferences you want to retain, then uninstall it: their signing keys differ. Uninstallation resets app preferences; it does not remove music from the empeg.
3. Install the APK using Android's prompt to allow installs from your chosen download/file app. This beta is distributed here, not through Google Play.
4. Connect the Android device and empeg to the same reachable network. The player needs Hijack with its HTTP interface enabled. Choose the discovered player or enter its IP address.
5. Allow notifications if you want persistent remote controls. Denying notification permission should still allow in-app use.

Future releases using this new release key and a higher version code can update this beta without another uninstall. Keep the release key secure and backed up.

## Testing status

Kieran found no issues in initial testing of development build 1.97.0-dev (8) on Lenovo TB-8505F / Android 10 and Lenovo TB330XU / Android 15. Those reports are not a physical-device validation of this newly signed beta. **No physical Android phone has been tested or is available to Kieran.** See TESTING.md and RELEASE_VALIDATION.md for exact coverage.

This is a prerelease for experienced empeg testers. In particular, test discovery, T9 search, streaming, repeated notification actions, network interruptions and longer sessions. Failed playlist loading may require Retry after connectivity returns.

Source is provided in this repository under the existing GPL-3.0 license; original credits and license remain intact. Use the tag corresponding to this release for its source. SHA256SUMS.txt identifies the downloadable APK.
