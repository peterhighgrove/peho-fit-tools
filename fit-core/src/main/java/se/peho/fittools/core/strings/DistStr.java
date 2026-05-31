package se.peho.fittools.core.strings;

public class DistStr {

    private String formattedString = null;

    public DistStr(Float dist) {

        String distanceStr = new Km1(dist).get();

        if (dist != null) {
            formattedString = distanceStr;
        }

        if (StringsDebug.enabled) System.out.println("  Extracted Distance:'" + distanceStr + "'"
            + " => Formatted:'" + formattedString + "'"
            );
    }
            
    public String get() {
        return formattedString;
    }

    public static String get(Float dist) {
        return new DistStr(dist).get();
    }
}
