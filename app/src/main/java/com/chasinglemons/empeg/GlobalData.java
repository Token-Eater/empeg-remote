package com.chasinglemons.empeg;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class GlobalData extends Application {
	
    @Override public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT < 35) return;
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            public void onActivityStarted(Activity activity) {
                // The framework action bar already includes its height in these insets.
                View content = activity.findViewById(android.R.id.content);
                content.setOnApplyWindowInsetsListener((view, insets) -> {
                    view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                            insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                    return insets.consumeSystemWindowInsets();
                });
                content.requestApplyInsets();
            }
            public void onActivityCreated(Activity a, Bundle b) {}
            public void onActivityResumed(Activity a) {}
            public void onActivityPaused(Activity a) {}
            public void onActivityStopped(Activity a) {}
            public void onActivitySaveInstanceState(Activity a, Bundle b) {}
            public void onActivityDestroyed(Activity a) {}
        });
    }

    public List<String> playlistHistory = new ArrayList<String>();
	
}
