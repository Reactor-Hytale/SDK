package codes.reactor.sdk.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class TimeFormatter {

    public static final Map<Character, Long> UNIT_MULTIPLIERS = new HashMap<>();

    private record TimeUnit(char key, long millis) {
        private TimeUnit(char key, Duration millis) {
            this(key, millis.toMillis());
        }
    }

    private static final TimeUnit[] UNITS = {
        new TimeUnit('a', Duration.ofDays(365)),
        new TimeUnit('M', Duration.ofDays(30)),
        new TimeUnit('d', Duration.ofDays(1)),
        new TimeUnit('h', Duration.ofHours(1)),
        new TimeUnit('m', Duration.ofMinutes(1)),
        new TimeUnit('s', Duration.ofSeconds(1)),
    };

    static {
        UNIT_MULTIPLIERS.put('s', 1L);           // Seconds
        UNIT_MULTIPLIERS.put('m', 60L);          // Minutes
        UNIT_MULTIPLIERS.put('h', 3600L);        // Hours
        UNIT_MULTIPLIERS.put('d', 86400L);       // Days
        UNIT_MULTIPLIERS.put('y', 86400L * 365); // Years
    }

    public static String formatSince(Instant instant, Instant since) {
        return format(Duration.between(instant, since).getSeconds());
    }

    public static @NotNull String format(long seconds) {
        long years = seconds / (365L * 24 * 60 * 60);
        long months = seconds / (30L * 24 * 60 * 60);
        long days = seconds / (24 * 60 * 60);
        long hours = seconds / (60 * 60);
        long minutes = seconds / 60;

        if (seconds < 60) {
            return seconds + "s";
        } else if (minutes < 60) {
            long sec = seconds % 60;
            return sec == 0 ? minutes + "m" : minutes + "m " + sec + "s";
        } else if (hours < 24) {
            long min = (seconds % (60 * 60)) / 60;
            return min == 0 ? hours + "h" : hours + "h " + min + "m";
        } else if (days < 30) {
            long hr = (seconds % (24 * 60 * 60)) / (60 * 60);
            return hr == 0 ? days + "d" : days + "d " + hr + "h";
        } else if (months < 12) {
            long d = (seconds % (30L * 24 * 60 * 60)) / (24 * 60 * 60);
            return months + "m " + d + "d";
        } else {
            long d = (seconds % (365L * 24 * 60 * 60)) / (24 * 60 * 60);
            return years + "y " + d + "d";
        }
    }

    public static @NotNull String formatMillis(final long inputMillis, final boolean includeMillis) {
        final StringBuilder result = new StringBuilder();
        long millis = inputMillis;
        boolean first = true;
        for (final TimeUnit time : UNITS) {
            final long unitValue = time.millis;
            final long unitCount = millis / unitValue;
            if (unitCount > 0) {
                if (!first) {
                    result.append(" ");
                }
                result.append(unitCount).append(time.key);
                millis %= unitValue;
                first = false;
            }
        }
        if (includeMillis && millis > 0) {
            if (!first) {
                result.append(" ");
            }
            result.append(millis).append("ms");
        }
        return result.toString();
    }

    public static long convertToSecondsSupport(final @NotNull String timeParts) {
        return convertToSeconds(Arrays.asList(timeParts.split(" ")));
    }

    public static long convertToSecondsSupportPermanent(final @NotNull Collection<String> timeParts) {
        for (final String part : timeParts) {
            if ("permanent".equalsIgnoreCase(part)) {
                return -1;
            }
        }
        return convertToSeconds(timeParts);
    }

    public static long convertToSeconds(final Collection<String> timeParts) {
        long totalSeconds = 0;

        for (String part : timeParts) {
            if (part.length() < 2) {
                throw new IllegalArgumentException("Invalid format: " + part);
            }
            char unit = part.charAt(part.length() - 1);
            final long multiplier = UNIT_MULTIPLIERS.get(unit);
            if (multiplier == 0) {
                throw new IllegalArgumentException("Invalid unit of time: " + unit);
            }
            try {
                long value = Long.parseLong(part.substring(0, part.length() - 1));
                totalSeconds += value * multiplier;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number in: " + part);
            }
        }

        return totalSeconds;
    }
}