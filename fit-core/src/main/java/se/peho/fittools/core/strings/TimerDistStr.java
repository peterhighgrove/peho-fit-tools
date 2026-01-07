package se.peho.fittools.core.strings;

public class TimerDistStr {

    private String formattedString = null;

    public TimerDistStr(Float timer, Float dist) {

        String timerStr = new Hmmss(timer).get();
        String distanceStr = new Km1(dist).get();

        if (timer != null && dist != null) {
            formattedString = timerStr + "-" + distanceStr;
        } else if (timer != null) {
            formattedString = timerStr;
        } else if (dist != null) {
            formattedString = distanceStr;
        }

        System.out.println("  Extracted Timer:'" + timerStr + "'"
            + ", Distance='" + distanceStr + "'"
            + " => Formatted:'" + formattedString + "'"
            );
    }
            
    public String get() {
        return formattedString;
    }

    public static String get(Float timer, Float dist) {
        return new TimerDistStr(timer, dist).get();
    }
}
