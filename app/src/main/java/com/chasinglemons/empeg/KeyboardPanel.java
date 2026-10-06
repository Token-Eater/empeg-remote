package com.chasinglemons.empeg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/** Search keyboard overlay. Visibility is local to this screen, with no translated hit targets. */
public class KeyboardPanel extends LinearLayout {
    public interface OnPanelListener {
        void onPanelClosed(KeyboardPanel panel);
        void onPanelOpened(KeyboardPanel panel);
    }
    private View content;
    private Button handle;
    private OnPanelListener listener;

    public KeyboardPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setOnPanelListener(OnPanelListener listener) { this.listener = listener; }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        content = findViewById(R.id.panelContent);
        handle = findViewById(R.id.panelHandle);
        // Use a normal labelled button instead of the unexplained A bitmap.
        handle.setBackgroundResource(android.R.drawable.btn_default);
        handle.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
        handle.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        handle.setTextSize(12);
        handle.setTextColor(android.graphics.Color.BLACK);
        handle.setOnClickListener(view -> setOpen(!isOpen()));
        setOpen(false);
    }

    public boolean isOpen() { return content != null && content.getVisibility() == VISIBLE; }

    public void setOpen(boolean open) {
        content.setVisibility(open ? VISIBLE : GONE);
        handle.setText(open ? R.string.close_keyboard : R.string.search_keyboard);
        handle.setContentDescription(getContext().getString(open
                ? R.string.close_keyboard : R.string.search_keyboard));
        if (open) bringToFront();
        if (listener != null) {
            if (open) listener.onPanelOpened(this);
            else listener.onPanelClosed(this);
        }
    }
}
