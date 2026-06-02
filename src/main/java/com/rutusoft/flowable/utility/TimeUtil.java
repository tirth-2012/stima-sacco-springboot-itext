package com.rutusoft.flowable.utility;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
public class TimeUtil {

    public static String getTimeAgo(Date createTime) {
        if (createTime == null) return "-";

        Instant now = Instant.now();
        Instant created = createTime.toInstant();

        Duration duration = Duration.between(created, now);

        long seconds = duration.getSeconds();

        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        // 🔥 Formatting logic
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm", minutes);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public static String getSLARemaining(Date createTime, Date dueTime) {
        //log.info("Calculating remaining SLA");

        if (createTime == null || dueTime == null) {
            return "-";
        }

        Instant now = Instant.now();
        Instant dueInstant = dueTime.toInstant();

        // ✅ Check SLA breach
        if (now.isAfter(dueInstant)) {
            return "Escalated";
        }

        Duration duration = Duration.between(now, dueInstant);

        long seconds = duration.getSeconds();

        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        // ✅ Formatting
        if (days > 0) {
            return String.format("%dd %dh %dm left", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh %dm left", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm left", minutes);
        } else {
            return String.format("%ds left", seconds);
        }
    }

    public static String getDifference(Date fromDate, Date toDate) {

        if (fromDate == null || toDate == null) {
            return "-";
        }

        Instant from = fromDate.toInstant();
        Instant to = toDate.toInstant();

        // Handle reverse dates (avoid negative duration issues)
        boolean isFuture = to.isBefore(from);

        Duration duration = Duration.between(from, to).abs();

        long seconds = duration.getSeconds();

        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        String result;

        if (days > 0) {
            result = String.format("%dd %dh %dm", days, hours, minutes);
        } else if (hours > 0) {
            result = String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            result = String.format("%dm", minutes);
        } else {
            result = String.format("%ds", seconds);
        }

        // Optional: indicate direction
        return isFuture ? result + " ago" : result;
    }

    public static String convertToDDHHMMSS(long millis) {
        Duration duration = Duration.ofMillis(millis);

        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);

        long seconds = duration.getSeconds();

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("D ");
        }
        if (hours > 0) {
            result.append(hours).append("H ");
        }
        if (minutes > 0) {
            result.append(minutes).append("M ");
        }
        if (seconds > 0 || result.length() == 0) { // always show seconds if everything else is 0
            result.append(seconds).append("S");
        }

        return result.toString().trim();
    }
}