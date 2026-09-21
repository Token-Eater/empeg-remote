# Android 15 development build

Tester release: 1.97.0-beta.1 (9), min API 21, target/compile API 35. Original physical-device tests used 1.97.0-dev (8); see TESTING.md for the evidence and release-signing distinction.

## Changes

- Gradle 8.9, Android Gradle Plugin 8.7.3, JDK 17, Java 8 source compatibility. Replaced unavailable dialog libraries with platform AlertDialog and NumberPicker; retained support-v4 for existing fragments.
- Shared HttpURLConnection transport with 3-second connect/read timeouts, connection cleanup and 8 MiB response cap. These are per-operation socket timeouts, not an absolute total-download deadline.
- Ordered controls run independently of display polling and playlist loading. Queued requests older than 3 seconds fail before opening a connection. Expired AsyncTasks still complete through their normal error path.
- One active display request per activity instance, paused/stale results discarded. Playlist errors expose Retry instead of indexing an absent response.
- Notification permission, low-importance channel, connected-device foreground service and reusable immutable controls. Status parser accepts the player's actual field order and extra fields.
- Android 15 content insets preserve the framework action bar and keyboard space. Corrected legacy resource/manifest lint errors.

## Verified on 2026-09-21

- assembleDebug, testDebugUnitTest (4 parser tests), lintDebug pass. Lint still reports 526 warnings; these have not all been remediated.
- Standalone HTTP fixture passes: normal response, HTTP error, stalled-header timeout, stale-request expiry, FIFO command ordering and recovery.
- Normal APK installation on Android 10/API29 and Android 15/API35 emulators without low-target-SDK bypass. Existing emulator APKs had a different signature and were replaced.
- Both emulators connected to the real player at 192.168.116.127: playlists, play/pause, volume up/down. Player restored to volume 50 and paused.
- Android 15 live VFD screenshot and notification title/time confirmed. Settings discovery timeout shows 2 sec. Message dialog defaults to 5 seconds; Send is disabled for empty input and enabled after text entry, including with keyboard open.
- Android 15 network disabled and restored: status changes to unavailable and automatically recovers without relaunch. Interrupted playlist loads recover through Retry. This does not establish automatic playlist reloading.
- Android 15 phone layout and playlist tab also verified with temporary size/density override, then restored to tablet dimensions.
- Physical Lenovo TB-8505F (Android 10/API29) and Lenovo TB330XU (Android 15/API35) tested with 1.97.0-dev (8). Both discovered the player and loaded playlists; Kieran reported no issues in initial testing. No physical Android phone is available. Extended soak, full T9 search, streaming and repeated notification-button effects remain to be tested.

## Reproduce on this workstation (PowerShell)

```powershell
$env:JAVA_HOME = 'C:\Data\empeg\tools\jdk17'
$env:ANDROID_HOME = 'C:\Data\empeg\tools\android-sdk'
# Short existing directory avoids this workstation's Windows JDK Unix-domain socket startup failure.
$env:JAVA_TOOL_OPTIONS = '-Djdk.net.unixdomain.tmpdir=C:\Data\empeg\tools\java-tmp'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
& "$env:JAVA_HOME\bin\javac.exe" -d ..\tools\http-test-classes app/src/main/java/com/chasinglemons/empeg/EmpegHttp.java tests/EmpegHttpTest.java
& "$env:JAVA_HOME\bin\java.exe" -cp ..\tools\http-test-classes com.chasinglemons.empeg.EmpegHttpTest
```

APK: app/build/outputs/apk/debug/app-debug.apk. Reviewable copy: C:\Data\empeg\baseline\empeg-remote-1.97.0-dev.apk.

The upstream repository tracks generated build output despite ignoring it. Automatic approval review rejected removing those tracked files as a broad unrequested cleanup; they remain tracked. Review source/config changes separately from generated output before committing. This is a development/debug-signed APK, not a published release.
