package se.peho.fittools.core.strings;

public class Hmmss {

    private String hmmss = null;

    public Hmmss(Float timer) {

            long totalSeconds = (timer != null) ? Math.round(timer) : 0L;
            long totalMinutes = totalSeconds / 60;
            long totalHours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            long seconds = totalSeconds % 60;

            String secStr = String.format("%02dsec", seconds)
                .replace(",", ".")
                .replace(".00", "")
                ;
            String minSecStr = String.format("%d:%02dmin", totalMinutes, seconds)
                .replace(",", ".")
                .replace(".00", "")
                ;
            String hourMinSecStr = String.format("%d:%02d:%02dh", totalHours, minutes, seconds)
                .replace(",", ".")
                .replace(".00", "")
                ;
            if (totalHours > 0) {
                hmmss = hourMinSecStr;
            } else if (totalMinutes > 0) {
                hmmss = minSecStr;
            } else {
                hmmss = secStr;
            }

        System.out.println("  Extracted Timer:'" + timer + "'"
            + " => Formatted:'" + hmmss + "'"
            );
    }
            
    public String get() {
        return hmmss;
    }

    public static String get(Float timer) {
        return new Hmmss(timer).get();
    }
}
