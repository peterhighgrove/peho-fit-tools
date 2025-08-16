package se.peho.fittools.core;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoUtils {

    // Constant for Garmin conversion
    private static final double SEMICIRCLES_PER_DEGREE = Math.pow(2, 31) / 180.0; //11930464.711111111; //Formula Math.pow(2, 31) / 180.0

    private static final double EARTH_RADIUS = 6371000.0; // in meters

    public static double[] interpolate(int lat1, int lon1, int lat2, int lon2, double distanceFromStart) {
        return interpolate(fromSemicircles(lat1), fromSemicircles(lon1), fromSemicircles(lat2), 
            fromSemicircles(lon2), distanceFromStart);
    }

    public static double[] interpolate(double lat1, double lon1, double lat2, double lon2, double distanceFromStart) {
        // Convert degrees to radians
        double φ1 = Math.toRadians(lat1);
        double λ1 = Math.toRadians(lon1);
        double φ2 = Math.toRadians(lat2);
        double λ2 = Math.toRadians(lon2);

        // Compute angular distance between the points (great-circle distance / Earth's radius)
        double δ = distanceBetween(lat1, lon1, lat2, lon2) / EARTH_RADIUS;
        if (δ == 0) return new double[]{lat1, lon1}; // Points are the same

        double f = distanceFromStart / (EARTH_RADIUS * δ); // Fraction of the arc

        // Interpolated coordinates
        double a = Math.sin((1 - f) * δ) / Math.sin(δ);
        double b = Math.sin(f * δ) / Math.sin(δ);

        double x = a * Math.cos(φ1) * Math.cos(λ1) + b * Math.cos(φ2) * Math.cos(λ2);
        double y = a * Math.cos(φ1) * Math.sin(λ1) + b * Math.cos(φ2) * Math.sin(λ2);
        double z = a * Math.sin(φ1) + b * Math.sin(φ2);

        double φ3 = Math.atan2(z, Math.sqrt(x * x + y * y));
        double λ3 = Math.atan2(y, x);

        // Convert back to degrees
        return new double[]{Math.toDegrees(φ3), Math.toDegrees(λ3)};
    }

    // Haversine formula to get the distance in meters
    public static double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2 - lat1);
        double Δλ = Math.toRadians(lon2 - lon1);

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                   Math.cos(φ1) * Math.cos(φ2) *
                   Math.sin(Δλ / 2) * Math.sin(Δλ / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    // Parse "lat, lon" with auto-format detection
    public static double[] parseCoordinates(String input) {
        String[] parts = input.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected format: lat, lon");
        }

        double lat = parseCoordinate(parts[0].trim());
        double lon = parseCoordinate(parts[1].trim());
        return new double[]{lat, lon};
    }

    public static double parseCoordinate(String coord) {
        if (coord.matches("^[NSWE]?\\s*-?\\d+(\\.\\d+)?$")) {
            return parseDecimalDegrees(coord);
        } else if (coord.matches(".*°.*'\\s*\\d+(\\.\\d+)?\"")) {
            return parseDMS(coord);
        } else if (coord.matches(".*°.*'")) {
            return parseDM(coord);
        } else {
            throw new IllegalArgumentException("Unknown coordinate format: " + coord);
        }
    }

    public static double parseDecimalDegrees(String coord) {
        coord = coord.trim();
        double value = Double.parseDouble(coord.replaceAll("[NSEW]", "").trim());
        if (coord.startsWith("S") || coord.startsWith("W") || value < 0) {
            return -Math.abs(value);
        }
        return Math.abs(value);
    }

    public static double parseDM(String coord) {
        Pattern pattern = Pattern.compile("([NSEW])?\\s*(\\d{1,3})°\\s*(\\d+(\\.\\d+)?)'");
        Matcher matcher = pattern.matcher(coord);
        if (matcher.find()) {
            String dir = matcher.group(1);
            int degrees = Integer.parseInt(matcher.group(2));
            double minutes = Double.parseDouble(matcher.group(3));
            double decimal = degrees + (minutes / 60.0);
            if ("S".equalsIgnoreCase(dir) || "W".equalsIgnoreCase(dir)) {
                decimal *= -1;
            }
            return decimal;
        }
        throw new IllegalArgumentException("Invalid DM format: " + coord);
    }

    public static double parseDMS(String coord) {
        Pattern pattern = Pattern.compile("([NSEW])?\\s*(\\d{1,3})°\\s*(\\d{1,2})'\\s*(\\d+(\\.\\d+)?)\"");
        Matcher matcher = pattern.matcher(coord);
        if (matcher.find()) {
            String dir = matcher.group(1);
            int degrees = Integer.parseInt(matcher.group(2));
            int minutes = Integer.parseInt(matcher.group(3));
            double seconds = Double.parseDouble(matcher.group(4));
            double decimal = degrees + (minutes / 60.0) + (seconds / 3600.0);
            if ("S".equalsIgnoreCase(dir) || "W".equalsIgnoreCase(dir)) {
                decimal *= -1;
            }
            return decimal;
        }
        throw new IllegalArgumentException("Invalid DMS format: " + coord);
    }

    public static double distCalc(int lat1Deg, int lon1Deg, int lat2Deg, int lon2Deg) {

        return distCalc(fromSemicircles(lat1Deg), fromSemicircles(lon1Deg), fromSemicircles(lat2Deg), fromSemicircles(lon2Deg));
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

        return EARTH_RADIUS * c;
    }

    // Convert decimal degrees to Garmin semicircles
    public static int toSemicircles(double degrees) {
        return (int) Math.round(degrees * SEMICIRCLES_PER_DEGREE);
    }

    // Convert Garmin semicircles to decimal degrees
    public static double fromSemicircles(int semicircles) {
        return semicircles / SEMICIRCLES_PER_DEGREE;
    }

    public static void main(String[] args) {

       
        //Parse coordinates example
        String[] testInputs = {
            "N 59° 20.431', E 018° 02.684'",        // DM
            "N 59° 20' 25.86\", E 018° 02' 41.04\"", // DMS
            "59.34052, 18.04473",                   // Decimal
            "-59.34052, -18.04473",                 // Signed DD
        };

        for (String input : testInputs) {
            try {
                double[] coords = parseCoordinates(input);
                int latSemi = toSemicircles(coords[0]);
                int lonSemi = toSemicircles(coords[1]);
                System.out.printf("Input: %-40s →\n", input);
                System.out.printf("  Decimal Degrees: Lat %.8f, Lon %.8f%n", coords[0], coords[1]);
                System.out.printf("  Garmin Semicircles: Lat %d, Lon %d%n", latSemi, lonSemi);
                System.out.printf("  Back to Decimal: Lat %.8f, Lon %.8f%n%n", fromSemicircles(latSemi), fromSemicircles(lonSemi));
            } catch (Exception e) {
                System.out.println("Error parsing '" + input + "': " + e.getMessage());
            }
        }

        //Interpolate axample
        double lat1 = 59.34052;
        double lon1 = 18.04473;
        double lat2 = 59.34200;
        double lon2 = 18.05000;

        double distanceFromStart = 100.0; // meters

        double[] result = interpolate(lat1, lon1, lat2, lon2, distanceFromStart);
        System.out.printf("Point %.1f meters from A towards B: %.8f, %.8f%n",
                          distanceFromStart, result[0], result[1]);

    }
}
