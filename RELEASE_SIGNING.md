# Building and signing releases

Use JDK 17 and an Android SDK with platform 35. Set JAVA_HOME and ANDROID_HOME, and set sdk.dir in local.properties for your machine (the upstream repository's tracked local.properties may contain an obsolete developer path).

```powershell
.\gradlew.bat :app:assembleRelease :app:testReleaseUnitTest :app:lintRelease
```

The unsigned APK is app/build/outputs/apk/release/app-release-unsigned.apk. Sign it using Android SDK Build Tools apksigner and the permanent private release key, alias empeg-release:

```text
apksigner sign --ks <private-key.p12> --ks-key-alias empeg-release --ks-pass env:EMPEG_KEY_PASSWORD --out <release.apk> app/build/outputs/apk/release/app-release-unsigned.apk
apksigner verify --verbose --print-certs <release.apk>
```

Supply EMPEG_KEY_PASSWORD privately for the signing process; clear it afterward. Never commit a keystore or password. The public signing-certificate SHA-256 is recorded in RELEASE_VALIDATION.md. Keep a secure portable backup of the key and password; losing them prevents compatible updates. Increase versionCode for each release and keep the same key.

The maintainer's signing material is stored outside this repository. Its local README describes the Windows-encrypted password storage; that encrypted file is bound to the Windows account/machine and is not by itself a portable backup.

Original and development APK signatures differ from this release key. Users need one uninstall/reinstall to switch; subsequent releases signed with this key can update in place. A rebuilt APK signed with another key is not an update to the official tester APK.
