package se.peho.fittools.garminWktFitMergeAnalyze;
import com.garmin.fit.DateTime;

public class GarminDate {
    DateTime objectDateTime;
    int objectHoursToAdd = 0;

    public GarminDate(DateTime time) {
        objectDateTime = time;
    }

    public GarminDate(DateTime time, int hours) {
        objectDateTime = time;
        objectHoursToAdd = hours;
    }

    public String date2String() {
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
        int hhInt = Integer.valueOf(timeParts[0])+objectHoursToAdd; 
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
}
