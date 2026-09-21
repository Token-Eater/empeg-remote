package com.chasinglemons.empeg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Small transport shared by polling and controls. Never retries a player command. */
public final class EmpegHttp {
    private static final int TIMEOUT_MS = 3000;
    private static final long QUEUED_REQUEST_MAX_AGE_MS = 3000;
    private static final ThreadLocal<Boolean> EXPIRED_REQUEST = new ThreadLocal<Boolean>();
    public static final Executor COMMANDS = new ExpiringExecutor();
    public static final Executor PLAYLISTS = new ExpiringExecutor();
    private static final int MAX_BYTES = 8 * 1024 * 1024;

    private EmpegHttp() {}

    private static final class ExpiringExecutor extends ThreadPoolExecutor {
        ExpiringExecutor() { super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()); }
        @Override public void execute(Runnable command) { super.execute(new ExpiringRunnable(command)); }
    }

    private static final class ExpiringRunnable implements Runnable {
        private final Runnable delegate;
        private final long submittedAt = System.nanoTime();
        ExpiringRunnable(Runnable delegate) { this.delegate = delegate; }
        @Override public void run() {
            if (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - submittedAt) > QUEUED_REQUEST_MAX_AGE_MS) {
                EXPIRED_REQUEST.set(Boolean.TRUE);
                try { delegate.run(); } finally { EXPIRED_REQUEST.remove(); }
                return;
            }
            delegate.run();
        }
    }

    public static String getText(String url) throws IOException {
        return new String(getBytes(url), "UTF-8");
    }

    public static byte[] getBytes(String url) throws IOException {
        if (Boolean.TRUE.equals(EXPIRED_REQUEST.get())) throw new IOException("Request expired while queued");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setReadTimeout(TIMEOUT_MS);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Connection", "close");
        try {
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) throw new IOException("HTTP " + status);
            try (InputStream input = connection.getInputStream();
                 ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) {
                    if (Thread.currentThread().isInterrupted()) throw new IOException("Request cancelled");
                    if (output.size() + count > MAX_BYTES) throw new IOException("Response too large");
                    output.write(buffer, 0, count);
                }
                return output.toByteArray();
            }
        } finally {
            connection.disconnect();
        }
    }
}
