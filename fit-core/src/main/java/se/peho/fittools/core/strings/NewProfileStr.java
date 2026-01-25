package se.peho.fittools.core.strings;

public class NewProfileStr {
    private String baseName = null;

    public NewProfileStr(String profile, String workoutName, String dist) {
        this.baseName = ""
            + (profile != null && !profile.isEmpty() ? " " + profile : "")
            + (workoutName != null && !workoutName.isEmpty() ? " " + workoutName : "")
            + (dist != null && !dist.isEmpty() ? " " + dist : "")
            ;

            System.out.println("    => Formatted new profile name: " + baseName);

    }
            
    public NewProfileStr(String profile, String workoutName, String dist, String suffix) {
        this.baseName = ""
            + (profile != null && !profile.isEmpty() ? " " + profile : "")
            + (workoutName != null && !workoutName.isEmpty() ? " " + workoutName : "")
            + (dist != null && !dist.isEmpty() ? " " + dist : "")
            + (suffix != null && !suffix.isEmpty() ? " " + suffix : "")
            ;

            System.out.println("    => Formatted new profile name: " + baseName);

    }
            
    public String get() {
        return baseName;
    }

    public String set() {
        return baseName;
    }

    public static String get(String profile, String workoutName, String timerDist, String product) {
        return new NewProfileStr(profile, workoutName, timerDist, product).get();
    }

}
