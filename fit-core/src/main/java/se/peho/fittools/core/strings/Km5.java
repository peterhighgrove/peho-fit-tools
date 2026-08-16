package se.peho.fittools.core.strings;

public class Km5 {

    private String distStr = null;

    public Km5(Float dist) {

        // --- Distance as km, trimmed, no trailing zeros or dot ---
            float distanceKm = (dist != null) ? (dist / 1000.0f) : 0.00f;
            distStr = String.format("%.5fkm", distanceKm)
                .replace(",", ".")
                .replace(".00000", "")
                ; 

        if (StringsDebug.enabled) System.out.println("  Extracted Distance:'" + distanceKm + "'"
            + " => Formatted:'" + distStr + "'"
            );
    }
            
    public String get() {
        return distStr;
    }

    public static String get(Float dist) {
        return new Km5(dist).get();
    }
}
