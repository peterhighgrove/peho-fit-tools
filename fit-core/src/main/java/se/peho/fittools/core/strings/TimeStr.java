package se.peho.fittools.core.strings;

public class TimeStr {

    private String formattedString = null;

    public TimeStr(Float timer) {

        String timerStr = new Hmmss(timer).get();

        if (timer != null) {
            formattedString = timerStr;
        }

        if (StringsDebug.enabled) System.out.println("  Extracted Timer:'" + timerStr + "'"
            + " => Formatted:'" + formattedString + "'"
            );
    }
            
    public String get() {
        return formattedString;
    }

    public static String get(Float timer) {
        return new TimeStr(timer).get();
    }
}
