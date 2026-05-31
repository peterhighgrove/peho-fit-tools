package se.peho.fittools.core.strings;

import com.garmin.fit.SessionMesg;

import se.peho.fittools.core.FitFileForIndoor;

public class MergedFileBaseStr {
    private String baseName = null;

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static boolean containsIgnoreCase(String source, String candidate) {
        if (source == null || source.isEmpty() || candidate == null || candidate.isEmpty()) {
            return false;
        }
        return source.toLowerCase().contains(candidate.toLowerCase());
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /* public FileBaseStr(String dateTime, String profile, String workoutName, String dist
            , String timer, String product) {
        String dedupedWorkoutName = containsIgnoreCase(profile, workoutName) ? null : workoutName;
        String dedupedDist = containsIgnoreCase(profile, dist) ? null : dist;

        this.baseName = dateTime
            + (profile != null && !profile.isEmpty() ? "-" + profile : "")
            + (dedupedWorkoutName != null && !dedupedWorkoutName.isEmpty() ? "-" + dedupedWorkoutName : "")
            + (dedupedDist != null && !dedupedDist.isEmpty() ? "-" + dedupedDist : "")
            + (timer != null && !timer.isEmpty() ? "-" + timer : "")
            + (product != null && !product.isEmpty() ? "-" + product : "");

            if (StringsDebug.enabled) System.out.println("    => Formatted base name: " + baseName);

    } */
            
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //Full FileBaseStr WITH DateTime
    public MergedFileBaseStr(String dateTime, String profile, String workoutName, String dist
            , String timer, String product, String suffix) {
        String dedupedWorkoutName = containsIgnoreCase(profile, workoutName) ? null : workoutName;
        String dedupedSuffix = containsIgnoreCase(profile, suffix) ? null : suffix;
        String dedupedDist = containsIgnoreCase(profile, dist) ? null : dist;

        this.baseName = dateTime
            + (profile != null && !profile.isEmpty() ? "-" + profile : "")
            + (dedupedWorkoutName != null && !dedupedWorkoutName.isEmpty() ? "-" + dedupedWorkoutName : "")
            + (dedupedSuffix != null && !dedupedSuffix.isEmpty() ? "-" + dedupedSuffix : "")
            + (dedupedDist != null && !dedupedDist.isEmpty() ? "-" + dedupedDist : "")
            + (timer != null && !timer.isEmpty() ? "-" + timer : "")
            + (product != null && !product.isEmpty() ? "-" + product : "") 
            ;

            if (StringsDebug.enabled) System.out.println("    => Formatted base name: '" + baseName + "'"
                + "\n        DateTime: '" + dateTime + "'"
                + "\n        Profile: '" + profile + "'"
                + "\n        WorkoutName: '" + workoutName + "'"
                + "\n        Dist: '" + dist + "'"
                + "\n        Timer: '" + timer + "'"
                + "\n        Product: '" + product + "'"
                + "\n        Suffix: '" + suffix + "'"
            );
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //Full FileBaseStr WITHOUT DateTime
    public MergedFileBaseStr(String profile, String workoutName, String dist
            , String timer, String product, String suffix) {
        String dedupedWorkoutName = containsIgnoreCase(profile, workoutName) ? null : workoutName;
        String dedupedSuffix = containsIgnoreCase(profile, suffix) ? null : suffix;
        String dedupedDist = containsIgnoreCase(profile, dist) ? null : dist;

        this.baseName = ""
            + (profile != null && !profile.isEmpty() ? profile : "")
            + (dedupedWorkoutName != null && !dedupedWorkoutName.isEmpty() ? "-" + dedupedWorkoutName : "")
            + (dedupedSuffix != null && !dedupedSuffix.isEmpty() ? "-" + dedupedSuffix : "")
            + (dedupedDist != null && !dedupedDist.isEmpty() ? "-" + dedupedDist : "")
            + (timer != null && !timer.isEmpty() ? "-" + timer : "")
            + (product != null && !product.isEmpty() ? "-" + product : "") 
            ;

            if (StringsDebug.enabled) System.out.println("    => Formatted base name: '" + baseName + "'"
                + "\n        Profile: '" + profile + "'"
                + "\n        WorkoutName: '" + workoutName + "'"
                + "\n        Dist: '" + dist + "'"
                + "\n        Timer: '" + timer + "'"
                + "\n        Product: '" + product + "'"
                + "\n        Suffix: '" + suffix + "'"
            );
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String get() {
        return baseName;
    }
    
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String get(String dateTime, String profile, String workoutName, String dist
            , String timer, String product, String suffix) {
        return new MergedFileBaseStr(dateTime, profile, workoutName, dist, timer, product, suffix).get();
    }

}
