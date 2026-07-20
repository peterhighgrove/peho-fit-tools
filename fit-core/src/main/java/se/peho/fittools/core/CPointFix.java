package se.peho.fittools.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garmin.fit.CoursePoint;
import com.garmin.fit.CoursePointMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;

public class CPointFix {

    private final FitFile fitFile;

    public CPointFix(FitFile fitFile) {
        this.fitFile = fitFile;
    }
    // =================================================================================
    public void changeCPointTypes(Scanner sc) {
        
        CPointReportGenerator reportGenerator = new CPointReportGenerator(fitFile);
        reportGenerator.printGenericCPointsInputList();

        printCPointTypeOptions();

        while (true) {
            Integer cPointNo = InputHelper.askForNumber("Enter CPoint number to change", sc);
            if (cPointNo == null) return;

            Mesg coursePointMesg = getCoursePointMesgByNo(cPointNo);
            if (coursePointMesg == null) {
                System.out.println("==XX> CPoint number must be within range. Enter a new CPoint number.");
                continue;
            }

            Integer typeValue = InputHelper.askForNumber("Enter new CPoint type number", sc);
            if (typeValue == null) return;

            CoursePoint newType = CoursePoint.getByValue(typeValue.shortValue());
            if (newType == CoursePoint.INVALID) {
                System.out.println("==XX> Not a valid CPoint type number. Enter a new CPoint type number.");
                continue;
            }

            CoursePointMesg typedCoursePointMesg = new CoursePointMesg(coursePointMesg);
            CoursePoint oldType = typedCoursePointMesg.getType();
            coursePointMesg.setFieldValue(CoursePointMesg.TypeFieldNum, newType.getValue());

            fitFile.clearTempUpdateLog();
            fitFile.appendTempUpdateLogLn("COURSE POINT - CHANGE TYPE");
            fitFile.appendTempUpdateLogLn("--------------------------------");
            fitFile.appendTempUpdateLogLn("Changed CPoint no: " + cPointNo);
            fitFile.appendTempUpdateLogLn("-- Type changed from " + coursePointName(oldType) + " to " + coursePointName(newType));

            System.out.println(fitFile.getTempUpdateLog());
            fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
        }
    }
    // =================================================================================
    public void changeCPointName(Scanner sc) {
        while (true) {
            Integer cPointNo = InputHelper.askForNumber("Enter CPoint number to change", sc);
            if (cPointNo == null) return;

            Mesg coursePointMesg = getCoursePointMesgByNo(cPointNo);
            if (coursePointMesg == null) {
                System.out.println("==XX> CPoint number must be within range. Enter a new CPoint number.");
                continue;
            }

            String oldName = coursePointMesg.getFieldStringValue(CoursePointMesg.NameFieldNum);
            String newName = InputHelper.askForString("Enter new CPoint name", sc);
            if (newName == null) return;

            coursePointMesg.setFieldValue(CoursePointMesg.NameFieldNum, newName);

            fitFile.clearTempUpdateLog();
            fitFile.appendTempUpdateLogLn("COURSE POINT - CHANGE NAME");
            fitFile.appendTempUpdateLogLn("--------------------------------");
            fitFile.appendTempUpdateLogLn("Changed CPoint no: " + cPointNo);
            fitFile.appendTempUpdateLogLn("-- Name changed from '" + (oldName != null ? oldName : "") + "' to '" + newName + "'");

            System.out.println(fitFile.getTempUpdateLog());
            fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
            return;
        }
    }
    // =================================================================================
    public void changeCPointNamesFromAbbrevList() {
        List<AbbrevRule> abbrevRules = loadAbbrevRules();
        if (abbrevRules.isEmpty()) {
            System.out.println("==XX> No course point abbreviation rules found.");
            return;
        }

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - ABBREVIATE NAMES");
        fitFile.appendTempUpdateLogLn("--------------------------------");

        int changedCount = 0;
        int coursePointNo = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            String originalName = mesg.getFieldStringValue(CoursePointMesg.NameFieldNum);
            if (originalName == null || originalName.trim().isEmpty()) {
                continue;
            }

            AbbrevRule matchedRule = findFirstMatchingRule(originalName, abbrevRules);
            if (matchedRule == null) {
                continue;
            }

            String newName = applyAbbrevRule(originalName, matchedRule);
            if (newName.equals(originalName)) {
                continue;
            }

            mesg.setFieldValue(CoursePointMesg.NameFieldNum, newName);
            changedCount++;
            fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " -> " + originalName + " => " + newName);
        }

        fitFile.appendTempUpdateLogLn("-- Changed course point names: " + changedCount);
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }
    // =================================================================================
    public void insertTypeCharInCPointNames() {
        List<String> validStartChars = loadValidStartChars();
        if (validStartChars.isEmpty()) {
            System.out.println("==XX> No valid start chars found for cpi.");
            return;
        }

        List<TypeInsertRule> typeInsertRules = loadTypeInsertRules();
        if (typeInsertRules.isEmpty()) {
            System.out.println("==XX> No type insert rules found for cpi.");
            return;
        }

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - INSERT TYPE PREFIX");
        fitFile.appendTempUpdateLogLn("--------------------------------");

        int changedCount = 0;
        int coursePointNo = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            CoursePointMesg coursePointMesg = new CoursePointMesg(mesg);
            CoursePoint coursePointType = coursePointMesg.getType();
            if (coursePointType == null || coursePointType == CoursePoint.INVALID) {
                continue;
            }

            TypeInsertRule matchedRule = findFirstTypeInsertRule(coursePointType, typeInsertRules);
            if (matchedRule == null) {
                continue;
            }

            String originalName = coursePointMesg.getName();
            if (originalName == null) {
                originalName = "";
            }

            if (startsWithAny(originalName, validStartChars)) {
                continue;
            }

            String newName = matchedRule.insertChar + originalName;
            mesg.setFieldValue(CoursePointMesg.NameFieldNum, newName);

            fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo
                + " type:" + coursePointName(coursePointType)
                + " -> " + originalName + " => " + newName);
            changedCount++;
        }

        fitFile.appendTempUpdateLogLn("-- Changed course point names: " + changedCount);
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }
    // =================================================================================
    public void moveCPointsBack(Scanner sc) {
        Integer metersToMove = InputHelper.askForNumber("Enter number of meters to move back", sc);
        if (metersToMove == null) return;
        if (metersToMove <= 0) {
            System.out.println("==XX> Number of meters must be greater than zero.");
            return;
        }

        String updateGpsAnswer = InputHelper.askForString("Also update GPS point (y/n)", "n", sc);
        if (updateGpsAnswer == null) return;

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - MOVE BACK");
        fitFile.appendTempUpdateLogLn("--------------------------------");
        fitFile.appendTempUpdateLogLn("Move all course points back by: " + metersToMove + "m");
        fitFile.appendTempUpdateLogLn("Update GPS points: " + updateGpsAnswer);

        int changedCount = 0;
        int coursePointNo = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            CoursePointMesg typedCoursePointMesg = new CoursePointMesg(mesg);
            CoursePoint oldType = typedCoursePointMesg.getType();
            Float oldDistance = typedCoursePointMesg.getDistance();
            if (oldDistance == null) {
                fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " skipped because distance is missing");
                continue;
            }

            float newDistance = oldDistance - metersToMove;
            if (newDistance < 0f) {
                fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " skipped because new distance would be negative");
                continue;
            }

            mesg.setFieldValue(CoursePointMesg.DistanceFieldNum, newDistance);

            String oldName = typedCoursePointMesg.getName() != null ? typedCoursePointMesg.getName() : "-";
            String oldPosition = formatCoursePointPosition(typedCoursePointMesg);
            String newPosition = oldPosition;

            if (updateGpsAnswer.equalsIgnoreCase("y")) {
                Integer coursePointLat = typedCoursePointMesg.getPositionLat();
                Integer coursePointLon = typedCoursePointMesg.getPositionLong();
                int anchorRecordIx = findAnchorRecordIndex(coursePointLat, coursePointLon, oldDistance);
                if (anchorRecordIx >= 1) {
                    int previousRecordIx = findPreviousGpsRecordIndex(anchorRecordIx - 1);
                    if (previousRecordIx >= 0) {
                        Mesg previousRecord = fitFile.getRecordMesg().get(previousRecordIx);
                        Mesg anchorRecord = fitFile.getRecordMesg().get(anchorRecordIx);

                        Integer prevLat = previousRecord.getFieldIntegerValue(FitFile.REC_LAT);
                        Integer prevLon = previousRecord.getFieldIntegerValue(FitFile.REC_LON);
                        Integer anchorLat = anchorRecord.getFieldIntegerValue(FitFile.REC_LAT);
                        Integer anchorLon = anchorRecord.getFieldIntegerValue(FitFile.REC_LON);

                        if (prevLat != null && prevLon != null && anchorLat != null && anchorLon != null) {
                            double segmentLength = GeoUtils.distCalc(prevLat, prevLon, anchorLat, anchorLon);
                            double distanceFromPrevious = Math.max(0d, segmentLength - metersToMove);
                            double[] movedCoords = GeoUtils.interpolate(prevLat, prevLon, anchorLat, anchorLon, distanceFromPrevious);

                            mesg.setFieldValue(CoursePointMesg.PositionLatFieldNum, GeoUtils.toSemicircles(movedCoords[0]));
                            mesg.setFieldValue(CoursePointMesg.PositionLongFieldNum, GeoUtils.toSemicircles(movedCoords[1]));
                            newPosition = String.format("%.5f, %.5f", movedCoords[0], movedCoords[1]);
                        } else {
                            fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " GPS update skipped because the anchor records have no GPS data");
                        }
                    } else {
                        fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " GPS update skipped because no previous GPS record was found");
                    }
                } else {
                    fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " GPS update skipped because no anchor record was found");
                }
            }

            fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " -> " + oldName + " / " + coursePointName(oldType));
            fitFile.appendTempUpdateLogLn("-- Distance changed from " + oldDistance + "m to " + newDistance + "m");
            fitFile.appendTempUpdateLogLn("-- Position: " + oldPosition + " -> " + newPosition);
            changedCount++;
        }

        fitFile.appendTempUpdateLogLn("-- Changed course points: " + changedCount);
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }
    // =================================================================================
    public void shiftGpsPointsSideways(Scanner sc) {
        ShiftSide shiftSide = askForShiftSide(sc);
        if (shiftSide == null) return;

        Integer metersToShift = InputHelper.askForNumber("Enter number of meters to shift", sc);
        if (metersToShift == null) return;
        if (metersToShift <= 0) {
            System.out.println("==XX> Number of meters must be greater than zero.");
            return;
        }

        List<OriginalGpsPoint> originalRecordGps = new ArrayList<>();
        for (Mesg record : fitFile.getRecordMesg()) {
            originalRecordGps.add(OriginalGpsPoint.fromRecord(record));
        }

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - SHIFT ROUTE SIDEWAYS");
        fitFile.appendTempUpdateLogLn("--------------------------------");
        fitFile.appendTempUpdateLogLn("Shift side: " + shiftSide);
        fitFile.appendTempUpdateLogLn("Shift distance: " + metersToShift + "m");

        Map<GpsKey, ShiftedPoint> shiftedRecordLookup = new HashMap<>();
        int changedRecords = 0;
        for (int recordIx = 0; recordIx < fitFile.getRecordMesg().size(); recordIx++) {
            Mesg record = fitFile.getRecordMesg().get(recordIx);
            OriginalGpsPoint originalPoint = originalRecordGps.get(recordIx);
            if (originalPoint == null) {
                continue;
            }

            ShiftedPoint shiftedPoint = shiftPointForRecord(recordIx, originalRecordGps, metersToShift, shiftSide);
            if (shiftedPoint == null) {
                fitFile.appendTempUpdateLogLn("Record no: " + (recordIx + 1) + " skipped because route direction could not be determined");
                continue;
            }

            record.setFieldValue(FitFile.REC_LAT, GeoUtils.toSemicircles(shiftedPoint.lat));
            record.setFieldValue(FitFile.REC_LON, GeoUtils.toSemicircles(shiftedPoint.lon));
            shiftedRecordLookup.putIfAbsent(originalPoint.key(), shiftedPoint);
            changedRecords++;

            fitFile.appendTempUpdateLogLn("Record no: " + (recordIx + 1)
                + " -> " + formatGpsPoint(originalPoint.lat, originalPoint.lon)
                + " => " + formatGpsPoint(shiftedPoint.lat, shiftedPoint.lon));
        }

        int changedCoursePoints = 0;
        int coursePointNo = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            Integer originalLatSemi = mesg.getFieldIntegerValue(CoursePointMesg.PositionLatFieldNum);
            Integer originalLonSemi = mesg.getFieldIntegerValue(CoursePointMesg.PositionLongFieldNum);
            if (originalLatSemi == null || originalLonSemi == null) {
                fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " skipped because GPS position is missing");
                continue;
            }

            GpsKey key = new GpsKey(originalLatSemi, originalLonSemi);
            ShiftedPoint shiftedPoint = shiftedRecordLookup.get(key);
            String shiftSource = "same GPS point";

            if (shiftedPoint == null) {
                int closestRecordIx = findClosestGpsRecordIndex(originalLatSemi, originalLonSemi, originalRecordGps);
                if (closestRecordIx < 0) {
                    fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " skipped because no nearby record GPS was found");
                    continue;
                }

                shiftedPoint = shiftPointAtLocation(originalLatSemi, originalLonSemi, closestRecordIx, originalRecordGps, metersToShift, shiftSide);
                if (shiftedPoint == null) {
                    fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " skipped because nearby record direction could not be determined");
                    continue;
                }

                shiftSource = "closest record no: " + (closestRecordIx + 1);
            }

            mesg.setFieldValue(CoursePointMesg.PositionLatFieldNum, GeoUtils.toSemicircles(shiftedPoint.lat));
            mesg.setFieldValue(CoursePointMesg.PositionLongFieldNum, GeoUtils.toSemicircles(shiftedPoint.lon));
            changedCoursePoints++;

            fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo
                + " (" + shiftSource + ") -> " + formatGpsPoint(GeoUtils.fromSemicircles(originalLatSemi), GeoUtils.fromSemicircles(originalLonSemi))
                + " => " + formatGpsPoint(shiftedPoint.lat, shiftedPoint.lon));
        }

        fitFile.appendTempUpdateLogLn("-- Changed record GPS points: " + changedRecords);
        fitFile.appendTempUpdateLogLn("-- Changed course points: " + changedCoursePoints);
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }
    // =================================================================================
    public void cleanCPointNamePrefixes() {
        List<String> prefixes = loadPrefixesToClean();
        if (prefixes.isEmpty()) {
            System.out.println("==XX> No prefixes found to clean.");
            return;
        }

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - CLEAN NAME PREFIXES");
        fitFile.appendTempUpdateLogLn("--------------------------------");
        fitFile.appendTempUpdateLogLn("Prefixes to remove:");
        for (String prefix : prefixes) {
            fitFile.appendTempUpdateLogLn("  '" + prefix + "'");
        }
        fitFile.appendTempUpdateLogLn("--------------------------------");

        int changedCount = 0;
        int coursePointNo = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            String originalName = mesg.getFieldStringValue(CoursePointMesg.NameFieldNum);
            if (originalName == null || originalName.isEmpty()) {
                continue;
            }

            String newName = removeMatchingPrefix(originalName, prefixes);
            if (newName.equals(originalName)) {
                continue;
            }

            mesg.setFieldValue(CoursePointMesg.NameFieldNum, newName);
            changedCount++;
            fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " -> '" + originalName + "' => '" + newName + "'");
        }

        fitFile.appendTempUpdateLogLn("-- Changed course point names: " + changedCount);
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }

    // =================================================================================

    private void printCPointTypeOptions() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("COURSE POINT TYPE OPTIONS");
        System.out.println("----------------------------------------");
        List<String> coursePointTypeLabels = new ArrayList<>();
        for (CoursePoint coursePoint : CoursePoint.values()) {
            if (coursePoint == CoursePoint.INVALID) {
                continue;
            }
            coursePointTypeLabels.add(String.format("%3d %s", coursePoint.getValue(), coursePointName(coursePoint)));
        }

        int rows = (coursePointTypeLabels.size() + 1) / 2;
        for (int i = 0; i < rows; i++) {
            String firstColumn = coursePointTypeLabels.get(i);
            String secondColumn = (i + rows < coursePointTypeLabels.size()) ? coursePointTypeLabels.get(i + rows) : "";
            System.out.printf("%-19s%-19s%n", firstColumn, secondColumn);
        }
        System.out.println("----------------------------------------");
    }
    // =================================================================================
    private Mesg getCoursePointMesgByNo(int cPointNo) {
        int coursePointCounter = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointCounter++;
            if (coursePointCounter == cPointNo) {
                return mesg;
            }
        }

        return null;
    }
    // =================================================================================
    private String coursePointName(CoursePoint coursePoint) {
        return coursePoint != null ? CoursePoint.getStringFromValue(coursePoint) : "-";
    }
    // =================================================================================
    private String formatCoursePointPosition(CoursePointMesg coursePointMesg) {
        if (coursePointMesg.getPositionLat() == null || coursePointMesg.getPositionLong() == null) {
            return "-";
        }
        return String.format("%.5f, %.5f",
            GeoUtils.fromSemicircles(coursePointMesg.getPositionLat()),
            GeoUtils.fromSemicircles(coursePointMesg.getPositionLong()));
    }
    // =================================================================================
    private int findAnchorRecordIndex(Integer coursePointLat, Integer coursePointLon, Float coursePointDistance) {
        if (coursePointLat != null && coursePointLon != null) {
            int exactIndex = findRecordIndexWithSameGps(coursePointLat, coursePointLon);
            if (exactIndex >= 0) {
                return exactIndex;
            }
        }

        return findRecordIndexWithClosestDistance(coursePointDistance);
    }
    // =================================================================================
    private int findRecordIndexWithSameGps(Integer lat, Integer lon) {
        for (int i = 0; i < fitFile.getRecordMesg().size(); i++) {
            Mesg record = fitFile.getRecordMesg().get(i);
            if (lat.equals(record.getFieldIntegerValue(FitFile.REC_LAT))
                    && lon.equals(record.getFieldIntegerValue(FitFile.REC_LON))) {
                return i;
            }
        }
        return -1;
    }
    // =================================================================================
    private int findRecordIndexWithClosestDistance(Float coursePointDistance) {
        if (coursePointDistance == null) {
            return -1;
        }

        int closestIndex = -1;
        double closestDiff = Double.MAX_VALUE;
        for (int i = 0; i < fitFile.getRecordMesg().size(); i++) {
            Mesg record = fitFile.getRecordMesg().get(i);
            Float recordDistance = record.getFieldFloatValue(FitFile.REC_DIST);
            if (recordDistance == null) {
                continue;
            }

            double diff = Math.abs(recordDistance - coursePointDistance);
            if (diff < closestDiff) {
                closestDiff = diff;
                closestIndex = i;
            }
        }

        return closestIndex;
    }
    // =================================================================================
    private int findPreviousGpsRecordIndex(int startIndex) {
        for (int i = startIndex; i >= 0; i--) {
            Mesg record = fitFile.getRecordMesg().get(i);
            if (record.getFieldIntegerValue(FitFile.REC_LAT) != null
                    && record.getFieldIntegerValue(FitFile.REC_LON) != null) {
                return i;
            }
        }
        return -1;
    }
    // =================================================================================
    private int findClosestGpsRecordIndex(Integer lat, Integer lon, List<OriginalGpsPoint> originalRecordGps) {
        int closestIndex = -1;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < originalRecordGps.size(); i++) {
            OriginalGpsPoint originalPoint = originalRecordGps.get(i);
            if (originalPoint == null) {
                continue;
            }

            double distance = GeoUtils.distCalc(
                GeoUtils.fromSemicircles(lat),
                GeoUtils.fromSemicircles(lon),
                originalPoint.lat,
                originalPoint.lon
            );
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }
    // =================================================================================
    private ShiftedPoint shiftPointForRecord(int recordIx, List<OriginalGpsPoint> originalRecordGps, int metersToShift, ShiftSide shiftSide) {
        OriginalGpsPoint currentPoint = originalRecordGps.get(recordIx);
        if (currentPoint == null) {
            return null;
        }

        Double bearing = resolveRouteBearing(recordIx, originalRecordGps);
        if (bearing == null) {
            return null;
        }

        return shiftPointAtLocation(currentPoint.latSemi, currentPoint.lonSemi, bearing, metersToShift, shiftSide);
    }
    // =================================================================================
    private ShiftedPoint shiftPointAtLocation(Integer latSemi, Integer lonSemi, int referenceRecordIx, List<OriginalGpsPoint> originalRecordGps, int metersToShift, ShiftSide shiftSide) {
        Double bearing = resolveRouteBearing(referenceRecordIx, originalRecordGps);
        if (bearing == null) {
            return null;
        }

        return shiftPointAtLocation(latSemi, lonSemi, bearing, metersToShift, shiftSide);
    }
    // =================================================================================
    private ShiftedPoint shiftPointAtLocation(Integer latSemi, Integer lonSemi, Double bearing, int metersToShift, ShiftSide shiftSide) {
        if (latSemi == null || lonSemi == null || bearing == null) {
            return null;
        }

        double originLat = GeoUtils.fromSemicircles(latSemi);
        double originLon = GeoUtils.fromSemicircles(lonSemi);
        double offsetBearing = shiftSide == ShiftSide.LEFT ? bearing - 90.0 : bearing + 90.0;
        double[] shiftedCoords = GeoUtils.destinationPoint(originLat, originLon, offsetBearing, metersToShift);
        return new ShiftedPoint(shiftedCoords[0], shiftedCoords[1]);
    }
    // =================================================================================
    private Double resolveRouteBearing(int recordIx, List<OriginalGpsPoint> originalRecordGps) {
        OriginalGpsPoint currentPoint = originalRecordGps.get(recordIx);
        if (currentPoint == null) {
            return null;
        }

        int previousIndex = findPreviousGpsRecordIndexInList(recordIx - 1, originalRecordGps);
        int nextIndex = findNextGpsRecordIndexInList(recordIx + 1, originalRecordGps);

        if (previousIndex >= 0 && nextIndex >= 0) {
            OriginalGpsPoint previousPoint = originalRecordGps.get(previousIndex);
            OriginalGpsPoint nextPoint = originalRecordGps.get(nextIndex);
            if (!previousPoint.sameLocation(nextPoint)) {
                return GeoUtils.bearingDegrees(previousPoint.lat, previousPoint.lon, nextPoint.lat, nextPoint.lon);
            }
        }

        if (previousIndex >= 0) {
            OriginalGpsPoint previousPoint = originalRecordGps.get(previousIndex);
            if (!previousPoint.sameLocation(currentPoint)) {
                return GeoUtils.bearingDegrees(previousPoint.lat, previousPoint.lon, currentPoint.lat, currentPoint.lon);
            }
        }

        if (nextIndex >= 0) {
            OriginalGpsPoint nextPoint = originalRecordGps.get(nextIndex);
            if (!currentPoint.sameLocation(nextPoint)) {
                return GeoUtils.bearingDegrees(currentPoint.lat, currentPoint.lon, nextPoint.lat, nextPoint.lon);
            }
        }

        return null;
    }
    // =================================================================================
    private int findPreviousGpsRecordIndexInList(int startIndex, List<OriginalGpsPoint> originalRecordGps) {
        for (int i = startIndex; i >= 0; i--) {
            if (originalRecordGps.get(i) != null) {
                return i;
            }
        }
        return -1;
    }
    // =================================================================================
    private int findNextGpsRecordIndexInList(int startIndex, List<OriginalGpsPoint> originalRecordGps) {
        for (int i = startIndex; i < originalRecordGps.size(); i++) {
            if (originalRecordGps.get(i) != null) {
                return i;
            }
        }
        return -1;
    }
    // =================================================================================
    private ShiftSide askForShiftSide(Scanner sc) {
        while (true) {
            String answer = InputHelper.askForString("Enter shift side (L/R)", sc);
            if (answer == null) {
                return null;
            }

            if (answer.equalsIgnoreCase("l")) {
                return ShiftSide.LEFT;
            }
            if (answer.equalsIgnoreCase("r")) {
                return ShiftSide.RIGHT;
            }

            System.out.println("==XX> Side must be L or R. Enter a new side.");
        }
    }
    // =================================================================================
    private String formatGpsPoint(double lat, double lon) {
        return String.format("%.5f, %.5f", lat, lon);
    }
    // =================================================================================
    private List<String> loadValidStartChars() {
        List<String> validStartChars = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/cpoint-valid-start-chars.txt")) {
            if (inputStream == null) {
                return validStartChars;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }
                    validStartChars.add(trimmed);
                }
            }
        } catch (IOException e) {
            System.out.println("==XX> Could not read valid start chars for cpi: " + e.getMessage());
        }

        return validStartChars;
    }
    // =================================================================================
    private List<TypeInsertRule> loadTypeInsertRules() {
        List<TypeInsertRule> rules = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/cpoint-type-insert-chars.txt")) {
            if (inputStream == null) {
                return rules;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }

                    String[] parts = trimmed.split(",", 2);
                    if (parts.length < 2) {
                        continue;
                    }

                    String typeName = parts[0].trim();
                    String insertChar = parts[1].trim();
                    if (typeName.isEmpty() || insertChar.isEmpty()) {
                        continue;
                    }

                    CoursePoint coursePointType;
                    try {
                        coursePointType = CoursePoint.valueOf(typeName);
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }

                    rules.add(new TypeInsertRule(coursePointType, insertChar));
                }
            }
        } catch (IOException e) {
            System.out.println("==XX> Could not read type insert rules for cpi: " + e.getMessage());
        }

        return rules;
    }
    // =================================================================================
    private TypeInsertRule findFirstTypeInsertRule(CoursePoint coursePointType, List<TypeInsertRule> rules) {
        for (TypeInsertRule rule : rules) {
            if (rule.coursePointType == coursePointType) {
                return rule;
            }
        }
        return null;
    }

    private boolean startsWithAny(String value, List<String> prefixes) {
        for (String prefix : prefixes) {
            if (value.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    // =================================================================================
    private List<String> loadPrefixesToClean() {
        List<String> prefixes = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/cpoint-abbrev.txt")) {
            if (inputStream == null) {
                return prefixes;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Don't trim - we want to preserve trailing spaces in the prefix
                    if (line.isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    // Split on the first comma only
                    int commaIndex = line.indexOf(',');
                    if (commaIndex <= 0) {
                        continue;
                    }

                    String prefix = line.substring(0, commaIndex);
                    if (!prefix.isEmpty()) {
                        prefixes.add(prefix);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("==XX> Could not read course point prefix list: " + e.getMessage());
        }

        return prefixes;
    }
    // =================================================================================
    private String removeMatchingPrefix(String name, List<String> prefixes) {
        // Try to match and remove prefixes from longest to shortest
        // to avoid removing substrings of longer prefixes
        List<String> sortedPrefixes = new ArrayList<>(prefixes);
        sortedPrefixes.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String prefix : sortedPrefixes) {
            if (name.startsWith(prefix)) {
                String result = name.substring(prefix.length());
                // Also remove any leading spaces that remain
                result = result.replaceAll("^\\s+", "");
                return result;
            }
        }

        // Check for single L or R character at start
        if (name.length() >= 1 && (name.charAt(0) == 'L' || name.charAt(0) == 'R')) {
            // If it's a single character, remove it
            if (name.length() == 1) {
                return "";
            }
            // If there are more characters and next char is a space, remove "L " or "R "
            if (name.charAt(1) == ' ') {
                String result = name.substring(2);
                // Also remove any additional leading spaces
                result = result.replaceAll("^\\s+", "");
                return result;
            }
        }

        return name;
    }

    // =================================================================================
    private List<AbbrevRule> loadAbbrevRules() {
        List<AbbrevRule> rules = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/cpoint-abbrev.txt")) {
            if (inputStream == null) {
                return rules;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                        continue;
                    }

                    String[] parts = trimmedLine.split(",", 2);
                    String search = parts[0]; //.trim();
                    String replacement = parts.length > 1 ? parts[1] : ""; // .trim()
                    if (!search.isEmpty()) {
                        rules.add(new AbbrevRule(search, replacement));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("==XX> Could not read course point abbreviation list: " + e.getMessage());
        }

        return rules;
    }

    // =================================================================================
    private AbbrevRule findFirstMatchingRule(String name, List<AbbrevRule> rules) {
        for (AbbrevRule rule : rules) {
            //if (containsIgnoreCase(name, rule.search)) {
            if (startsWithIgnoreCase(name, rule.search)) {
                return rule;
            } else if(name.trim().length() == 1 && startsWithIgnoreCase(name, rule.search.trim())) {
                return rule;
            }
        }
        return null;
    }

    // =================================================================================
    private String applyAbbrevRule(String name, AbbrevRule rule) {

        // If the name is a single character, trim the search string to avoid matching trailing spaces f.i. "L " -> "L"
        String searchString = rule.search;
        if (name.length() == 1) { 
            searchString = searchString.trim();
        }

        Pattern pattern = Pattern.compile(Pattern.quote(searchString) + "\\s*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        String replaced = matcher.replaceFirst(Matcher.quoteReplacement(rule.replacement));
        //System.out.println("==>> pattern: " + pattern.pattern() + ", name: " + name + ", replaced: " + replaced);
        //System.out.println("==>>>> returning:" + replaced.replaceFirst("^\\s+", "").replaceAll("\\s{2,}", " ").trim() + "<<<<");
        return replaced.replaceFirst("^\\s+", "").replaceAll("\\s{2,}", " ").trim();
    }

    // =================================================================================
    private boolean startsWithIgnoreCase(String text, String search) {
        return text.toLowerCase().startsWith(search.toLowerCase());
    }

    // =================================================================================
    private static class AbbrevRule {
        private final String search;
        private final String replacement;

        private AbbrevRule(String search, String replacement) {
            this.search = search;
            this.replacement = replacement;
        }
    }

    // =================================================================================
    private static class TypeInsertRule {
        private final CoursePoint coursePointType;
        private final String insertChar;

        private TypeInsertRule(CoursePoint coursePointType, String insertChar) {
            this.coursePointType = coursePointType;
            this.insertChar = insertChar;
        }
    }

    // =================================================================================
    private enum ShiftSide {
        LEFT,
        RIGHT
    }
    // =================================================================================
    private static class GpsKey {
        private final int latSemi;
        private final int lonSemi;

        private GpsKey(int latSemi, int lonSemi) {
            this.latSemi = latSemi;
            this.lonSemi = lonSemi;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof GpsKey)) return false;
            GpsKey gpsKey = (GpsKey) other;
            return latSemi == gpsKey.latSemi && lonSemi == gpsKey.lonSemi;
        }

        @Override
        public int hashCode() {
            return 31 * latSemi + lonSemi;
        }
    }

    // =================================================================================
    private static class ShiftedPoint {
        private final double lat;
        private final double lon;

        private ShiftedPoint(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    // =================================================================================
    private static class OriginalGpsPoint {
        private final Integer latSemi;
        private final Integer lonSemi;
        private final double lat;
        private final double lon;

        private OriginalGpsPoint(Integer latSemi, Integer lonSemi) {
            this.latSemi = latSemi;
            this.lonSemi = lonSemi;
            this.lat = GeoUtils.fromSemicircles(latSemi);
            this.lon = GeoUtils.fromSemicircles(lonSemi);
        }

        private static OriginalGpsPoint fromRecord(Mesg record) {
            Integer latSemi = record.getFieldIntegerValue(FitFile.REC_LAT);
            Integer lonSemi = record.getFieldIntegerValue(FitFile.REC_LON);
            if (latSemi == null || lonSemi == null) {
                return null;
            }

            return new OriginalGpsPoint(latSemi, lonSemi);
        }

        private GpsKey key() {
            return new GpsKey(latSemi, lonSemi);
        }

        private boolean sameLocation(OriginalGpsPoint other) {
            return other != null && latSemi.equals(other.latSemi) && lonSemi.equals(other.lonSemi);
        }
    }
}