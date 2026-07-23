package se.peho.fittools.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garmin.fit.CourseMesg;
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

            CoursePointMesg typedCoursePointMesg = new CoursePointMesg(coursePointMesg);
            CoursePoint oldType = typedCoursePointMesg.getType();
            if (oldType == null || oldType == CoursePoint.INVALID) {
                oldType = CoursePoint.GENERIC;
            }

            CoursePoint newType;
            while (true) {
                String typeInput = InputHelper.askForString(
                    "Enter new CPoint type (number/text)",
                    coursePointName(oldType),
                    sc);
                if (typeInput == null) {
                    return;
                }

                TypeResolution resolution = resolveCoursePointTypeInput(typeInput);
                if (!resolution.valid) {
                    System.out.println("==XX> " + resolution.message);
                    continue;
                }

                newType = resolution.coursePoint;
                break;
            }

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
    public void changeGenericCPointTypes(Scanner sc) {

        CPointReportGenerator reportGenerator = new CPointReportGenerator(fitFile);
        reportGenerator.printGenericCPointsInputList();

        List<GenericCPointEntry> genericCoursePoints = collectGenericCoursePoints();
        if (genericCoursePoints.isEmpty()) {
            System.out.println("==XX> No generic course points found in file.");
            return;
        }

        printCPointTypeOptions();

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - CHANGE TYPE GENERIC LOOP");
        fitFile.appendTempUpdateLogLn("--------------------------------");

        int changedCount = 0;
        for (GenericCPointEntry entry : genericCoursePoints) {
            CoursePointMesg coursePointMesg = new CoursePointMesg(entry.mesg);
            CoursePoint oldType = coursePointMesg.getType();
            if (oldType == null || oldType == CoursePoint.INVALID) {
                oldType = CoursePoint.GENERIC;
            }

            String shortLabel = buildShortLabel(coursePointMesg.getName());

            while (true) {
                String typeInput = InputHelper.askForString(
                    "[" + shortLabel + "] CPoint no " + entry.coursePointNo + " new type (number/text)",
                    coursePointName(oldType),
                    sc);
                if (typeInput == null) {
                    fitFile.appendTempUpdateLogLn("Stopped by user.");
                    fitFile.appendTempUpdateLogLn("-- Changed course points: " + changedCount);
                    System.out.println(fitFile.getTempUpdateLog());
                    fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
                    return;
                }

                TypeResolution resolution = resolveCoursePointTypeInput(typeInput);
                if (!resolution.valid) {
                    System.out.println("==XX> " + resolution.message);
                    continue;
                }

                CoursePoint newType = resolution.coursePoint;
                if (newType == oldType) {
                    fitFile.appendTempUpdateLogLn("CPoint no: " + entry.coursePointNo
                        + " [" + shortLabel + "] unchanged (" + coursePointName(oldType) + ")");
                } else {
                    entry.mesg.setFieldValue(CoursePointMesg.TypeFieldNum, newType.getValue());
                    changedCount++;
                    System.out.println("CPoint no: " + entry.coursePointNo
                        + " [" + shortLabel + "] " + coursePointName(oldType) + " -> " + coursePointName(newType));
                    fitFile.appendTempUpdateLogLn("CPoint no: " + entry.coursePointNo
                        + " [" + shortLabel + "] " + coursePointName(oldType) + " -> " + coursePointName(newType));
                }
                break;
            }
        }

        fitFile.appendTempUpdateLogLn("-- Changed course points: " + changedCount);
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
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
            String newName = InputHelper.askForString("Enter new CPoint name", oldName != null ? oldName : "", sc);
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
    public void changeCourseName(Scanner sc) {
        String currentName = null;
        int courseMesgCount = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE) {
                continue;
            }

            courseMesgCount++;
            if (currentName == null) {
                currentName = mesg.getFieldStringValue(CourseMesg.NameFieldNum);
            }
        }

        if (courseMesgCount == 0) {
            System.out.println("==XX> No COURSE message found in file.");
            return;
        }

        String newName = InputHelper.askForString("Enter new Course name", currentName != null ? currentName : "", sc);
        if (newName == null) {
            return;
        }

        int changedCount = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE) {
                continue;
            }

            mesg.setFieldValue(CourseMesg.NameFieldNum, newName);
            changedCount++;
        }

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE - CHANGE NAME");
        fitFile.appendTempUpdateLogLn("--------------------------------");
        fitFile.appendTempUpdateLogLn("Changed COURSE messages: " + changedCount);
        fitFile.appendTempUpdateLogLn("-- Name changed from '" + (currentName != null ? currentName : "") + "' to '" + newName + "'");

        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }
    // =================================================================================
    public void checkAndMoveCPointsAfterRecords(Scanner sc) {
        List<Mesg> allMesg = fitFile.getAllMesg();

        int firstRecordIx = findFirstMesgIndex(allMesg, MesgNum.RECORD);
        int lastRecordIx = findLastMesgIndex(allMesg, MesgNum.RECORD);
        if (firstRecordIx < 0 || lastRecordIx < 0) {
            System.out.println("==XX> No RECORD messages found in file.");
            return;
        }

        List<Integer> coursePointIndices = new ArrayList<>();
        for (int i = 0; i < allMesg.size(); i++) {
            if (allMesg.get(i).getNum() == MesgNum.COURSE_POINT) {
                coursePointIndices.add(i);
            }
        }

        if (coursePointIndices.isEmpty()) {
            System.out.println("==XX> No COURSE_POINT messages found in file.");
            return;
        }

        int beforeCount = 0;
        int betweenCount = 0;
        int afterCount = 0;
        for (Integer cPointIx : coursePointIndices) {
            if (cPointIx < firstRecordIx) {
                beforeCount++;
            } else if (cPointIx > lastRecordIx) {
                afterCount++;
            } else {
                betweenCount++;
            }
        }

        String placement;
        if (beforeCount == coursePointIndices.size()) {
            placement = "ALL BEFORE FIRST RECORD";
        } else if (betweenCount == coursePointIndices.size()) {
            placement = "ALL BETWEEN RECORDS";
        } else if (afterCount == coursePointIndices.size()) {
            placement = "ALL AFTER LAST RECORD";
        } else {
            placement = "MIXED";
        }

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - CHECK/MOVE POSITION");
        fitFile.appendTempUpdateLogLn("--------------------------------");
        fitFile.appendTempUpdateLogLn("Record range in allMesg: " + firstRecordIx + ".." + lastRecordIx);
        fitFile.appendTempUpdateLogLn("Course points total: " + coursePointIndices.size());
        fitFile.appendTempUpdateLogLn("-- Before first record: " + beforeCount);
        fitFile.appendTempUpdateLogLn("-- Between records: " + betweenCount);
        fitFile.appendTempUpdateLogLn("-- After last record: " + afterCount);
        fitFile.appendTempUpdateLogLn("Placement result: " + placement);
        System.out.println(fitFile.getTempUpdateLog());

        String answer = InputHelper.askForString(
            "Move all course points to after records and before last timer stop event (y/n)",
            "n",
            sc);
        if (answer == null || !answer.equalsIgnoreCase("y")) {
            fitFile.appendTempUpdateLogLn("No move done.");
            fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
            return;
        }

        List<Mesg> coursePointMesgs = new ArrayList<>();
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.COURSE_POINT) {
                coursePointMesgs.add(mesg);
            }
        }

        allMesg.removeIf(mesg -> mesg.getNum() == MesgNum.COURSE_POINT);

        int lastRecordIxAfterRemoval = findLastMesgIndex(allMesg, MesgNum.RECORD);
        if (lastRecordIxAfterRemoval < 0) {
            fitFile.appendTempUpdateLogLn("==XX> Move aborted: records disappeared unexpectedly.");
            System.out.println(fitFile.getTempUpdateLog());
            fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
            return;
        }

        int lastTimerStopIx = fitFile.findLastTimerStopEventIndex(allMesg);
        int insertIx = lastRecordIxAfterRemoval + 1;

        if (lastTimerStopIx >= 0 && lastTimerStopIx > lastRecordIxAfterRemoval) {
            insertIx = lastTimerStopIx;
        } else if (lastTimerStopIx >= 0) {
            fitFile.appendTempUpdateLogLn("Warning: last timer stop event is not after records; inserting directly after last record.");
        } else {
            fitFile.appendTempUpdateLogLn("Warning: no timer stop event (STOP_ALL/STOP_DISABLE_ALL) found; inserting directly after last record.");
        }

        allMesg.addAll(insertIx, coursePointMesgs);

        fitFile.appendTempUpdateLogLn("Moved course points: " + coursePointMesgs.size());
        fitFile.appendTempUpdateLogLn("Inserted at allMesg index: " + insertIx);
        fitFile.appendTempUpdateLogLn("-- After last record index: " + lastRecordIxAfterRemoval);
        fitFile.appendTempUpdateLogLn("-- Last timer stop index: " + lastTimerStopIx);

        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
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
    public void syncCPointTimeAndDistanceToClosestRecordByGps() {
        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT - MATCH TO CLOSEST RECORD");
        fitFile.appendTempUpdateLogLn("--------------------------------");
        fitFile.appendTempUpdateLogLn("Matching mode: forward-only along record sequence");

        int changedCoursePoints = 0;
        int changedTimes = 0;
        int changedDistances = 0;
        int skippedCoursePoints = 0;
        int coursePointNo = 0;
        int searchStartRecordIx = 0;

        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            Integer cPointLatSemi = mesg.getFieldIntegerValue(CoursePointMesg.PositionLatFieldNum);
            Integer cPointLonSemi = mesg.getFieldIntegerValue(CoursePointMesg.PositionLongFieldNum);
            if (cPointLatSemi == null || cPointLonSemi == null) {
                fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " skipped because GPS position is missing");
                skippedCoursePoints++;
                continue;
            }

            int closestRecordIx = findClosestGpsRecordIndexInRecords(
                cPointLatSemi,
                cPointLonSemi,
                fitFile.getRecordMesg(),
                searchStartRecordIx);
            if (closestRecordIx < 0) {
                fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " skipped because no nearby record GPS was found");
                skippedCoursePoints++;
                continue;
            }

            // Keep course-point matching moving forward so repeated route locations
            // (for example start/end overlap) don't snap back to an earlier pass.
            searchStartRecordIx = closestRecordIx;

            Mesg closestRecord = fitFile.getRecordMesg().get(closestRecordIx);
            Integer recordLatSemi = closestRecord.getFieldIntegerValue(FitFile.REC_LAT);
            Integer recordLonSemi = closestRecord.getFieldIntegerValue(FitFile.REC_LON);
            double gpsGapMeters = 0d;
            if (recordLatSemi != null && recordLonSemi != null) {
                gpsGapMeters = GeoUtils.distCalc(
                    GeoUtils.fromSemicircles(cPointLatSemi),
                    GeoUtils.fromSemicircles(cPointLonSemi),
                    GeoUtils.fromSemicircles(recordLatSemi),
                    GeoUtils.fromSemicircles(recordLonSemi));
            }

            Long oldTimestamp = mesg.getFieldLongValue(CoursePointMesg.TimestampFieldNum);
            Long recordTimestamp = closestRecord.getFieldLongValue(FitFile.REC_TIME);
            Float oldDistance = mesg.getFieldFloatValue(CoursePointMesg.DistanceFieldNum);
            Float recordDistance = closestRecord.getFieldFloatValue(FitFile.REC_DIST);

            boolean changed = false;
            if (recordTimestamp != null && !recordTimestamp.equals(oldTimestamp)) {
                mesg.setFieldValue(CoursePointMesg.TimestampFieldNum, recordTimestamp);
                changedTimes++;
                changed = true;
            }

            if (recordDistance != null && (oldDistance == null || Math.abs(oldDistance - recordDistance) > 0.01f)) {
                mesg.setFieldValue(CoursePointMesg.DistanceFieldNum, recordDistance);
                changedDistances++;
                changed = true;
            }

            if (changed) {
                changedCoursePoints++;
                fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " -> closest record no: " + (closestRecordIx + 1)
                    + " (gps gap: " + String.format("%.1f", gpsGapMeters) + "m)");
                if (recordTimestamp != null && !recordTimestamp.equals(oldTimestamp)) {
                    fitFile.appendTempUpdateLogLn("-- Timestamp: " + FitDateTime.toString(oldTimestamp)
                        + " -> " + FitDateTime.toString(recordTimestamp));
                }
                if (recordDistance != null && (oldDistance == null || Math.abs(oldDistance - recordDistance) > 0.01f)) {
                    fitFile.appendTempUpdateLogLn("-- Distance: " + oldDistance + "m -> " + recordDistance + "m");
                }
            }
        }

        fitFile.appendTempUpdateLogLn("-- Updated course points: " + changedCoursePoints);
        fitFile.appendTempUpdateLogLn("-- Updated timestamps: " + changedTimes);
        fitFile.appendTempUpdateLogLn("-- Updated distances: " + changedDistances);
        fitFile.appendTempUpdateLogLn("-- Skipped course points: " + skippedCoursePoints);
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }

    // =================================================================================
    public void changeRecordAndCPointTimesToTodayDate() {
        LocalDate today = LocalDate.now();

        fitFile.clearTempUpdateLog();
        fitFile.appendTempUpdateLogLn("COURSE POINT/RECORD - CHANGE DATE TO TODAY");
        fitFile.appendTempUpdateLogLn("--------------------------------");
        fitFile.appendTempUpdateLogLn("Today date: " + today);

        int changedRecords = 0;
        int changedCoursePoints = 0;
        int changedEventTimestamps = 0;
        int changedLapTimestamps = 0;
        int changedLapStartTimes = 0;
        int skippedTimestamps = 0;

        int recordNo = 0;
        for (Mesg record : fitFile.getRecordMesg()) {
            recordNo++;
            Long oldTimestamp = record.getFieldLongValue(FitFile.REC_TIME);
            Long newTimestamp = replaceDateWithToday(oldTimestamp, today);

            if (newTimestamp == null) {
                skippedTimestamps++;
                continue;
            }

            if (!newTimestamp.equals(oldTimestamp)) {
                record.setFieldValue(FitFile.REC_TIME, newTimestamp);
                changedRecords++;
                fitFile.appendTempUpdateLogLn("Record no: " + recordNo + " -> "
                    + FitDateTime.toString(oldTimestamp) + " => " + FitDateTime.toString(newTimestamp));
            }
        }

        int coursePointNo = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            Long oldTimestamp = mesg.getFieldLongValue(CoursePointMesg.TimestampFieldNum);
            Long newTimestamp = replaceDateWithToday(oldTimestamp, today);

            if (newTimestamp == null) {
                skippedTimestamps++;
                continue;
            }

            if (!newTimestamp.equals(oldTimestamp)) {
                mesg.setFieldValue(CoursePointMesg.TimestampFieldNum, newTimestamp);
                changedCoursePoints++;
                fitFile.appendTempUpdateLogLn("CPoint no: " + coursePointNo + " -> "
                    + FitDateTime.toString(oldTimestamp) + " => " + FitDateTime.toString(newTimestamp));
            }
        }

        if (!fitFile.getRecordMesg().isEmpty()) {
            fitFile.setTimeFirstRecord(fitFile.getRecordMesg().get(0).getFieldLongValue(FitFile.REC_TIME));
            fitFile.setTimeLastRecord(fitFile.getRecordMesg().get(fitFile.getRecordMesg().size() - 1).getFieldLongValue(FitFile.REC_TIME));
        }

        int eventNo = 0;
        for (Mesg mesg : fitFile.getEventMesg()) {
            eventNo++;
            Long oldTimestamp = mesg.getFieldLongValue(FitFile.EVE_TIME);
            Long newTimestamp = replaceDateWithToday(oldTimestamp, today);
            if (newTimestamp == null) {
                skippedTimestamps++;
                continue;
            }
            if (!newTimestamp.equals(oldTimestamp)) {
                mesg.setFieldValue(FitFile.EVE_TIME, newTimestamp);
                changedEventTimestamps++;
                fitFile.appendTempUpdateLogLn("Event no: " + eventNo + " -> "
                    + FitDateTime.toString(oldTimestamp) + " => " + FitDateTime.toString(newTimestamp));
            }
        }

        int lapNo = 0;
        for (Mesg lapMesg : fitFile.getLapMesg()) {
            lapNo++;
            Long oldTimestamp = lapMesg.getFieldLongValue(FitFile.LAP_TIME);
            Long newTimestamp = replaceDateWithToday(oldTimestamp, today);
            if (newTimestamp == null) {
                skippedTimestamps++;
            } else if (!newTimestamp.equals(oldTimestamp)) {
                lapMesg.setFieldValue(FitFile.LAP_TIME, newTimestamp);
                changedLapTimestamps++;
                fitFile.appendTempUpdateLogLn("Lap no: " + lapNo + " timestamp -> "
                    + FitDateTime.toString(oldTimestamp) + " => " + FitDateTime.toString(newTimestamp));
            }

            Long oldStartTime = lapMesg.getFieldLongValue(FitFile.LAP_STIME);
            Long newStartTime = replaceDateWithToday(oldStartTime, today);
            if (newStartTime == null) {
                skippedTimestamps++;
            } else if (!newStartTime.equals(oldStartTime)) {
                lapMesg.setFieldValue(FitFile.LAP_STIME, newStartTime);
                changedLapStartTimes++;
                fitFile.appendTempUpdateLogLn("Lap no: " + lapNo + " start_time -> "
                    + FitDateTime.toString(oldStartTime) + " => " + FitDateTime.toString(newStartTime));
            }
        }

        // Normalize lap/event timestamps against updated records.
        FitFile.TimestampNormalizationStats normalizationStats = fitFile.fixLapAndEventTimestampsFromRecords();
        changedEventTimestamps += normalizationStats.changedEventTimestamps;
        changedLapTimestamps += normalizationStats.changedLapTimestamps;
        changedLapStartTimes += normalizationStats.changedLapStartTimes;

        fitFile.appendTempUpdateLogLn("-- Updated records: " + changedRecords);
        fitFile.appendTempUpdateLogLn("-- Updated course points: " + changedCoursePoints);
        fitFile.appendTempUpdateLogLn("-- Updated event timestamps: " + changedEventTimestamps);
        fitFile.appendTempUpdateLogLn("-- Updated lap timestamps: " + changedLapTimestamps);
        fitFile.appendTempUpdateLogLn("-- Updated lap start_times: " + changedLapStartTimes);
        fitFile.appendTempUpdateLogLn("-- Skipped timestamps: " + skippedTimestamps);
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
    private int findFirstMesgIndex(List<Mesg> mesgs, int mesgNum) {
        for (int i = 0; i < mesgs.size(); i++) {
            if (mesgs.get(i).getNum() == mesgNum) {
                return i;
            }
        }
        return -1;
    }
    // =================================================================================
    private int findLastMesgIndex(List<Mesg> mesgs, int mesgNum) {
        for (int i = mesgs.size() - 1; i >= 0; i--) {
            if (mesgs.get(i).getNum() == mesgNum) {
                return i;
            }
        }
        return -1;
    }
    // =================================================================================

    // =================================================================================
    private String coursePointName(CoursePoint coursePoint) {
        return coursePoint != null ? CoursePoint.getStringFromValue(coursePoint) : "-";
    }

    // =================================================================================
    private List<GenericCPointEntry> collectGenericCoursePoints() {
        List<GenericCPointEntry> genericEntries = new ArrayList<>();

        int coursePointNo = 0;
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() != MesgNum.COURSE_POINT) {
                continue;
            }

            coursePointNo++;
            CoursePointMesg coursePointMesg = new CoursePointMesg(mesg);
            if (coursePointMesg.getType() == CoursePoint.GENERIC) {
                genericEntries.add(new GenericCPointEntry(coursePointNo, mesg));
            }
        }

        return genericEntries;
    }

    // =================================================================================
    private TypeResolution resolveCoursePointTypeInput(String input) {
        if (input == null) {
            return TypeResolution.invalid("No type entered.");
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return TypeResolution.invalid("No type entered.");
        }

        try {
            int typeValue = Integer.parseInt(trimmed);
            CoursePoint coursePointByValue = CoursePoint.getByValue((short) typeValue);
            if (coursePointByValue == null || coursePointByValue == CoursePoint.INVALID) {
                return TypeResolution.invalid("Not a valid CPoint type number: " + typeValue);
            }
            return TypeResolution.valid(coursePointByValue);
        } catch (NumberFormatException ignore) {
            // Continue with text-based type parsing.
        }

        String normalizedInput = normalizeTypeToken(trimmed);
        if (normalizedInput.isEmpty()) {
            return TypeResolution.invalid("Type text contains no letters or numbers.");
        }

        Map<String, CoursePoint> exactByName = new LinkedHashMap<>();
        List<CoursePoint> prefixMatches = new ArrayList<>();

        for (CoursePoint type : CoursePoint.values()) {
            if (type == CoursePoint.INVALID) {
                continue;
            }

            String normalizedTypeName = normalizeTypeToken(coursePointName(type));
            if (normalizedTypeName.equals(normalizedInput)) {
                exactByName.put(normalizedTypeName, type);
            }
            if (normalizedTypeName.startsWith(normalizedInput)) {
                prefixMatches.add(type);
            }
        }

        if (!exactByName.isEmpty()) {
            return TypeResolution.valid(exactByName.values().iterator().next());
        }

        if (prefixMatches.isEmpty()) {
            return TypeResolution.invalid("No CPoint type matches text: '" + input + "'");
        }

        if (prefixMatches.size() > 1) {
            return TypeResolution.invalid("Ambiguous CPoint type text '" + input + "'. Matches: " + formatTypeChoices(prefixMatches));
        }

        return TypeResolution.valid(prefixMatches.get(0));
    }

    // =================================================================================
    private String normalizeTypeToken(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder normalized = new StringBuilder();
        for (char c : value.toLowerCase().toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                normalized.append(c);
            }
        }
        return normalized.toString();
    }

    // =================================================================================
    private String formatTypeChoices(List<CoursePoint> coursePoints) {
        List<String> choices = new ArrayList<>();
        for (CoursePoint coursePoint : coursePoints) {
            choices.add(coursePointName(coursePoint) + "(" + coursePoint.getValue() + ")");
        }
        return String.join(", ", choices);
    }

    // =================================================================================
    private String buildShortLabel(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "-";
        }

        String cleaned = name.replaceAll("[^A-Za-z0-9åäöÅÄÖ]", "");
        if (cleaned.isEmpty()) {
            return "-";
        }
        return cleaned.length() <= 12 ? cleaned : cleaned.substring(0, 12);
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
    private int findClosestGpsRecordIndexInRecords(Integer lat, Integer lon, List<Mesg> recordMesgList) {
        return findClosestGpsRecordIndexInRecords(lat, lon, recordMesgList, 0);
    }
    // =================================================================================
    private int findClosestGpsRecordIndexInRecords(Integer lat, Integer lon, List<Mesg> recordMesgList, int startIndex) {
        if (recordMesgList == null || recordMesgList.isEmpty()) {
            return -1;
        }

        int boundedStart = Math.max(0, startIndex);
        int closestIndex = -1;
        double closestDistance = Double.MAX_VALUE;

        for (int i = boundedStart; i < recordMesgList.size(); i++) {
            Mesg record = recordMesgList.get(i);
            Integer recordLatSemi = record.getFieldIntegerValue(FitFile.REC_LAT);
            Integer recordLonSemi = record.getFieldIntegerValue(FitFile.REC_LON);
            if (recordLatSemi == null || recordLonSemi == null) {
                continue;
            }

            double distance = GeoUtils.distCalc(
                GeoUtils.fromSemicircles(lat),
                GeoUtils.fromSemicircles(lon),
                GeoUtils.fromSemicircles(recordLatSemi),
                GeoUtils.fromSemicircles(recordLonSemi));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }
    // =================================================================================
    private Long replaceDateWithToday(Long fitTimestamp, LocalDate today) {
        if (fitTimestamp == null || today == null) {
            return null;
        }

        long unixSeconds = fitTimestamp + FitDateTime.FIT_EPOCH_OFFSET;
        LocalDateTime dateTimeUtc = LocalDateTime.ofEpochSecond(unixSeconds, 0, ZoneOffset.UTC);
        LocalDateTime updatedDateTimeUtc = LocalDateTime.of(today, dateTimeUtc.toLocalTime());
        long updatedUnixSeconds = updatedDateTimeUtc.toEpochSecond(ZoneOffset.UTC);
        long updatedFitSeconds = updatedUnixSeconds - FitDateTime.FIT_EPOCH_OFFSET;

        if (updatedFitSeconds < 0) {
            return null;
        }
        return updatedFitSeconds;
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
    private static class GenericCPointEntry {
        private final int coursePointNo;
        private final Mesg mesg;

        private GenericCPointEntry(int coursePointNo, Mesg mesg) {
            this.coursePointNo = coursePointNo;
            this.mesg = mesg;
        }
    }

    // =================================================================================
    private static class TypeResolution {
        private final boolean valid;
        private final CoursePoint coursePoint;
        private final String message;

        private TypeResolution(boolean valid, CoursePoint coursePoint, String message) {
            this.valid = valid;
            this.coursePoint = coursePoint;
            this.message = message;
        }

        private static TypeResolution valid(CoursePoint coursePoint) {
            return new TypeResolution(true, coursePoint, "");
        }

        private static TypeResolution invalid(String message) {
            return new TypeResolution(false, null, message);
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