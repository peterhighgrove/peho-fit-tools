package se.peho.fittools.core.strings;

public class Km1 {

    private String km1 = null;

    public Km1(Float dist) {

        // --- Distance as km, trimmed, no trailing zeros or dot ---
            double distanceKm = (dist != null) ? (dist / 1000.0) : 0.0;
            km1 = String.format("%.1fkm", distanceKm)
                .replace(",", ".")
                .replace(".0", "")
                ; 

        System.out.println("  Extracted info: Distance='" + distanceKm + "'"
            + " => FormattedTimerDistString='" + km1 + "'"
            );
    }
            
    public String get() {
        return km1;
    }
}
