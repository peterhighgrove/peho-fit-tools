package se.peho.fittools.core.strings;

import com.garmin.fit.SessionMesg;

import se.peho.fittools.core.FitFileForIndoor;

public class FileBaseStr {
    private String baseName = null;

    public FileBaseStr(String dateTime, String profile, String workoutName, String timerDist, String product) {
        this.baseName = dateTime
            + (profile != null && !profile.isEmpty() ? "-" + profile : "")
            + (workoutName != null && !workoutName.isEmpty() ? "-" + workoutName : "")
            + (timerDist != null && !timerDist.isEmpty() ? "-" + timerDist : "")
            + (product != null && !product.isEmpty() ? "-" + product : "");

            System.out.println("    => Formatted base name: " + baseName);

    }
            
    public FileBaseStr(String dateTime, String profile, String workoutName, String timerDist, String product, String suffix) {
        this.baseName = dateTime
            + (profile != null && !profile.isEmpty() ? "-" + profile : "")
            + (workoutName != null && !workoutName.isEmpty() ? "-" + workoutName : "")
            + (timerDist != null && !timerDist.isEmpty() ? "-" + timerDist : "")
            + (suffix != null && !suffix.isEmpty() ? "-" + suffix : "")
            + (product != null && !product.isEmpty() ? "-" + product : "") 
            ;

            System.out.println("    => Formatted base name: " + baseName);
    }
            
    public String get() {
        return baseName;
    }

    public static String get(String dateTime, String profile, String workoutName, String timerDist, String product) {
        return new FileBaseStr(dateTime, profile, workoutName, timerDist, product).get();
    }

}
