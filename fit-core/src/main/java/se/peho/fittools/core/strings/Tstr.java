package se.peho.fittools.core.strings;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Tstr {

    private final long FIT_EPOCH_OFFSET = 631065600L; // seconds
    private final String formatPattern = "HH:mm:ss";

    private String timeStr = null;

    public Tstr(Long garminLocalDate) {
        timeStr = formatDateTime(garminLocalDate, 0L);
    }

    public Tstr (Long garminLocalDate, Long offsetMinutes) {
        timeStr = formatDateTime(garminLocalDate, offsetMinutes);
    }

    private String formatDateTime(Long garminLocalDate, Long offsetMinutes) {
        if (garminLocalDate == null) {
            timeStr = "null";
            return timeStr;
        }
        String tz = offsetMinutesToTimeZoneString(offsetMinutes);
        long unixDateTime = garminLocalDate + FIT_EPOCH_OFFSET;
        Instant instantDateTime = Instant.ofEpochSecond(unixDateTime);
        String T = DateTimeFormatter.ofPattern(formatPattern).withZone(ZoneOffset.of(tz)).format(instantDateTime);
        if (StringsDebug.enabled) System.out.println("  Extracted DateTime:'" + garminLocalDate + "'" + " => Formatted:'" + T + "'");
        
        return T;
    }

    private String offsetMinutesToTimeZoneString(Long offsetMinutes) {
        int absOffsetMinutes = Math.abs(offsetMinutes.intValue());
        int hours = absOffsetMinutes / 60;
        int minutes = absOffsetMinutes % 60;
        String sign = offsetMinutes >= 0 ? "+" : "-";
        return String.format("%s%02d:%02d", sign, hours, minutes);
    }
    public String get() {
        return timeStr;
    }

    public static String get(Long garminLocalDate) {
        return new Tstr(garminLocalDate).get();
    }

    public static String get(Long garminLocalDate, Long offsetMinutes) {
        return new Tstr(garminLocalDate, offsetMinutes).get();
    }

}
