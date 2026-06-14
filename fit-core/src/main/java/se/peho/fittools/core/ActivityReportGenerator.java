package se.peho.fittools.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import com.garmin.fit.Event;
import com.garmin.fit.EventType;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;

public class ActivityReportGenerator {
    private static final int MESG_GPS_METADATA = 160;
    private static final int MESG_TIME_IN_ZONE = 216;
    private static final int MESG_UNKNOWN_233 = 233;

    private final FitFile fitFile;

    public ActivityReportGenerator(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    public void printFileStructure() {
        List<String> summaryLines = buildFileStructureSummary();
        List<String> outputLines = new ArrayList<>();

        outputLines.add("");
        outputLines.add("================================================");
        outputLines.addAll(buildMesgTypeSummaryLines());
        outputLines.add("================================================");
        outputLines.add("ACTIVITY FILE STRUCTURE");
        outputLines.add("------------------------------------------------");
        outputLines.addAll(summaryLines);
        outputLines.add("------------------------------------------------");
        outputLines.add("Total mesg: " + fitFile.getAllMesg().size());

        for (String line : outputLines) {
            System.out.println(line);
        }

        try (FileWriter myWriter = new FileWriter("activity-file-structure.txt")) {
            for (String line : outputLines) {
                myWriter.write(line);
                myWriter.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Could not write activity-file-structure.txt");
        }
    }

    List<String> buildFileStructureSummary() {
        List<String> summaryLines = new ArrayList<>();
        List<Mesg> allMesg = fitFile.getAllMesg();

        if (allMesg.isEmpty()) {
            summaryLines.add("No mesg in current fit file.");
            return summaryLines;
        }

        int ix = 0;
        while (ix < allMesg.size()) {
            int patternStart = ix;

            // Try to build a repeating pattern by looking ahead
            List<Integer> pattern = buildRepeatingPattern(allMesg, ix);
            int patternLength = pattern.size();
            int repeatCount = countPatternRepetitions(allMesg, ix, pattern);
            int totalCount = repeatCount * pattern.size();
            
            // Grouping thresholds:
            // - Same-type runs (pattern size 1): require at least 3 consecutive
            // - Heterogeneous patterns (pattern size > 1): require at least 2 repeats
            boolean shouldGroup = pattern.size() == 1 ? repeatCount >= 3 : repeatCount >= 2;
            if (shouldGroup) {
                int nextIx = ix + (patternLength * repeatCount);

                // Get message index information from first and last message in group
                Mesg firstMesg = allMesg.get(patternStart);
                int lastIx = nextIx - 1;
                Mesg lastMesg = allMesg.get(lastIx);
                String indexInfo = formatIndexInfo(
                    patternStart,
                    lastIx,
                    firstMesg.getFieldIntegerValue(254),
                    lastMesg.getFieldIntegerValue(254)
                );

                // Format output line
                StringBuilder line = new StringBuilder();
                // Check if all messages in pattern are the same type
                boolean allSameType = pattern.stream().allMatch(m -> m.equals(pattern.get(0)));

                if (allSameType) {
                    line.append(indexInfo).append(" ");
                    // Simplify: show as single message type with total count
                    int mesgNum = pattern.get(0);
                    line.append(formatMesgName(mesgNum));
                    
                    // Add EVENT details if this is an EVENT message
                    if (mesgNum == MesgNum.EVENT) {
                        line.append(getEventDetails(firstMesg));
                    }
                    
                    if (totalCount > 1) {
                        line.append(" x").append(totalCount);
                    }
                } else if (pattern.size() == 1) {
                    line.append(indexInfo).append(" ");
                    // Single message type
                    int mesgNum = pattern.get(0);
                    line.append(formatMesgName(mesgNum));
                    
                    // Add EVENT details if this is an EVENT message
                    if (mesgNum == MesgNum.EVENT) {
                        line.append(getEventDetails(firstMesg));
                    }
                    
                    if (repeatCount > 1) {
                        line.append(" x").append(repeatCount);
                    }
                } else {
                    // Multi-message pattern (different types)
                    for (int i = 0; i < pattern.size(); i++) {
                        if (i > 0) {
                            line.append(", ");
                        }
                        line.append(formatRepeatedPatternEntry(allMesg, patternStart, patternLength, repeatCount, i, pattern.get(i)));
                    }
                    if (repeatCount > 1) {
                        line.append(" x").append(repeatCount);
                    }
                }

                summaryLines.add(line.toString());
                ix = nextIx;
            } else {
                // Less than 3 consecutive: display individually
                Mesg mesg = allMesg.get(ix);
                String indexInfo = formatIndexInfo(ix, ix, mesg.getFieldIntegerValue(254), mesg.getFieldIntegerValue(254));
                StringBuilder line = new StringBuilder();
                line.append(indexInfo).append(" ");
                line.append(formatMesgName(mesg.getNum()));
                if (mesg.getNum() == MesgNum.EVENT) {
                    line.append(getEventDetails(mesg));
                }
                summaryLines.add(line.toString());
                ix++;
            }
        }

        return summaryLines;
    }

    /**
     * Build the minimal repeating pattern starting at position ix.
     * Returns a list of message numbers that form a repeating unit.
     */
    private List<Integer> buildRepeatingPattern(List<Mesg> allMesg, int ix) {
        List<Integer> pattern = new ArrayList<>();
        
        // Start with the first message
        int startMesgNum = allMesg.get(ix).getNum();
        pattern.add(startMesgNum);

        // Never start heterogeneous grouped patterns with these mesg types.
        // Same-type repeats are still handled by length-1 pattern + repeat count.
        if (isRestrictedGroupStarter(startMesgNum)) {
            return pattern;
        }
        
        // Try to extend the pattern by one message at a time
        // and see if this extended pattern actually repeats.
        // Only extend when the candidate contains multiple different message types -
        // same-type runs are handled as a length-1 pattern with a repeat count.
        for (int len = 2; len <= Math.min(10, allMesg.size() - ix); len++) {
            // Build candidate pattern of length len
            List<Integer> candidate = new ArrayList<>();
            for (int i = 0; i < len && ix + i < allMesg.size(); i++) {
                candidate.add(allMesg.get(ix + i).getNum());
            }

            boolean hasMultipleTypes = candidate.stream().anyMatch(m -> !m.equals(candidate.get(0)));
            if (!hasMultipleTypes) {
                // All same type so far - keep length-1 pattern
                break;
            }

            // Heterogeneous grouped patterns must not repeat mesg types.
            // Example disallowed: LAP, TIME_IN_ZONE, LAP, TIME_IN_ZONE
            if (hasDuplicateTypes(candidate)) {
                break;
            }

            // Check if this heterogeneous candidate pattern repeats at least twice (3 total)
            if (candidate.size() == len && repeatPatternExists(allMesg, ix, candidate)) {
                pattern = candidate;
            }
            // Do not break: continue trying longer patterns even if this length doesn't repeat
        }
        
        return pattern;
    }

    private boolean isRestrictedGroupStarter(int mesgNum) {
        // Don't allow heterogeneous grouping to start with TIME_IN_ZONE(216) or UNKNOWN_233
        // This ensures patterns starting with 20/160 are preferred for grouping
        return mesgNum == MESG_TIME_IN_ZONE || mesgNum == MESG_UNKNOWN_233;
    }

    private boolean hasDuplicateTypes(List<Integer> pattern) {
        Set<Integer> seen = new HashSet<>();
        for (Integer mesgNum : pattern) {
            if (!seen.add(mesgNum)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the pattern repeats at least once starting at position ix (2 total consecutive).
     */
    private boolean repeatPatternExists(List<Mesg> allMesg, int ix, List<Integer> pattern) {
        int checkIx = ix + pattern.size();
        
        // Check if the pattern repeats at least once (for 2 total consecutive occurrences)
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int patternIdx = 0; patternIdx < pattern.size(); patternIdx++) {
                if (checkIx >= allMesg.size() || allMesg.get(checkIx).getNum() != pattern.get(patternIdx)) {
                    return false;
                }
                checkIx++;
            }
        }
        return true;
    }

    /**
     * Count how many times the pattern repeats starting at position ix.
     */
    private int countPatternRepetitions(List<Mesg> allMesg, int ix, List<Integer> pattern) {
        int count = 0;
        int checkIx = ix;
        
        while (checkIx < allMesg.size()) {
            boolean matchesPattern = true;
            
            for (int patternIdx = 0; patternIdx < pattern.size(); patternIdx++) {
                if (checkIx >= allMesg.size() || allMesg.get(checkIx).getNum() != pattern.get(patternIdx)) {
                    matchesPattern = false;
                    break;
                }
                checkIx++;
            }
            
            if (matchesPattern) {
                count++;
            } else {
                break;
            }
        }
        
        return count;
    }

    private List<String> buildMesgTypeSummaryLines() {
        List<String> lines = new ArrayList<>();
        Map<Integer, Integer> counts = new TreeMap<>();
        for (Mesg mesg : fitFile.getAllMesg()) {
            int num = mesg.getNum();
            counts.put(num, counts.getOrDefault(num, 0) + 1);
        }
        lines.add("MESG TYPE COUNTS");
        lines.add("------------------------------------------------");
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            lines.add(String.format("  %-30s %d", formatMesgName(entry.getKey()), entry.getValue()));
        }
        return lines;
    }

    private String formatRepeatedPatternEntry(
            List<Mesg> allMesg,
            int patternStart,
            int patternLength,
            int repeatCount,
            int patternOffset,
            int mesgNum) {
        int firstOccurrenceIx = patternStart + patternOffset;
        Mesg firstOccurrence = allMesg.get(firstOccurrenceIx);
        int lastOccurrenceIx = patternStart + patternOffset + ((repeatCount - 1) * patternLength);
        Mesg lastOccurrence = allMesg.get(lastOccurrenceIx);

        StringBuilder entry = new StringBuilder();
        entry.append(formatIndexInfo(
            firstOccurrenceIx,
            lastOccurrenceIx,
            firstOccurrence.getFieldIntegerValue(254),
            lastOccurrence.getFieldIntegerValue(254)
        ));
        entry.append(" ").append(formatMesgName(mesgNum));
        if (mesgNum == MesgNum.EVENT) {
            entry.append(getEventDetails(firstOccurrence));
        }

        return entry.toString();
    }

    private String formatIndexInfo(int firstIx, int lastIx, Integer firstField254, Integer lastField254) {
        StringBuilder indexInfo = new StringBuilder("[");
        indexInfo.append(firstIx);
        if (lastIx > firstIx) {
            indexInfo.append("-").append(lastIx);
        }
        if (firstField254 != null) {
            indexInfo.append(":").append(firstField254);
            if (lastIx > firstIx && lastField254 != null && !lastField254.equals(firstField254)) {
                indexInfo.append("-").append(lastField254);
            }
        }
        indexInfo.append("]");
        return indexInfo.toString();
    }

    private String formatMesgName(int mesgNum) {
        String mesgName = MesgNum.getStringFromValue(mesgNum);
        if (mesgName == null || mesgName.isBlank() || mesgName.startsWith("UNKNOWN")) {
            String alias = getUndocumentedMesgAlias(mesgNum);
            if (alias != null) {
                return alias + "(" + mesgNum + ")";
            }
            return "UNKNOWN(" + mesgNum + ")";
        }
        return mesgName + "(" + mesgNum + ")";
    }

    private String getUndocumentedMesgAlias(int mesgNum) {
        switch (mesgNum) {
            case 22:
                return "device_used";
            case 79:
                return "user_metrics";
            case 104:
                return "device_status";
            case 113:
                return "best_effort";
            case 140:
                return "activity_metrics";
            case 141:
                return "epo_status";
            case 147:
                return "sensor_settings";
            case 195:
                return "unknown";
            case 233:
                return "unknownx";
            case 288:
                return "unknown";
            case 325:
                return "unknown";
            case 326:
                return "gps_event";
            case 327:
                return "unknown";
            case 380:
                return "unknown";
            case 394:
                return "cpe_status";
            case 428:
                return "workout_schedule";
            default:
                return null;
        }
    }

    private String getEventDetails(Mesg eventMesg) {
        Short rawEvent = eventMesg.getFieldShortValue(FitFile.EVE_EVENT);
        Short rawEventType = eventMesg.getFieldShortValue(FitFile.EVE_TYPE);

        if (rawEvent == null || rawEventType == null) {
            return "";
        }

        Event event = Event.getByValue(rawEvent);
        EventType eventType = EventType.getByValue(rawEventType);

        String eventStr = event != null ? event.toString() : "UNKNOWN(" + rawEvent + ")";
        String typeStr = eventType != null ? eventType.toString() : "UNKNOWN(" + rawEventType + ")";

        return " [" + eventStr + "/" + typeStr + "]";
    }

    public void writeMesgList() {
        List<Mesg> allMesg = fitFile.getAllMesg();
        long diffMinutesLocalUTC = fitFile.getDiffMinutesLocalUTC() != null ? fitFile.getDiffMinutesLocalUTC() : 0L;

        Long firstTimestamp = null;
        Long lastTimestamp = null;

        try (FileWriter writer = new FileWriter("activity-mesg-list.csv")) {
            writer.write(String.join(",",
                    "Overall mesg ix",
                    "Timestamp (field 253) raw value",
                    "Timestamp (field 253) localtime string",
                    "Timestamp value that is compatible with Libre",
                    "Seconds diff from last value",
                    "ElapsedTimer in h:mm:ss",
                    "TotalTimer in h:mm:ss (pause time excluded)",
                    "Mesg name",
                    "Mesg no",
                    "Mesg ix (field 253) if applicable",
                    "Event type and data",
                    "Distance in m",
                    "Enh speed in min/sec hh:mm:ss string",
                    "Enh speed in m/s value",
                    "Cadence"
            ));
            writer.write(System.lineSeparator());

            for (int overallIx = 0; overallIx < allMesg.size(); overallIx++) {
                Mesg mesg = allMesg.get(overallIx);
                Long timestamp = mesg.getFieldLongValue(253);

                if (timestamp != null && firstTimestamp == null) {
                    firstTimestamp = timestamp;
                }

                String timestampLocal = "";
                String timestampLibre = "";
                String timestampDiff = "";
                String elapsedTimer = "";
                String totalTimer = "";

                if (timestamp != null) {
                    timestampLocal = formatLocalDateTimeForCsv(timestamp, diffMinutesLocalUTC);
                    timestampLibre = formatLibreDateTimeValue(timestamp, diffMinutesLocalUTC);
                    if (lastTimestamp != null) {
                        timestampDiff = String.valueOf(timestamp - lastTimestamp);
                    }
                    if (firstTimestamp != null) {
                        elapsedTimer = formatHms(timestamp - firstTimestamp);
                    }
                    totalTimer = formatHms(fitFile.findTimerBasedOnTime(timestamp));
                    lastTimestamp = timestamp;
                }

                String mesgName = formatMesgName(mesg.getNum());
                String mesgNo = String.valueOf(mesg.getNum());
                Integer mesgIx = mesg.getFieldIntegerValue(254);
                String mesgIxValue = mesgIx != null ? String.valueOf(mesgIx) : "";

                String eventTypeAndData = "";
                String distanceMeters = "";
                String enhSpeedPace = "";
                String enhSpeedMps = "";
                String cadence = "";

                if (mesg.getNum() == MesgNum.EVENT) {
                    eventTypeAndData = buildEventTypeAndData(mesg);
                }

                if (mesg.getNum() == MesgNum.RECORD) {
                    Float dist = mesg.getFieldFloatValue(FitFile.REC_DIST);
                    if (dist != null) {
                        distanceMeters = String.format(Locale.ROOT, "%.1f", dist);
                    }

                    Float enhancedSpeed = mesg.getFieldFloatValue(FitFile.REC_ESPEED);
                    if (enhancedSpeed != null) {
                        enhSpeedMps = String.format(Locale.ROOT, "%.3f", enhancedSpeed);
                        enhSpeedPace = speedToHmsPerKm(enhancedSpeed);
                    }

                    Short recordCadence = mesg.getFieldShortValue(FitFile.REC_CAD);
                    if (recordCadence != null) {
                        cadence = String.valueOf(recordCadence);
                    }
                }

                List<String> row = List.of(
                        String.valueOf(overallIx),
                        timestamp != null ? String.valueOf(timestamp) : "",
                        timestampLocal,
                        timestampLibre,
                        timestampDiff,
                        elapsedTimer,
                        totalTimer,
                        mesgName,
                        mesgNo,
                        mesgIxValue,
                        eventTypeAndData,
                        distanceMeters,
                        enhSpeedPace,
                        enhSpeedMps,
                        cadence
                );

                writer.write(toCsvLine(row));
                writer.write(System.lineSeparator());
            }

            System.out.println("Wrote activity-mesg-list.csv with " + allMesg.size() + " rows.");
        } catch (IOException e) {
            System.out.println("Could not write activity-mesg-list.csv");
        }
    }

    private String formatLocalDateTimeForCsv(Long fitTimestamp, long diffMinutesLocalUTC) {
        String value = FitDateTime.toString(fitTimestamp, diffMinutesLocalUTC);
        if (value == null || value.length() < 19) {
            return "";
        }
        // Convert yyyy-MM-dd-HH-mm-ss to yyyy-MM-dd HH:mm:ss for easier spreadsheet parsing.
        return value.substring(0, 10) + " " + value.substring(11, 13) + ":"
                + value.substring(14, 16) + ":" + value.substring(17, 19);
    }

    private String formatLibreDateTimeValue(Long fitTimestamp, long diffMinutesLocalUTC) {
        long unixSecondsLocal = fitTimestamp + FitDateTime.FIT_EPOCH_OFFSET + (diffMinutesLocalUTC * 60L);
        double libreValue = (unixSecondsLocal / 86400.0d) + 25569.0d;
        return String.format(Locale.ROOT, "%.12f", libreValue);
    }

    private String formatHms(Long secondsValue) {
        if (secondsValue == null || secondsValue < 0) {
            return "";
        }
        long hours = secondsValue / 3600;
        long minutes = (secondsValue % 3600) / 60;
        long seconds = secondsValue % 60;
        return String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds);
    }

    private String speedToHmsPerKm(Float speedMetersPerSecond) {
        if (speedMetersPerSecond == null || speedMetersPerSecond <= 0f) {
            return "";
        }
        long secondsPerKm = Math.round(1000d / speedMetersPerSecond);
        return formatHms(secondsPerKm);
    }

    private String buildEventTypeAndData(Mesg eventMesg) {
        Short rawEvent = eventMesg.getFieldShortValue(FitFile.EVE_EVENT);
        Short rawEventType = eventMesg.getFieldShortValue(FitFile.EVE_TYPE);
        Object rawData = eventMesg.getFieldValue(3);

        String eventStr = rawEvent != null
                ? String.valueOf(Event.getByValue(rawEvent))
                : "";
        String typeStr = rawEventType != null
                ? String.valueOf(EventType.getByValue(rawEventType))
                : "";

        if (rawData == null && eventStr.isBlank() && typeStr.isBlank()) {
            return "";
        }
        return eventStr + "/" + typeStr + (rawData != null ? " data=" + rawData : "");
    }

    private String toCsvLine(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escapeCsv(values.get(i)));
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}