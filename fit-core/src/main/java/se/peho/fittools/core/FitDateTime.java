package se.peho.fittools.core;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.garmin.fit.DateTime;

public class FitDateTime {
    
    public static final long FIT_EPOCH_OFFSET = 631065600L; // seconds
    
    DateTime dateTimeValue;
    String tz = "+00:00";

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String toTimerString(Long timerValue) {
        
        // timerValue is seconds from start of activity, NOT garmin DateTime epoch

        if (timerValue == null) {
            return null;
        }

        long totalSeconds = timerValue;
        if (totalSeconds < 0) {
            return null; // invalid before epoch
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            // h:mm:ss h
            return String.format("%d:%02d:%02d h", hours, minutes, seconds);
        } else if (minutes > 0) {
            // m:ss min
            return String.format("%d:%02d min", minutes, seconds);
        } else {
            // s sec
            return String.format("%d sec", seconds);
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public FitDateTime(DateTime dateTimeValue, long offsetMinutes) {
        this.dateTimeValue = dateTimeValue;
        this.tz = offsetToTimeZoneString(offsetMinutes);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public FitDateTime(DateTime dateTimeValue, String tz) {
        this.dateTimeValue = dateTimeValue;
        this.tz = tz;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public FitDateTime(DateTime dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
        this.tz = "+00:00";
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void setDateTimeValue(DateTime dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void setZoneStringFromOffset(long offsetMinutes) {
        this.tz = offsetToTimeZoneString(offsetMinutes);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public DateTime getDateTimeValue() {
        return this.dateTimeValue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String getZoneString() {
        return this.tz;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String toString() {
        String formatPattern = "yyyy-MM-dd-HH-mm-ss z";
        long unixDateTime = this.dateTimeValue.getTimestamp()+FIT_EPOCH_OFFSET;
        Instant instantDateTime = Instant.ofEpochSecond(unixDateTime);
        String strDateTime = DateTimeFormatter.ofPattern(formatPattern).format(instantDateTime);
        return strDateTime;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    static public String toString(DateTime fitDateTime) {
        if (fitDateTime == null) {
            fitDateTime= new DateTime(0);
        }
        String tz = offsetToTimeZoneString(0);
        String formatPattern = "yyyy-MM-dd-HH-mm-ss";
        long unixDateTime = fitDateTime.getTimestamp() + FIT_EPOCH_OFFSET;
        Instant instantDateTime = Instant.ofEpochSecond(unixDateTime);
        String strDateTime = DateTimeFormatter.ofPattern(formatPattern).withZone(ZoneOffset.of(tz)).format(instantDateTime);
        return strDateTime;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    static public String toString(Long fitDateTime) {
        if (fitDateTime == null) {
            fitDateTime= 0l;
        }
        String tz = offsetToTimeZoneString(0);
        String formatPattern = "yyyy-MM-dd-HH-mm-ss";
        long unixDateTime = fitDateTime + FIT_EPOCH_OFFSET;
        Instant instantDateTime = Instant.ofEpochSecond(unixDateTime);
        String strDateTime = DateTimeFormatter.ofPattern(formatPattern).withZone(ZoneOffset.of(tz)).format(instantDateTime);
        return strDateTime;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    static public String toString(DateTime fitDateTime, long offsetMinutes) {
        if (fitDateTime == null) {
            fitDateTime = new DateTime(0);
        }
        String tz = offsetToTimeZoneString(offsetMinutes);
        String formatPattern = "yyyy-MM-dd-HH-mm-ss";
        long unixDateTime = fitDateTime.getTimestamp() + FIT_EPOCH_OFFSET;
        Instant instantDateTime = Instant.ofEpochSecond(unixDateTime);
        String strDateTime = DateTimeFormatter.ofPattern(formatPattern).withZone(ZoneOffset.of(tz)).format(instantDateTime);
        return strDateTime;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    static public String toString(Long fitDateTime, long offsetMinutes) {
        if (fitDateTime == null) {
            fitDateTime = 0l;
        }
        String tz = offsetToTimeZoneString(offsetMinutes);
        String formatPattern = "yyyy-MM-dd-HH-mm-ss";
        long unixDateTime = fitDateTime + FIT_EPOCH_OFFSET;
        Instant instantDateTime = Instant.ofEpochSecond(unixDateTime);
        String strDateTime = DateTimeFormatter.ofPattern(formatPattern).withZone(ZoneOffset.of(tz)).format(instantDateTime);
        return strDateTime;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    static public String offsetToTimeZoneString(long offsetMinutes) {
        long hours = offsetMinutes / 60;
        long minutes = Math.abs(offsetMinutes % 60);
        return String.format("%s%02d:%02d", offsetMinutes >= 0 ? "+" : "-", Math.abs(hours), minutes);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    static public String date2String(DateTime objectDateTime, long offsetMinutes) {
        String[] parts = objectDateTime.toString().split(" "); // Split w space divider
        String yyyy = parts[5] + "-";
        String mm = "";
        switch (parts[1]) {
            case "Jan":
                mm = "01";
                break;
            case "Feb":
                mm = "02";
                break;
            case "Mar":
                mm = "03";
                break;
            case "Apr":
                mm = "04";
                break;
            case "May":
                mm = "05";
                break;
            case "Jun":
                mm = "06";
                break;
            case "Jul":
                mm = "07";
                break;
            case "Aug":
                mm = "08";
                break;
            case "Sep":
                mm = "09";
                break;
            case "JOct":
                mm = "10";
                break;
            case "Nov":
                mm = "11";
                break;
            case "Dec":
                mm = "12";
                break;
        }
        mm = mm + "-";
        String dd = parts[2] + "-";
        String[] timeParts = parts[3].split(":"); // Split time with :
        int hhInt = Integer.valueOf(timeParts[0])+(int)offsetMinutes/60; 
        String hh = ""; 
        if (hhInt < 10) {
            hh = "0" + hhInt + "-";
        } else {
            hh = hhInt + "-";
        }
        String min = timeParts[1] + "-"; 
        String ss = timeParts[2]; 

        return (yyyy + mm + dd + hh + min + ss);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    
}
