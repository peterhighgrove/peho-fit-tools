package se.peho.fittools.core;

import java.util.List;

import com.garmin.fit.CoursePoint;
import com.garmin.fit.CoursePointMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import se.peho.fittools.core.strings.Tstr;

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
        System.out.println("=".repeat(3+13+19+7+8));
        System.out.println(title);
        System.out.println("-".repeat(3+13+19+7+8));
        System.out.printf("%-3s%-13s%-19s%-7s%-8s%n",
            "No",
            "Type",
            "Name",
            "Dist",
            "Time"
        );
        System.out.printf("%-3s%-13s%-19s%-7s%-8s%n",
            "-".repeat(2),
            "-".repeat(12),
            "-".repeat(18),
            "-".repeat(6),
            "-".repeat(8)
        );

        int coursePointNo = 0;
        int printedCoursePointNo = 0;
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;

            CoursePointMesg coursePointMesg = new CoursePointMesg(mesg);

            String timeStr = coursePointMesg.getTimestamp() != null
                ? new Tstr(coursePointMesg.getTimestamp().getTimestamp(), fitFile.getDiffMinutesLocalUTC()).get()
                : "-";

            CoursePoint coursePoint = coursePointMesg.getType();
            if (typeFilter != null && coursePoint != typeFilter) {
                continue;
            }
            String typeStr = coursePoint != null ? CoursePoint.getStringFromValue(coursePoint) : "-";

            String nameStr = coursePointMesg.getName() != null ? coursePointMesg.getName() : "-";
            String distStr = coursePointMesg.getDistance() != null ? String.format("%1$.0fm", coursePointMesg.getDistance()) : "-";

            System.out.printf("%-3d%-13s%-19s%-7s%-8s%n",
                coursePointNo,
                typeStr,
                nameStr,
                distStr,
                timeStr
            );

            printedCoursePointNo++;
        }

        if (printedCoursePointNo == 0) {
            System.out.println(emptyMessage);
        }

        System.out.println("-".repeat(3+13+19+7+8));
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

            CoursePoint coursePoint = coursePointMesg.getType();
            if (typeFilter != null && coursePoint != typeFilter) {
                continue;
            }

            String nameStr = coursePointMesg.getName() != null ? coursePointMesg.getName() : "-";

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