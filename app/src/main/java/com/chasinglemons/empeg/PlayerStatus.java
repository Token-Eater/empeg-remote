package com.chasinglemons.empeg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The Hijack notification endpoint returns JavaScript assignments, not fixed-order records. */
final class PlayerStatus {
    private static final Pattern FIELD = Pattern.compile(
            "notify_(Artist|Title|FidTime)\\s*=\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*;");
    private static final Pattern TIME = Pattern.compile("(?:^|\\s)(\\d+:\\d{2}(?::\\d{2})?)(?:\\s|$)");
    final String artist;
    final String title;
    final String time;

    private PlayerStatus(String artist, String title, String time) {
        this.artist = artist;
        this.title = title;
        this.time = time;
    }

    static PlayerStatus parse(String response) {
        String artist = "", title = "", time = "";
        Matcher fields = FIELD.matcher(response == null ? "" : response);
        while (fields.find()) {
            String value = fields.group(2).replace("\\\"", "\"").replace("\\\\", "\\");
            switch (fields.group(1)) {
                case "Artist": artist = value; break;
                case "Title": title = value; break;
                case "FidTime":
                    Matcher clock = TIME.matcher(value);
                    if (clock.find()) {
                        time = clock.group(1);
                        if (time.startsWith("0:") && time.indexOf(':', 2) >= 0) time = time.substring(2);
                    }
                    break;
                default: break;
            }
        }
        return new PlayerStatus(artist, title, time);
    }

    String displayText() {
        String text = artist.isEmpty() ? title : title.isEmpty() ? artist : artist + " - " + title;
        if (text.isEmpty()) text = "Empeg connected";
        return time.isEmpty() ? text : text + " (" + time + ")";
    }
}
