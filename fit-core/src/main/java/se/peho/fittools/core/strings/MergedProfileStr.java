package se.peho.fittools.core.strings;
import se.peho.fittools.core.FitFile;

public class MergedProfileStr {
    private String baseName = null;

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static boolean containsIgnoreCase(String source, String candidate) {
        if (source == null || source.isEmpty() || candidate == null || candidate.isEmpty()) {
            return false;
        }
        return source.toLowerCase().contains(candidate.toLowerCase());
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public MergedProfileStr(String profile, String workoutName, String dist) {
        this.baseName = ""
            + (profile != null && !profile.isEmpty() ? profile : "")
            + (workoutName != null && !workoutName.isEmpty() ? " " + workoutName : "")
            + (dist != null && !dist.isEmpty() ? " " + dist : "")
            ;

            if (StringsDebug.enabled) System.out.println("    => Formatted new profile name: " + baseName);
    }
    
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public MergedProfileStr(String profile, String workoutName, String dist, String suffix) {
        String dedupedWorkoutName = containsIgnoreCase(profile, workoutName) ? null : workoutName;
        String dedupedSuffix = containsIgnoreCase(profile, suffix) ? null : suffix;
        String dedupedDist = containsIgnoreCase(profile, dist) ? null : dist;

        this.baseName = ""
            + (profile != null && !profile.isEmpty() ? profile : "")
            + (dedupedWorkoutName != null && !dedupedWorkoutName.isEmpty() ? " " + dedupedWorkoutName : "")
            + (dedupedSuffix != null && !dedupedSuffix.isEmpty() ? " " + dedupedSuffix : "")
            + (dedupedDist != null && !dedupedDist.isEmpty() ? " " + dedupedDist : "")
            ;

        if (StringsDebug.enabled) System.out.println("    => Profile: '" + profile + "'"
            + "\n        Profile: '" + profile + "'"
            + "\n        WorkoutName: '" + workoutName + "'"
            + "\n        Dist: '" + dist + "'"
            + "\n        Suffix: '" + suffix + "'"
        );
        if (StringsDebug.enabled) System.out.println("    => Formatted new profile name: " + baseName);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public MergedProfileStr(FitFile fitFile) {
        String profile = new ProfileStr(fitFile.getSportProfile(), 
                fitFile.getSport(), 
                fitFile.getSubsport()
                ).get();
        String workoutName = new WorkoutStr(fitFile.getWktName()).get();
        String suffix = fitFile.getActivityNameSuffix();
        String dist = new DistStr(fitFile.getTotalDistance()).get();
        
        String dedupedWorkoutName = containsIgnoreCase(profile, workoutName) ? null : workoutName;
        String dedupedSuffix = containsIgnoreCase(profile, suffix) ? null : suffix;
        String dedupedDist = containsIgnoreCase(profile, dist) ? null : dist;

        this.baseName = ""
            + (profile != null && !profile.isEmpty() ? profile : "")
            + (dedupedWorkoutName != null && !dedupedWorkoutName.isEmpty() ? " " + dedupedWorkoutName : "")
            + (dedupedSuffix != null && !dedupedSuffix.isEmpty() ? " " + dedupedSuffix : "")
            + (dedupedDist != null && !dedupedDist.isEmpty() ? " " + dedupedDist : "")
            ;

        if (StringsDebug.enabled) System.out.println("    => Profile: '" + profile + "'"
            + "\n        Profile: '" + profile + "'"
            + "\n        WorkoutName: '" + workoutName + "'"
            + "\n        Dist: '" + dist + "'"
            + "\n        Suffix: '" + suffix + "'"
        );
        if (StringsDebug.enabled) System.out.println("    => Formatted new profile name: " + baseName);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String get() {
        return baseName;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String set() {
        return baseName;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String get(String profile, String workoutName, String timerDist, String product) {
        return new MergedProfileStr(profile, workoutName, timerDist, product).get();
    }

}
