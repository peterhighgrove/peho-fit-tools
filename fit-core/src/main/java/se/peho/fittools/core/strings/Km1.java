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

        System.out.println("  Extracted Distance:'" + distanceKm + "'"
            + " => Formatted:'" + km1 + "'"
            );
    }
            
    public String get() {
        return km1;
    }

    public static String get(Float dist) {
        return new Km1(dist).get();
    }
}
