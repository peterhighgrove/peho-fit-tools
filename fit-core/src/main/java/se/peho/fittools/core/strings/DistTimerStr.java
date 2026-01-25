package se.peho.fittools.core.strings;

public class DistTimerStr {

    private String formattedString = null;

    public DistTimerStr(Float dist, Float timer) {

        String distanceStr = new Km1(dist).get();
        String timerStr = new Hmmss(timer).get();

        if (timer != null && dist != null) {
            formattedString = distanceStr + "-" + timerStr;
        } else if (dist != null) {
            formattedString = distanceStr;
        } else if (timer != null) {
            formattedString = timerStr;
        }

        System.out.println("  Extracted Distance:'" + distanceStr + "'"
            + ", Timer='" + timerStr + "'"
            + " => Formatted:'" + formattedString + "'"
            );
    }
            
    public String get() {
        return formattedString;
    }

    public static String get(Float dist, Float timer) {
        return new DistTimerStr(dist, timer).get();
    }
}
