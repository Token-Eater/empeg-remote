package com.chasinglemons.empeg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.Toast;

/** Local, permission-free information for support reports. */
final class AboutDialog {
    private AboutDialog() {}

    static void show(Activity activity) {
        final String details = details(activity);
        AlertDialog dialog = new AlertDialog.Builder(activity,
                android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle(R.string.about_title)
                .setMessage(details)
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton(R.string.copy_details, null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager)
                    activity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(
                    activity.getString(R.string.about_title), details));
            // Android 13 and later provide their own clipboard confirmation.
            if (Build.VERSION.SDK_INT < 33) {
                Toast.makeText(activity, R.string.details_copied, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String details(Activity activity) {
        String version;
        try {
            PackageInfo info = activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0);
            long code = Build.VERSION.SDK_INT >= 28 ? info.getLongVersionCode() : info.versionCode;
            version = info.versionName + " (" + code + ")";
        } catch (PackageManager.NameNotFoundException e) {
            version = "Unavailable";
        }
        DisplayMetrics display = activity.getResources().getDisplayMetrics();
        return "Empeg Remote " + version
                + "\nPackage: " + activity.getPackageName()
                + "\n\nDevice: " + Build.MANUFACTURER + " " + Build.MODEL
                + "\nAndroid: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")"
                + "\nSecurity patch: " + (Build.VERSION.SDK_INT >= 23 ? Build.VERSION.SECURITY_PATCH : "Unavailable")
                + "\nOS build: " + Build.DISPLAY
                + "\nArchitectures: " + android.text.TextUtils.join(", ", Build.SUPPORTED_ABIS)
                + "\n\nApp display: " + display.widthPixels + " x " + display.heightPixels + " px"
                + "\nDensity: " + display.densityDpi + " dpi"
                + "\nFont scale: " + activity.getResources().getConfiguration().fontScale
                + "\nLocale: " + activity.getResources().getConfiguration().locale.toLanguageTag()
                + "\n\nBased on suomi35/empeg-remote. Updates by Token-Eater."
                + "\nLicense: GPL-3.0"
                + "\nhttps://github.com/Token-Eater/empeg-remote";
    }
}
