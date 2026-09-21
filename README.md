# Empeg Remote — Android tester builds

Modernization of [suomi35/empeg-remote](https://github.com/suomi35/empeg-remote). One APK supports phones and tablets; initial physical tests cover Android 10 and Android 15 tablets. No physical Android phone has been tested.

- [Tester release and installation](TESTER_RELEASE.md)
- [Device test record and checklist](TESTING.md)
- [Release validation](RELEASE_VALIDATION.md)
- [Implementation and build notes](MODERNIZATION.md)

The discovery timeout preference is separate from the HTTP timeouts introduced in this fork. Your player still needs Hijack and network connectivity.

## Original project notes

empeg-remote
============

Releasing the source since I no longer have time to work on it.

There is a precompiled version available on Google Play (https://play.google.com/store/apps/details?id=com.chasinglemons.empeg)

NOTE: Your player MUST have a recent version of the Hijack kernel installed (http://empeg-hijack.sourceforge.net)

This app allows you to control the Empeg on a phone or tablet and provides the full functionality of the original remote control as well as full playlist navigation and control. Colors can be customized to fit the lens color of your Empeg. The app also employs discovery to automatically find Empegs on the network.

Continued thanks and praise to the Empeg guys for giving us a great gift, which has lasted for 15+ years(!)
http://www.empeg.com
