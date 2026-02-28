package se.peho.fittools.core.strings;

public class WorkoutStr {
    private String name = null;

    public WorkoutStr(String workoutName) {
        if (workoutName != null && !workoutName.isEmpty()) {

            this.name = workoutName
                .replace(",", ".")
                .replace("Bike ", "")
                .replace("Run ", "")
                .replace("Styrka ", "")
                .replace(" (bike)", "")
                .replace("HR", "")
                .replace(" + ", "+")
                ;
            System.out.println("  Extracted info: WorkoutName=" + workoutName
                + " => FormattedWorkoutName='" + name + "'"
                );
        }
    }
            
    public String get() {
        return name;
    }

    public static String get(String workoutName) {
        return new WorkoutStr(workoutName).get();
    }
    
}
