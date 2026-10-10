package com.chasinglemons.empeg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Start extends Activity {

    private static final int LOCAL_NETWORK_REQUEST = 1002;
    private boolean opened;

    static boolean canConnect(android.content.Context context) {
        return android.os.Build.VERSION.SDK_INT < 37 || context.checkSelfPermission(
                android.Manifest.permission.ACCESS_LOCAL_NETWORK)
                == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    @Override public void onResume() {
        super.onResume();
        if (canConnect(this)) openRemote();
    }

    @Override public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCAL_NETWORK_REQUEST) return;
        if (canConnect(this)) {
            openRemote();
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (!canConnect(this)) {
            setTitle(R.string.local_network_title);
            android.widget.LinearLayout content = new android.widget.LinearLayout(this);
            content.setOrientation(android.widget.LinearLayout.VERTICAL);
            int padding = (int) (24 * getResources().getDisplayMetrics().density);
            content.setPadding(padding, padding, padding, padding);
            android.widget.TextView explanation = new android.widget.TextView(this);
            explanation.setText(R.string.local_network_message);
            content.addView(explanation);
            android.widget.Button retry = new android.widget.Button(this);
            retry.setText(R.string.local_network_retry);
            retry.setOnClickListener(view -> requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_LOCAL_NETWORK}, LOCAL_NETWORK_REQUEST));
            content.addView(retry);
            android.widget.Button settings = new android.widget.Button(this);
            settings.setText(R.string.local_network_settings);
            settings.setOnClickListener(view -> startActivity(new Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    android.net.Uri.parse("package:" + getPackageName()))));
            content.addView(settings);
            setContentView(content);
            if (savedInstanceState == null) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_LOCAL_NETWORK},
                        LOCAL_NETWORK_REQUEST);
            }
            return;
        }
        openRemote();
    }

    private void openRemote() {
        if (opened) return;
        opened = true;

		if (isTablet()) {
			Intent tabVersion = new Intent(this, TabletMain.class);
			tabVersion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(tabVersion);
			finish();
		} else {
			Intent phoneVersion = new Intent(this, PhoneMain.class);
			phoneVersion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(phoneVersion);
			finish();
		}
	}

    public boolean isTablet() {
        // Physical diagonals misclassify modern large phones. Use Android's tablet breakpoint.
        return getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }
}
