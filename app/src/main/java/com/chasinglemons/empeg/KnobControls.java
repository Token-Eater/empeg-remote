package com.chasinglemons.empeg;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.io.IOException;

/** Web-lite knob controls, shared by the phone and tablet layouts. */
final class KnobControls {
    private final SharedPreferences preferences;
    private final View press;
    private String heldPlayer;
    private boolean touchClick;

    KnobControls(View root, SharedPreferences preferences) {
        this.preferences = preferences;
        root.findViewById(R.id.remote_g2).setOnClickListener(v -> turn("KnobLeft"));
        root.findViewById(R.id.remote_g4).setOnClickListener(v -> turn("KnobRight"));
        press = root.findViewById(R.id.remote_g3);
        // Accessibility and keyboard activation produce an ordinary press/release pair.
        press.setOnClickListener(v -> { if (!touchClick) { down(); release(); } });
        press.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    v.setPressed(true);
                    down();
                    break;
                case MotionEvent.ACTION_UP:
                    release();
                    touchClick = true;
                    try { v.performClick(); } finally { touchClick = false; }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    release();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if (event.getActionIndex() == 0) release();
                    break;
            }
            return true;
        });
        press.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) {}
            public void onViewDetachedFromWindow(View v) { release(); }
        });
    }

    private String player() { return preferences.getString("activeEmpegIP", "none"); }

    private void turn(String button) {
        String ip = player();
        if (!"none".equals(ip)) send(ip, button, false);
    }

    private void down() {
        if (heldPlayer != null || "none".equals(player())) return;
        heldPlayer = player();
        send(heldPlayer, "Knob", false);
    }

    void release() {
        press.setPressed(false);
        if (heldPlayer == null) return;
        String ip = heldPlayer;
        heldPlayer = null;
        // Capture the original player even if discovery/settings switch the active IP.
        send(ip, "Knob.R", true);
    }

    private void send(String ip, String button, boolean release) {
        Runnable request = () -> {
            try { EmpegHttp.getText("http://" + ip + "/?NODATA&BUTTONRAW=" + button); }
            catch (IOException e) { Log.w("KnobControls", "Knob command failed: " + button, e); }
        };
        if (release) EmpegHttp.executeRelease(request);
        else EmpegHttp.COMMANDS.execute(request);
    }
}
