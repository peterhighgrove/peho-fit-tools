package se.peho.fittools.core.strings;

public class Km2 {

    private String km2 = null;

    public Km2(Float dist) {

        // --- Distance as km, trimmed, no trailing zeros or dot ---
            double distanceKm = (dist != null) ? (dist / 1000.0) : 0.00;
            km2 = String.format("%.2fkm", distanceKm)
                .replace(",", ".")
                .replace(".00", "")
                ; 

        System.out.println("  Extracted info: Distance='" + distanceKm + "'"
            + " => FormattedTimerDistString='" + km2 + "'"
            );
    }
            
    public String get() {
        return km2;
    }
}
