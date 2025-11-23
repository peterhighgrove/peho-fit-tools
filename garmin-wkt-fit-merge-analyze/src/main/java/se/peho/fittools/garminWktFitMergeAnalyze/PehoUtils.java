package se.peho.fittools.garminWktFitMergeAnalyze;

public class PehoUtils {
/*
    private static double EARTH_RADIUS_KM = 6371.0;
    private static final double SEMICIRCLES_PER_DEGREE = 11930464.711111111;

    public static double distCalc(int lat1Deg, int lon1Deg, int lat2Deg, int lon2Deg) {

        return distCalc(degreesToSemicircles(lat1Deg), degreesToSemicircles(lon1Deg), degreesToSemicircles(lat2Deg), degreesToSemicircles(lon2Deg));
    }

    public static double distCalc(double lat1Deg, double lon1Deg, double lat2Deg, double lon2Deg) {
        double dLat = Math.toRadians(lat2Deg - lat1Deg);
        double dLon = Math.toRadians(lon2Deg - lon1Deg);

        double radLat1 = Math.toRadians(lat1Deg);
        double radLat2 = Math.toRadians(lat2Deg);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000;
    }

    public static double degreesToSemicircles(double degrees) {
        return degrees / SEMICIRCLES_PER_DEGREE;
    }

    public static double semicirclesToDegrees(int semicircles) {
        return semicircles * (180.0 / Math.pow(2, 31));
    }
*/
    public static Integer safeParseInt(String str) {
    try {
        return Integer.parseInt(str);
    } catch (NumberFormatException e) {
        return null; // or you could return a default value like 0
    }
}
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2kmph3(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600f / 1000f *1000)/1000);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2kmph2(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600f / 1000f *100)/100);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2kmph1(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600f / 1000f *10)/10);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String m2km1(Float speed) {
        String speedStr = String.valueOf(Float.valueOf(Math.round(speed / 1000f *10))/10);
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String m2km2(Float speed) {
        String speedStr = String.valueOf(Float.valueOf(Math.round(speed / 1000f *100))/100);
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2minpkm(Float speed) {
        String speedStr = "";
        if (speed.equals(0)) {
            speedStr = "-";
        } else {
            speedStr = sec2minSecLong(1 / (speed / 1000f));
        }
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2minp500m(Float speed) {
        String speedStr = "";
        if (speed.equals(0)) {
            speedStr = "-";
        } else {
            speedStr = sec2minSecLong(1 / (speed / 1000f) / 2f);
        }
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecShort(Float seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec == 0) {
            minStr += "";
        } else if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecShort(Long seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec == 0) {
            minStr += "";
        } else if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecLong(Float seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecLong(Long seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }

}
