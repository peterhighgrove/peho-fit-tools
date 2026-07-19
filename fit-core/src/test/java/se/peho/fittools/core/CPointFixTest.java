package se.peho.fittools.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Scanner;

import org.junit.Test;

import com.garmin.fit.CoursePointMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.RecordMesg;

public class CPointFixTest {

    @Test
    public void changeCPointName_updatesTheSelectedCoursePointName() {
        FitFile fitFile = new FitFile();

        CoursePointMesg coursePointMesg = coursePoint(59.00000, 18.00100);
        coursePointMesg.setName("Old name");
        fitFile.getAllMesg().add((Mesg) coursePointMesg);

        try (Scanner sc = new Scanner("1\nNew name\n")) {
            fitFile.getCPointFix().changeCPointName(sc);
        }

        assertEquals("New name", coursePointMesg.getName());
    }

    @Test
    public void shiftGpsPointsSideways_movesRecordsAndCoursePointsToTheLeft() {
        FitFile fitFile = new FitFile();

        fitFile.getRecordMesg().add(record(59.00000, 18.00000));
        fitFile.getRecordMesg().add(record(59.00000, 18.00100));
        fitFile.getRecordMesg().add(record(59.00000, 18.00200));

        CoursePointMesg matchedCoursePoint = coursePoint(59.00000, 18.00100);
        CoursePointMesg nearbyCoursePoint = coursePoint(59.00000, 18.00120);
        fitFile.getAllMesg().add((Mesg) fitFile.getRecordMesg().get(0));
        fitFile.getAllMesg().add((Mesg) fitFile.getRecordMesg().get(1));
        fitFile.getAllMesg().add((Mesg) fitFile.getRecordMesg().get(2));
        fitFile.getAllMesg().add((Mesg) matchedCoursePoint);
        fitFile.getAllMesg().add((Mesg) nearbyCoursePoint);

        try (Scanner sc = new Scanner("l\n10\n")) {
            fitFile.getCPointFix().shiftGpsPointsSideways(sc);
        }

        double record0Lat = GeoUtils.fromSemicircles(fitFile.getRecordMesg().get(0).getFieldIntegerValue(FitFile.REC_LAT));
        double record1Lat = GeoUtils.fromSemicircles(fitFile.getRecordMesg().get(1).getFieldIntegerValue(FitFile.REC_LAT));
        double record2Lat = GeoUtils.fromSemicircles(fitFile.getRecordMesg().get(2).getFieldIntegerValue(FitFile.REC_LAT));

        assertTrue(record0Lat > 59.0);
        assertTrue(record1Lat > 59.0);
        assertTrue(record2Lat > 59.0);

        assertEquals(fitFile.getRecordMesg().get(1).getFieldIntegerValue(FitFile.REC_LAT), matchedCoursePoint.getPositionLat());
        assertEquals(fitFile.getRecordMesg().get(1).getFieldIntegerValue(FitFile.REC_LON), matchedCoursePoint.getPositionLong());

        double nearbyLat = GeoUtils.fromSemicircles(nearbyCoursePoint.getPositionLat());
        assertTrue(nearbyLat > 59.0);
    }

    @Test
    public void shiftGpsPointsSideways_acceptsRightSideInput() {
        FitFile fitFile = new FitFile();

        fitFile.getRecordMesg().add(record(59.00000, 18.00000));
        fitFile.getRecordMesg().add(record(59.00000, 18.00100));
        fitFile.getRecordMesg().add(record(59.00000, 18.00200));
        fitFile.getAllMesg().addAll(fitFile.getRecordMesg());

        try (Scanner sc = new Scanner("r\n5\n")) {
            fitFile.getCPointFix().shiftGpsPointsSideways(sc);
        }

        double record1Lat = GeoUtils.fromSemicircles(fitFile.getRecordMesg().get(1).getFieldIntegerValue(FitFile.REC_LAT));
        assertTrue(record1Lat < 59.0);
    }

    private RecordMesg record(double lat, double lon) {
        RecordMesg recordMesg = new RecordMesg();
        recordMesg.setFieldValue(FitFile.REC_LAT, GeoUtils.toSemicircles(lat));
        recordMesg.setFieldValue(FitFile.REC_LON, GeoUtils.toSemicircles(lon));
        return recordMesg;
    }

    private CoursePointMesg coursePoint(double lat, double lon) {
        CoursePointMesg coursePointMesg = new CoursePointMesg();
        coursePointMesg.setFieldValue(CoursePointMesg.PositionLatFieldNum, GeoUtils.toSemicircles(lat));
        coursePointMesg.setFieldValue(CoursePointMesg.PositionLongFieldNum, GeoUtils.toSemicircles(lon));
        return coursePointMesg;
    }
}