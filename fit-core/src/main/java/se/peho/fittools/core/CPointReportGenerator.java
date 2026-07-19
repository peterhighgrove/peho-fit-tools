package se.peho.fittools.core;

import java.util.List;

import com.garmin.fit.CoursePoint;
import com.garmin.fit.CoursePointMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;

public class CPointReportGenerator {

    private final FitFile fitFile;
    // =================================================================================
    public CPointReportGenerator(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    // =================================================================================
    public void printCPoints() {
        printCPoints(null, "COURSE POINTS IN FILE", "No course points found in file.");
    }

    // =================================================================================
    public void printGenericCPoints() {
        printCPoints(CoursePoint.GENERIC, "GENERIC COURSE POINTS IN FILE", "No generic course points found in file.");
    }

    // =================================================================================
    public void printGenericCPointsInputList() {
        printCPointsInputList(CoursePoint.GENERIC, "GENERIC COURSE POINTS FOR Console Input", "No generic course points found in file.");
    }

    // =================================================================================
    private void printCPoints(CoursePoint typeFilter, String title, String emptyMessage) {
        List<Mesg> allMesg = fitFile.getAllMesg();

        System.out.println();
        System.out.println("===========================================");
        System.out.println(title);
        System.out.println("-------------------------------------------");
        // System.out.printf("%-3s %-15s %-22s %-7s %s%n",
        //     "No",
        //     "Type",
        //     "Name",
        //     "Dist",
        //     "Position");
        System.out.printf("%-3s %-13s %-19s%-7s%n",
            "No",
            "Type",
            "Name",
            "Dist");
        // System.out.printf("%-3s %-15s %-22s %-7s %s%n",
        //     "-".repeat(3),
        //     "-".repeat(15),
        //     "-".repeat(22),
        //     "-".repeat(7),
        //     "-".repeat(18));
        System.out.printf("%-3s %-13s %-19s%-7s%n",
            "-".repeat(3),
            "-".repeat(13),
            "-".repeat(19),
            "-".repeat(6));

        int coursePointNo = 0;
        int printedCoursePointNo = 0;
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;

            CoursePointMesg coursePointMesg = new CoursePointMesg(mesg);

            String timeStr = coursePointMesg.getTimestamp() != null
                ? FitDateTime.toString(coursePointMesg.getTimestamp().getTimestamp(), fitFile.getDiffMinutesLocalUTC())
                : "-";

            CoursePoint coursePoint = coursePointMesg.getType();
            if (typeFilter != null && coursePoint != typeFilter) {
                continue;
            }
            String typeStr = coursePoint != null ? CoursePoint.getStringFromValue(coursePoint) : "-";

            String nameStr = coursePointMesg.getName() != null ? coursePointMesg.getName() : "-";
            String distStr = coursePointMesg.getDistance() != null ? String.format("%1$.0fm", coursePointMesg.getDistance()) : "-";
            String favStr = coursePointMesg.getFavorite() != null ? coursePointMesg.getFavorite().toString() : "-";

            String positionStr = "-";
            if (coursePointMesg.getPositionLat() != null && coursePointMesg.getPositionLong() != null) {
                positionStr = String.format("%.5f, %.5f",
                    GeoUtils.fromSemicircles(coursePointMesg.getPositionLat()),
                    GeoUtils.fromSemicircles(coursePointMesg.getPositionLong()));
            }

            // System.out.printf("%-3d %-15s %-22s %-7s %s%n",
            //     coursePointNo,
            //     typeStr,
            //     nameStr,
            //     distStr,
            //     positionStr);
            System.out.printf("%-3d %-13s %-19s%-7s%n",
                coursePointNo,
                typeStr,
                nameStr,
                distStr);

            printedCoursePointNo++;
        }

        if (printedCoursePointNo == 0) {
            System.out.println(emptyMessage);
        }

        System.out.println("-------------------------------------------");
        System.out.println("Number of course points: " + printedCoursePointNo);
    }
    // =================================================================================
    private void printCPointsInputList(CoursePoint typeFilter, String title, String emptyMessage) {
        List<Mesg> allMesg = fitFile.getAllMesg();

        System.out.println();
        System.out.println("===========================================");
        System.out.println(title);
        System.out.println("-------------------------------------------");
        System.out.printf("%-3s%n%-19s%n",
            "No",
            "Name");
        System.out.printf("%-13s%n",
            "-".repeat(19));

        int coursePointNo = 0;
        int printedCoursePointNo = 0;
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;

            CoursePointMesg coursePointMesg = new CoursePointMesg(mesg);

            String timeStr = coursePointMesg.getTimestamp() != null
                ? FitDateTime.toString(coursePointMesg.getTimestamp().getTimestamp(), fitFile.getDiffMinutesLocalUTC())
                : "-";

            CoursePoint coursePoint = coursePointMesg.getType();
            if (typeFilter != null && coursePoint != typeFilter) {
                continue;
            }
            String typeStr = coursePoint != null ? CoursePoint.getStringFromValue(coursePoint) : "-";

            String nameStr = coursePointMesg.getName() != null ? coursePointMesg.getName() : "-";
            String distStr = coursePointMesg.getDistance() != null ? String.format("%1$.0fm", coursePointMesg.getDistance()) : "-";
            String favStr = coursePointMesg.getFavorite() != null ? coursePointMesg.getFavorite().toString() : "-";

            String positionStr = "-";
            if (coursePointMesg.getPositionLat() != null && coursePointMesg.getPositionLong() != null) {
                positionStr = String.format("%.5f, %.5f",
                    GeoUtils.fromSemicircles(coursePointMesg.getPositionLat()),
                    GeoUtils.fromSemicircles(coursePointMesg.getPositionLong()));
            }

            System.out.printf("%-3d%n%-19s%n",
                coursePointNo,
                cleanStringFromDashAndSpaces(nameStr));

            printedCoursePointNo++;
        }

        if (printedCoursePointNo == 0) {
            System.out.println(emptyMessage);
        }

        System.out.println("-------------------------------------------");
        System.out.println("Number of course points: " + printedCoursePointNo);
    }
    // =================================================================================
    private String cleanStringFromDashAndSpaces(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("-", "").replace(" ", "").replace("/", "");
    }

}