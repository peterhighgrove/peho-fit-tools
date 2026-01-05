package se.peho.fittools.core.strings;

public class FormattedWorkoutName {
    private String name = null;

    public FormattedWorkoutName(String workoutName) {
        if (workoutName != null && !workoutName.isEmpty()) {

            this.name = workoutName
                .replace(",", ".")
                .replace("Bike ", "")
                .replace("Run ", "")
                .replace("Styrka ", "")
                .replace(" (bike)", "")
                .replace("HR", "")
                ;
            System.out.println("  Extracted info: WorkoutName=" + workoutName
                + " => FormattedWorkoutName='" + name + "'"
                );
        }
    }
            
    public String getName() {
        return name;
    }
    
}
