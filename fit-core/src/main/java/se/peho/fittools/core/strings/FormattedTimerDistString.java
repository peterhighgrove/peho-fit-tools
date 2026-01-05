package se.peho.fittools.core.strings;

public class FormattedTimerDistString {

    private String formattedString = null;

    public FormattedTimerDistString(Float timer, Float dist) {

        String timerStr = new Hmmss(timer).get();
        String distanceStr = new Km1(dist).get();

        if (timer != null && dist != null) {
            formattedString = timerStr + "-" + distanceStr;
        } else if (timer != null) {
            formattedString = timerStr;
        } else if (dist != null) {
            formattedString = distanceStr;
        }

        System.out.println("  Extracted info: Timer='" + timerStr + "'"
            + ", Distance='" + distanceStr + "'"
            + " => FormattedTimerDistString='" + formattedString + "'"
            );
    }
            
    public String get() {
        return formattedString;
    }
    
}
