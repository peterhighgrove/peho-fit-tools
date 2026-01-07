package se.peho.fittools.core.strings;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DTstr {

    private final long FIT_EPOCH_OFFSET = 631065600L; // seconds
    private final String formatPattern = "yyyy-MM-dd-HH-mm-ss";

    private String DateTimeStr = null;

    public DTstr(Long garminLocalDate) {
        DateTimeStr = formatDateTime(garminLocalDate, 0);
    }

    public DTstr (Long garminLocalDate, int offsetMinutes) {
        DateTimeStr = formatDateTime(garminLocalDate, offsetMinutes);
    }

    private String formatDateTime(Long garminLocalDate, int offsetMinutes) {
        if (garminLocalDate == null) {
            garminLocalDate = 0l;
        }
        String tz = offsetMinutesToTimeZoneString(offsetMinutes);
        long unixDateTime = garminLocalDate + FIT_EPOCH_OFFSET;
        Instant instantDateTime = Instant.ofEpochSecond(unixDateTime);
        String DT = DateTimeFormatter.ofPattern(formatPattern).withZone(ZoneOffset.of(tz)).format(instantDateTime);
        System.out.println("  Extracted DateTime:'" + garminLocalDate + "'" + " => Formatted:'" + DT + "'");
        
        return DT;
    }

    private String offsetMinutesToTimeZoneString(int offsetMinutes) {
        int absOffsetMinutes = Math.abs(offsetMinutes);
        int hours = absOffsetMinutes / 60;
        int minutes = absOffsetMinutes % 60;
        String sign = offsetMinutes >= 0 ? "+" : "-";
        return String.format("%s%02d:%02d", sign, hours, minutes);
    }
    public String get() {
        return DateTimeStr;
    }

    public static String get(Long garminLocalDate) {
        return new DTstr(garminLocalDate).get();
    }

    public static String get(Long garminLocalDate, int offsetMinutes) {
        return new DTstr(garminLocalDate, offsetMinutes).get();
    }

}
