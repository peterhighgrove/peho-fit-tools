package se.peho.fittools.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
}