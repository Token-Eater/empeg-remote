package com.chasinglemons.empeg;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Persistent controls for the external player, with polling independent of control requests. */
public class NotificationService extends Service {
    public static final int NOTIFICATION_PERMISSION_REQUEST = 1001;

    /** Called from a visible activity; permission result is handled by that activity. */
    public static void start(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33 && activity.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST);
            return;
        }
        Intent service = new Intent(activity, NotificationService.class);
        if (Build.VERSION.SDK_INT >= 26) activity.startForegroundService(service);
        else activity.startService(service);
    }

    private static final String CHANNEL = "player_controls";
    private static final int NOTIFICATION_ID = 1;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ScheduledExecutorService polling = Executors.newSingleThreadScheduledExecutor();
    private SharedPreferences config;
    private NotificationManager notifications;
    private RemoteViews controls;
    private PendingIntent openApp;
    private volatile boolean destroyed;
    private boolean pollingStarted;

    @Override public void onCreate() {
        super.onCreate();
        config = PreferenceManager.getDefaultSharedPreferences(this);
        notifications = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            notifications.createNotificationChannel(new NotificationChannel(CHANNEL,
                    "Player controls", NotificationManager.IMPORTANCE_LOW));
        }
        openApp = PendingIntent.getActivity(this, 0,
                new Intent(this, Start.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                pendingFlags());
        controls = new RemoteViews(getPackageName(), R.layout.custom_notification);
        String[] buttons = {"Top", "Left", "Right", "Bottom"};
        int[] ids = {R.id.imageButton1, R.id.imageButton2, R.id.imageButton3, R.id.imageButton4};
        for (int i = 0; i < buttons.length; i++) {
            Intent command = new Intent(this, NotificationService.class).setAction(buttons[i]);
            PendingIntent click = Build.VERSION.SDK_INT >= 26
                    ? PendingIntent.getForegroundService(this, i + 1, command, pendingFlags())
                    : PendingIntent.getService(this, i + 1, command, pendingFlags());
            controls.setOnClickPendingIntent(ids[i], click);
        }
        startForeground(NOTIFICATION_ID, notification("Connecting to player…"));
    }

    private static int pendingFlags() {
        return PendingIntent.FLAG_UPDATE_CURRENT
                | (Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (!config.getBoolean("doNotifications", true) || "none".equals(playerIP())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        String button = intent == null ? null : intent.getAction();
        if ("Top".equals(button) || "Left".equals(button)
                || "Right".equals(button) || "Bottom".equals(button)) {
            final String url = "http://" + playerIP() + "/proc/empeg_notify?button=" + button;
            EmpegHttp.COMMANDS.execute(() -> {
                if (destroyed) return;
                try {
                    EmpegHttp.getText(url);
                } catch (IOException e) {
                    showText("Player unavailable — retrying…");
                }
            });
        }
        if (!pollingStarted) {
            pollingStarted = true;
            // Delay starts after completion, so slow requests never build a backlog.
            polling.scheduleWithFixedDelay(this::poll, 0, 1, TimeUnit.SECONDS);
        }
        return START_NOT_STICKY;
    }

    private String playerIP() {
        return config.getString("activeEmpegIP", "none");
    }

    private void poll() {
        if (destroyed) return;
        final String ip = playerIP();
        try {
            String text = PlayerStatus.parse(EmpegHttp.getText("http://" + ip
                    + "/proc/empeg_notify")).displayText();
            if (ip.equals(playerIP())) showText(text);
        } catch (IOException e) {
            showText("Player unavailable — retrying…");
        }
    }

    private void showText(String text) {
        if (destroyed) return;
        handler.post(() -> {
            if (!destroyed) notifications.notify(NOTIFICATION_ID, notification(text));
        });
    }

    private Notification notification(String text) {
        controls.setTextViewText(R.id.notification_title, "Empeg Remote");
        controls.setTextViewText(R.id.notification_text, text);
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        builder.setContentTitle("Empeg Remote").setContentText(text)
                .setSmallIcon(R.drawable.player_white).setContentIntent(openApp)
                .setOnlyAlertOnce(true).setOngoing(true).setShowWhen(false);
        if (Build.VERSION.SDK_INT >= 24) {
            builder.setCustomBigContentView(controls)
                    .setStyle(new Notification.DecoratedCustomViewStyle());
        }
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT < 24) notification.bigContentView = controls;
        return notification;
    }

    @Override public void onDestroy() {
        destroyed = true;
        polling.shutdownNow();
        handler.removeCallbacksAndMessages(null);
        stopForeground(true);
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    /** Retained for pending broadcasts created by earlier app versions. */
    public static class NotifButtonListener extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            String oldAction = intent.getStringExtra("action");
            String button = "up".equals(oldAction) ? "Top" : "left".equals(oldAction) ? "Left"
                    : "right".equals(oldAction) ? "Right" : "down".equals(oldAction) ? "Bottom" : null;
            if (button == null) return;
            Intent service = new Intent(context, NotificationService.class).setAction(button);
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(service);
            else context.startService(service);
        }
    }
}
