package dev.mehdizebhi.twitchtelegrambot.util;

import java.time.Duration;

public class TimeUtil {

    public static String formatUptime(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
}
