package se.peho.fittools.core;

import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import se.peho.fittools.core.strings.*;

public class LapFix {

    private static final float SPLIT_TIMER_MATCH_TOLERANCE_SEC = 1.1f;
    private static final int SPL_LAP_INDEX_FIELD_NUM = 67;
    private static final int SPL_TEMP_FIELD_NUM = 32;
    private static final int SPL_MAXTEMP_FIELD_NUM = 33;
    private static final int SPL_MINTEMP_FIELD_NUM = 34;

    private final FitFile fitFile;

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public LapFix(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSplitLapMatchReport() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("SPLIT vs LAP MATCH ANALYZE");
        System.out.println("Rule 1: SPL_LAPIX (field " + SPL_LAP_INDEX_FIELD_NUM + ")");
        System.out.println("Rule 2: SPL_TIMER fallback (tolerance +/-" + SPLIT_TIMER_MATCH_TOLERANCE_SEC + "s)");
        System.out.println("--------------------------------------------------");

        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            System.out.println("No LAP messages found.");
            return;
        }
        if (fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty()) {
            System.out.println("No SPLIT messages found.");
            return;
        }

        System.out.println("LAP -> SPLIT");
        System.out.println("--------------------------------------------------");
        Set<Integer> usedSplitIndexes = new HashSet<>();
        for (int lapIx = 0; lapIx < fitFile.getLapMesg().size(); lapIx++) {
            Mesg lap = fitFile.getLapMesg().get(lapIx);
            SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);
            Float lapTimer = lap.getFieldFloatValue(FitFile.LAP_TIMER);
            if (match != null) {
                usedSplitIndexes.add(match.splitListIndex);
                System.out.println("LAP " + (lapIx + 1)
                    + " timer=" + formatSec(lapTimer)
                    + " -> SPLIT " + (match.splitListIndex + 1)
                    + " by " + match.matchReason
                    + " splitTimer=" + formatSec(match.splitTimer));
            } else {
                System.out.println("LAP " + (lapIx + 1)
                    + " timer=" + formatSec(lapTimer)
                    + " -> no matching split");
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("SPLIT -> LAP (for SPLIT records without usable SPL_LAPIX)");
        System.out.println("--------------------------------------------------");

        int missingLapIxCount = 0;
        for (int splitIx = 0; splitIx < fitFile.getSplitMesg().size(); splitIx++) {
            Mesg split = fitFile.getSplitMesg().get(splitIx);
            Integer splitLapIx = getMesgFieldAsInt(split, SPL_LAP_INDEX_FIELD_NUM);
            Float splitTimer = split.getFieldFloatValue(FitFile.SPL_TIMER);

            if (splitLapIx != null && splitLapIx >= 0 && splitLapIx < fitFile.getLapMesg().size()) {
                continue;
            }

            missingLapIxCount++;
            List<Integer> singleLapMatches = findLapsByTimer(splitTimer);
            Integer pairStartLapIx = findAdjacentLapPairBySummedTimer(splitTimer);

            if (singleLapMatches.size() == 1) {
                int lapNo = singleLapMatches.get(0) + 1;
                System.out.println("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " timer=" + formatSec(splitTimer)
                    + " -> single LAP TIMER match: LAP " + lapNo
                    + " (candidate to set SPL_LAPIX)");
            } else if (singleLapMatches.size() > 1) {
                System.out.println("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " timer=" + formatSec(splitTimer)
                    + " -> multiple LAP TIMER matches: " + toLapNoList(singleLapMatches)
                    + " (ambiguous)");
            } else if (pairStartLapIx != null) {
                int lapNo1 = pairStartLapIx + 1;
                int lapNo2 = pairStartLapIx + 2;
                Float lap1Timer = fitFile.getLapMesg().get(pairStartLapIx).getFieldFloatValue(FitFile.LAP_TIMER);
                Float lap2Timer = fitFile.getLapMesg().get(pairStartLapIx + 1).getFieldFloatValue(FitFile.LAP_TIMER);
                System.out.println("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " timer=" + formatSec(splitTimer)
                    + " -> matches LAP pair sum: LAP " + lapNo1 + " + LAP " + lapNo2
                    + " (" + formatSec(lap1Timer) + " + " + formatSec(lap2Timer) + ")"
                    + " => candidate for merge/split handling");
            } else {
                System.out.println("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " timer=" + formatSec(splitTimer)
                    + " -> no LAP TIMER match");
            }
        }

        if (missingLapIxCount == 0) {
            System.out.println("All SPLIT messages already have valid SPL_LAPIX.");
        }
        System.out.println("==================================================");
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void lapMerge(int fromLap, int toLap) {
        fitFile.clearTempUpdateLog();
        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.appendTempUpdateLogLn("==XX> No lap messages found.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }
        if (fromLap < 1 || toLap < 1 || fromLap > toLap || toLap > fitFile.getLapMesg().size()) {
            fitFile.appendTempUpdateLogLn("==XX> Invalid lap merge range: " + fromLap + "-" + toLap);
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }
        if (fitFile.getRecordMesg() == null || fitFile.getRecordMesg().isEmpty()) {
            fitFile.appendTempUpdateLogLn("==XX> No record messages found.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int fromLapIx = fromLap - 1;
        int toLapIx = toLap - 1;

        Mesg firstLap = fitFile.getLapMesg().get(fromLapIx);
        Mesg mergedLap = fitFile.getLapMesg().get(toLapIx);

        Long originalMergedStartTime = firstLap.getFieldLongValue(FitFile.LAP_STIME);
        Integer originalMergedStartLat = firstLap.getFieldIntegerValue(FitFile.LAP_SLAT);
        Integer originalMergedStartLon = firstLap.getFieldIntegerValue(FitFile.LAP_SLON);

        if (originalMergedStartTime == null) {
            fitFile.appendTempUpdateLogLn("==XX> First lap in merge range has no start time.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int mergedStartRecordIx = findRecordIndexAtOrAfterTime(originalMergedStartTime);
        int mergedEndRecordIx = findLapRecordEndIndex(toLapIx);
        if (mergedStartRecordIx < 0 || mergedEndRecordIx < 0 || mergedStartRecordIx > mergedEndRecordIx) {
            fitFile.appendTempUpdateLogLn("==XX> Could not resolve record range for merged lap " + fromLap + "-" + toLap);
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        LapBoundaryValues mergedBoundary = recalculateLapValuesFromRecords(
            mergedLap,
            mergedStartRecordIx,
            mergedEndRecordIx);

        List<SplitMatch> splitMatchesToMerge = analyzeSplitMatchesForLapRange(fromLapIx, toLapIx);
        mergeMatchedSplitsForLapMerge(splitMatchesToMerge, fromLapIx, toLapIx, mergedLap);

        // Preserve the original first-lap start identity fields on merged lap.
        setLongIfPresent(mergedLap, FitFile.LAP_STIME, originalMergedStartTime);
        setIntIfPresent(mergedLap, FitFile.LAP_SLAT, originalMergedStartLat);
        setIntIfPresent(mergedLap, FitFile.LAP_SLON, originalMergedStartLon);

        fitFile.appendTempUpdateLogLn("Merged laps: " + fromLap + " to " + toLap);
        Float mergedDist = mergedLap.getFieldFloatValue(FitFile.LAP_DIST);
        fitFile.appendTempUpdateLogLn("-- New lap " + toLap
            + " time: " + new TimeStr(mergedBoundary.totalTimer).get()
            + ", dist: " + Math.round(mergedDist != null ? mergedDist : 0f) + " m");

        // Deleting the merged laps (fromLap to toLap-1)
        //-----------------------------------------------
        int deleteCount = toLap - fromLap;
        int targetLapIx = fromLap - 1;
        for (int deleteCounter = 0; deleteCounter < deleteCount; deleteCounter++) {
            int lapAllMesgIx = findLapMesgIndexInAllMesgByLapIx(targetLapIx);
            if (lapAllMesgIx < 0) {
                fitFile.appendTempUpdateLogLn("-- Could not find LAP mesg in allMesg for lap ix:" + targetLapIx);
                continue;
            }

            Mesg lapMesgToDelete = fitFile.getAllMesg().get(lapAllMesgIx);
            fitFile.appendTempUpdateLogLn("-- Deleting lap ix:" + targetLapIx + " time:"
                + FitDateTime.toString(lapMesgToDelete.getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()));

            int timeInZoneIx = findLinkedTimeInZoneMesgIndex(lapAllMesgIx, targetLapIx);
            if (timeInZoneIx >= 0) {
                fitFile.appendTempUpdateLogLn("-- Deleting linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                int firstRemoveIx = Math.max(lapAllMesgIx, timeInZoneIx);
                int secondRemoveIx = Math.min(lapAllMesgIx, timeInZoneIx);
                fitFile.getAllMesg().remove(firstRemoveIx);
                fitFile.getAllMesg().remove(secondRemoveIx);
            } else {
                fitFile.appendTempUpdateLogLn("-- Could not find linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                fitFile.getAllMesg().remove(lapAllMesgIx);
            }

            int lapMesgIx = findLapMesgIndexInLapMesgByLapIx(targetLapIx);
            if (lapMesgIx >= 0) {
                fitFile.getLapMesg().remove(lapMesgIx);
            }

            decrementLapReferencesAfterDeletedLap(targetLapIx);
        }
        fitFile.setNumberOfLaps(fitFile.getNumberOfLaps() - (toLap - fromLap));

        // Update SES_LAPS
        if (!fitFile.getSessionMesg().isEmpty()) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_LAPS, fitFile.getNumberOfLaps());
        }

        syncSplitsFromLapsAfterLapChange("lapMerge");

        // Print and save logs
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void lapNew(Long totalTimer) {
        fitFile.clearTempUpdateLog();

        if (totalTimer == null) {
            fitFile.appendTempUpdateLogLn("==XX> No timer value provided.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }
        if (fitFile.getRecordMesgAddOnRecords() == null || fitFile.getRecordMesgAddOnRecords().isEmpty()) {
            fitFile.appendTempUpdateLogLn("==XX> Timer list is empty. Run createTimerList() first.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }
        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.appendTempUpdateLogLn("==XX> No lap messages found.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int splitRecordIx = findFirstRecordIndexAtOrAfterTimer(totalTimer);
        if (splitRecordIx <= 0 || splitRecordIx >= fitFile.getRecordMesg().size()) {
            fitFile.appendTempUpdateLogLn("==XX> Timer cannot be used for lap split (outside record range): "
                + PehoUtils.sec2minSecLong(totalTimer));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        Mesg splitRecord = fitFile.getRecordMesg().get(splitRecordIx);
        Mesg prevRecord = fitFile.getRecordMesg().get(splitRecordIx - 1);

        Long splitTime = splitRecord.getFieldLongValue(FitFile.REC_TIME);
        Long prevTime = prevRecord.getFieldLongValue(FitFile.REC_TIME);
        Long splitTimer = fitFile.getRecordMesgAddOnRecords().get(splitRecordIx).getTimer();

        if (splitTime == null || prevTime == null || splitTimer == null) {
            fitFile.appendTempUpdateLogLn("==XX> Could not resolve split record timing values.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int lapIx = findLapIndexForTime(splitTime);
        if (lapIx < 0 || lapIx >= fitFile.getLapMesg().size()) {
            fitFile.appendTempUpdateLogLn("==XX> Could not find lap for timer " + PehoUtils.sec2minSecLong(totalTimer));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        // Creating a new lap message based on the first lap message
        Mesg firstLap = fitFile.getLapMesg().get(lapIx);
        SplitMatch splitToSplit = analyzeSplitMatchForSingleLap(lapIx, firstLap, "LAP NEW");
        Mesg secondLap = new Mesg(firstLap);

        Long originalLapStartTime = firstLap.getFieldLongValue(FitFile.LAP_STIME);
        Integer originalLapStartLat = firstLap.getFieldIntegerValue(FitFile.LAP_SLAT);
        Integer originalLapStartLon = firstLap.getFieldIntegerValue(FitFile.LAP_SLON);

        // Get first lap start time to find the record range for the lap
        Long lapStartTime = firstLap.getFieldLongValue(FitFile.LAP_STIME);
        if (lapStartTime == null) {
            fitFile.appendTempUpdateLogLn("==XX> Lap has no start time. Cannot split lap " + (lapIx + 1));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int originalLapStartRecordIx = findRecordIndexAtOrAfterTime(lapStartTime);
        int originalLapEndRecordIx = findLapRecordEndIndex(lapIx);
        
        if (originalLapStartRecordIx < 0 || originalLapEndRecordIx < 0 || originalLapStartRecordIx > originalLapEndRecordIx) {
            fitFile.appendTempUpdateLogLn("==XX> Could not resolve record range for lap " + (lapIx + 1));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        if (splitRecordIx <= originalLapStartRecordIx || splitRecordIx > originalLapEndRecordIx) {
            fitFile.appendTempUpdateLogLn("==XX> Split timer must point inside lap " + (lapIx + 1)
                + ", not at or outside lap boundary.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        // Set the start time of the second lap to the split time
        setLongIfPresent(secondLap, FitFile.LAP_STIME, splitTime);

        LapBoundaryValues firstLapBoundary = recalculateLapValuesFromRecords(
            firstLap,
            originalLapStartRecordIx,
            splitRecordIx - 1);
        LapBoundaryValues secondLapBoundary = recalculateLapValuesFromRecords(
            secondLap,
            splitRecordIx,
            originalLapEndRecordIx);

        // Keep the original lap start fields intact for the first split segment.
        setLongIfPresent(firstLap, FitFile.LAP_STIME, originalLapStartTime);
        setIntIfPresent(firstLap, FitFile.LAP_SLAT, originalLapStartLat);
        setIntIfPresent(firstLap, FitFile.LAP_SLON, originalLapStartLon);

        int lapAllMesgIx = findLapMesgIndexInAllMesgByLapIx(lapIx);
        if (lapAllMesgIx < 0) {
            fitFile.appendTempUpdateLogLn("==XX> Could not find LAP in allMesg for lap ix: " + lapIx);
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int linkedTizIx = findLinkedTimeInZoneMesgIndex(lapAllMesgIx, lapIx);
        Mesg insertedTimeInZone = null;
        Mesg sourceTimeInZone = linkedTizIx >= 0 ? fitFile.getAllMesg().get(linkedTizIx) : null;
        int insertAllMesgIx = lapAllMesgIx + 1;
        if (linkedTizIx == lapAllMesgIx + 1) {
            insertAllMesgIx = lapAllMesgIx + 2;
        }

        setIntIfPresent(secondLap, FitFile.LAP_IX, lapIx + 1);
        fitFile.getLapMesg().add(lapIx + 1, secondLap);
        fitFile.getAllMesg().add(insertAllMesgIx, secondLap);

        if (sourceTimeInZone != null) {
            insertedTimeInZone = new Mesg(sourceTimeInZone);
            insertedTimeInZone.setFieldValue(FitFile.TIZ_REF_MESG, MesgNum.LAP);
            insertedTimeInZone.setFieldValue(FitFile.TIZ_REF_IX, lapIx + 1);
            fitFile.getAllMesg().add(insertAllMesgIx + 1, insertedTimeInZone);
        }

        Mesg insertedSplit = splitMatchedSplitForLapNew(splitToSplit, lapIx, firstLap, secondLap);

        incrementLapReferencesAfterInsertedLap(lapIx, secondLap, insertedTimeInZone, insertedSplit);

        fitFile.setNumberOfLaps(fitFile.getNumberOfLaps() + 1);
        if (!fitFile.getSessionMesg().isEmpty()) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_LAPS, fitFile.getNumberOfLaps());
        }

        syncSplitsFromLapsAfterLapChange("lapNew");

        fitFile.appendTempUpdateLogLn("Split lap " + (lapIx + 1)
            + " at totalTimer=" + PehoUtils.sec2minSecLong(totalTimer)
            + " (recordIx=" + splitRecordIx + ")");
        fitFile.appendTempUpdateLogLn("-- Lap " + (lapIx + 1)
            + " new timer: " + new TimeStr(firstLapBoundary.totalTimer).get()
            + ", new lap " + (lapIx + 2)
            + " timer: " + new TimeStr(secondLapBoundary.totalTimer).get());

        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findLapMesgIndexInAllMesgByLapIx(int lapIx) {
        return FindMesgIx.findMesgIndexByIntField(fitFile.getAllMesg(), MesgNum.LAP, FitFile.LAP_IX, lapIx);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private List<SplitMatch> analyzeSplitMatchesForLapRange(int fromLapIx, int toLapIx) {
        List<SplitMatch> matches = new ArrayList<>();
        Set<Integer> usedSplitIndexes = new HashSet<>();

        fitFile.appendTempUpdateLogLn("-- Split analyze for lap range " + (fromLapIx + 1) + "-" + (toLapIx + 1));
        for (int lapIx = fromLapIx; lapIx <= toLapIx; lapIx++) {
            Mesg lap = fitFile.getLapMesg().get(lapIx);
            SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);
            if (match != null) {
                usedSplitIndexes.add(match.splitListIndex);
                matches.add(match);
                fitFile.appendTempUpdateLogLn("-- Split match LAP " + (lapIx + 1)
                    + " -> SPLIT " + (match.splitListIndex + 1)
                    + " by " + match.matchReason
                    + " (splitTimer=" + formatSec(match.splitTimer)
                    + ", lapTimer=" + formatSec(match.lapTimer) + ")");
            } else {
                Float lapTimer = lap.getFieldFloatValue(FitFile.LAP_TIMER);
                fitFile.appendTempUpdateLogLn("-- No split match for LAP " + (lapIx + 1)
                    + " (lapTimer=" + formatSec(lapTimer) + ")");
            }
        }
        return matches;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private SplitMatch analyzeSplitMatchForSingleLap(int lapIx, Mesg lap, String context) {
        Set<Integer> usedSplitIndexes = new HashSet<>();
        SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);
        if (match != null) {
            fitFile.appendTempUpdateLogLn("-- Split analyze [" + context + "] LAP " + (lapIx + 1)
                + " -> SPLIT " + (match.splitListIndex + 1)
                + " by " + match.matchReason
                + " (splitTimer=" + formatSec(match.splitTimer)
                + ", lapTimer=" + formatSec(match.lapTimer) + ")");
        } else {
            fitFile.appendTempUpdateLogLn("-- Split analyze [" + context + "] LAP " + (lapIx + 1)
                + " -> no matching SPLIT");
        }
        return match;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private SplitMatch findBestSplitMatchForLap(int lapIx, Mesg lapMesg, Set<Integer> usedSplitIndexes) {
        if (fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty() || lapMesg == null) {
            return null;
        }

        // 1) Preferred match by SPL_LAPIX.
        for (int splitIx = 0; splitIx < fitFile.getSplitMesg().size(); splitIx++) {
            if (usedSplitIndexes.contains(splitIx)) {
                continue;
            }
            Mesg split = fitFile.getSplitMesg().get(splitIx);
            Integer splitLapIx = getMesgFieldAsInt(split, SPL_LAP_INDEX_FIELD_NUM);
            if (splitLapIx != null && splitLapIx == lapIx) {
                return new SplitMatch(split, splitIx, lapIx, split.getFieldFloatValue(FitFile.SPL_TIMER),
                    lapMesg.getFieldFloatValue(FitFile.LAP_TIMER), "SPL_LAPIX");
            }
        }

        // 2) Fallback: match by SPL_TIMER ~= LAP_TIMER.
        Float lapTimer = lapMesg.getFieldFloatValue(FitFile.LAP_TIMER);
        if (lapTimer == null) {
            return null;
        }

        SplitMatch best = null;
        float bestDiff = Float.MAX_VALUE;
        int closeMatches = 0;
        for (int splitIx = 0; splitIx < fitFile.getSplitMesg().size(); splitIx++) {
            if (usedSplitIndexes.contains(splitIx)) {
                continue;
            }
            Mesg split = fitFile.getSplitMesg().get(splitIx);
            Float splitTimer = split.getFieldFloatValue(FitFile.SPL_TIMER);
            if (splitTimer == null) {
                continue;
            }
            float diff = Math.abs(splitTimer - lapTimer);
            if (diff <= SPLIT_TIMER_MATCH_TOLERANCE_SEC) {
                closeMatches++;
                if (diff < bestDiff) {
                    bestDiff = diff;
                    best = new SplitMatch(split, splitIx, lapIx, splitTimer, lapTimer,
                        "SPL_TIMER(±" + SPLIT_TIMER_MATCH_TOLERANCE_SEC + "s)");
                }
            }
        }

        if (closeMatches > 1 && best != null) {
            fitFile.appendTempUpdateLogLn("-- WARNING: Multiple SPL_TIMER matches for LAP " + (lapIx + 1)
                + "; selecting SPLIT " + (best.splitListIndex + 1) + " with closest timer diff.");
        }
        return best;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void mergeMatchedSplitsForLapMerge(List<SplitMatch> matches, int fromLapIx, int toLapIx, Mesg mergedLap) {
        if (matches == null || matches.isEmpty()) {
            fitFile.appendTempUpdateLogLn("-- No matching SPLIT records found for merge range.");
            return;
        }

        matches.sort(Comparator.comparingInt(m -> m.lapIx));

        SplitMatch keeper = null;
        for (SplitMatch match : matches) {
            if (match.lapIx == toLapIx) {
                keeper = match;
                break;
            }
        }
        if (keeper == null) {
            keeper = matches.get(matches.size() - 1);
        }

        int removedSplits = 0;
        for (SplitMatch match : matches) {
            if (match == keeper) {
                continue;
            }
            removeSplitMesg(match.splitMesg);
            removedSplits++;
        }

        applyLapMetricsToSplit(toLapIx, keeper.splitMesg, mergedLap);
        setIntIfPresent(keeper.splitMesg, SPL_LAP_INDEX_FIELD_NUM, toLapIx);

        fitFile.appendTempUpdateLogLn("-- SPLIT merge result: kept SPLIT " + (keeper.splitListIndex + 1)
            + ", removed " + removedSplits + " split(s), tied to LAP " + (toLapIx + 1));

        // Keep splitMesg list stable with allMesg after deletions.
        fitFile.getSplitMesg().removeIf(split -> !fitFile.getAllMesg().contains(split));
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Mesg splitMatchedSplitForLapNew(SplitMatch splitToSplit, int lapIx, Mesg firstLap, Mesg secondLap) {
        if (splitToSplit == null) {
            return null;
        }

        Mesg firstSplit = splitToSplit.splitMesg;
        Mesg secondSplit = new Mesg(firstSplit);

        applyLapMetricsToSplit(lapIx, firstSplit, firstLap);
        applyLapMetricsToSplit(lapIx + 1, secondSplit, secondLap);

        setIntIfPresent(firstSplit, SPL_LAP_INDEX_FIELD_NUM, lapIx);
        setIntIfPresent(secondSplit, SPL_LAP_INDEX_FIELD_NUM, lapIx + 1);

        int splitMesgIx = fitFile.getSplitMesg().indexOf(firstSplit);
        if (splitMesgIx >= 0) {
            fitFile.getSplitMesg().add(splitMesgIx + 1, secondSplit);
        } else {
            fitFile.getSplitMesg().add(secondSplit);
        }

        int splitAllMesgIx = fitFile.getAllMesg().indexOf(firstSplit);
        if (splitAllMesgIx >= 0) {
            fitFile.getAllMesg().add(splitAllMesgIx + 1, secondSplit);
        }

        fitFile.appendTempUpdateLogLn("-- SPLIT update: split SPLIT " + (splitToSplit.splitListIndex + 1)
            + " into two splits for LAP " + (lapIx + 1) + " and LAP " + (lapIx + 2));
        return secondSplit;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void applyLapMetricsToSplit(int lapIx, Mesg splitMesg, Mesg lapMesg) {
        if (splitMesg == null || lapMesg == null) {
            return;
        }

        Long lapStartTime = lapMesg.getFieldLongValue(FitFile.LAP_STIME);
        setLongIfPresent(splitMesg, FitFile.SPL_STIME, lapStartTime);

        Float lapTimer = lapMesg.getFieldFloatValue(FitFile.LAP_TIMER);
        Float lapElapsed = lapMesg.getFieldFloatValue(FitFile.LAP_ETIMER);
        Float lapMoving = lapMesg.getFieldFloatValue(FitFile.LAP_MTIMER);
        Float lapDist = lapMesg.getFieldFloatValue(FitFile.LAP_DIST);
        Float lapSpeed = lapMesg.getFieldFloatValue(FitFile.LAP_ESPEED);
        Float lapMaxSpeed = lapMesg.getFieldFloatValue(FitFile.LAP_EMSPEED);

        Long splitEndTime = estimateSplitEndTime(lapStartTime, lapElapsed != null ? lapElapsed : lapTimer);
        setLongIfPresent(splitMesg, FitFile.SPL_ETIME, splitEndTime);

        setFloatIfPresent(splitMesg, FitFile.SPL_TIMER, lapTimer != null ? lapTimer : 0f);
        setFloatIfPresent(splitMesg, FitFile.SPL_ETIMER, lapElapsed != null ? lapElapsed : (lapTimer != null ? lapTimer : 0f));
        setFloatIfPresent(splitMesg, FitFile.SPL_MTIMER, lapMoving != null ? lapMoving : (lapTimer != null ? lapTimer : 0f));
        setFloatIfPresent(splitMesg, FitFile.SPL_DIST, lapDist != null ? lapDist : 0f);

        Float lapStartDist = findLapStartDistanceMeters(lapIx, lapStartTime);
        if (lapStartDist != null) {
            setFloatIfPresent(splitMesg, FitFile.SPL_SDIST, lapStartDist * 100f);
        }

        float speed = 0f;
        if (lapSpeed != null) {
            speed = lapSpeed;
        } else if (lapDist != null && lapTimer != null && lapTimer > 0f) {
            speed = lapDist / lapTimer;
        }
        setFloatIfPresent(splitMesg, FitFile.SPL_SPEED, speed);

        float maxSpeed = lapMaxSpeed != null ? lapMaxSpeed : speed;
        setFloatIfPresent(splitMesg, FitFile.SPL_MSPEED, maxSpeed);

        setIntIfPresent(splitMesg, FitFile.SPL_CAD, getMesgFieldAsInt(lapMesg, FitFile.LAP_CAD));
        setIntIfPresent(splitMesg, FitFile.SPL_MCAD, getMesgFieldAsInt(lapMesg, FitFile.LAP_MCAD));
        setIntIfPresent(splitMesg, FitFile.SPL_POW, getMesgFieldAsInt(lapMesg, FitFile.LAP_POW));
        setIntIfPresent(splitMesg, FitFile.SPL_MPOW, getMesgFieldAsInt(lapMesg, FitFile.LAP_MPOW));
        setIntIfPresent(splitMesg, FitFile.SPL_ASC, lapMesg.getFieldIntegerValue(FitFile.LAP_ASC));
        setIntIfPresent(splitMesg, FitFile.SPL_DESC, lapMesg.getFieldIntegerValue(FitFile.LAP_DESC));

        int splitStartElevation = 0;
        int lapStartRecordIx = findRecordIndexAtOrAfterTime(lapStartTime);
        if (lapStartRecordIx >= 0 && lapStartRecordIx < fitFile.getRecordMesg().size()) {
            Float startAlt = fitFile.getRecordMesg().get(lapStartRecordIx).getFieldFloatValue(FitFile.REC_EALT);
            splitStartElevation = Math.round(startAlt != null ? startAlt : 0f);
        }
        setIntIfPresent(splitMesg, FitFile.SPL_SELE, splitStartElevation);
        setIntIfPresent(splitMesg, SPL_TEMP_FIELD_NUM, getMesgFieldAsInt(lapMesg, FitFile.LAP_TEMP));
        setIntIfPresent(splitMesg, SPL_MAXTEMP_FIELD_NUM, getMesgFieldAsInt(lapMesg, FitFile.LAP_MTEMP));
        setIntIfPresent(splitMesg, SPL_MINTEMP_FIELD_NUM, getMesgFieldAsInt(lapMesg, FitFile.LAP_MINTEMP));

        setIntIfPresent(splitMesg, FitFile.SPL_SLAT, lapMesg.getFieldIntegerValue(FitFile.LAP_SLAT));
        setIntIfPresent(splitMesg, FitFile.SPL_SLON, lapMesg.getFieldIntegerValue(FitFile.LAP_SLON));
        setIntIfPresent(splitMesg, FitFile.SPL_ELAT, lapMesg.getFieldIntegerValue(FitFile.LAP_ELAT));
        setIntIfPresent(splitMesg, FitFile.SPL_ELON, lapMesg.getFieldIntegerValue(FitFile.LAP_ELON));
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void syncSplitsFromLapsAfterLapChange(String context) {
        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.appendTempUpdateLogLn("-- Split sync skipped (no laps) [" + context + "]");
            return;
        }
        if (fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty()) {
            fitFile.appendTempUpdateLogLn("-- Split sync skipped (no splits) [" + context + "]");
            return;
        }

        int synced = 0;
        int noMatch = 0;
        Set<Integer> usedSplitIndexes = new HashSet<>();

        for (int lapIx = 0; lapIx < fitFile.getLapMesg().size(); lapIx++) {
            Mesg lap = fitFile.getLapMesg().get(lapIx);
            SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);
            if (match == null) {
                noMatch++;
                continue;
            }

            usedSplitIndexes.add(match.splitListIndex);
            applyLapMetricsToSplit(lapIx, match.splitMesg, lap);
            setIntIfPresent(match.splitMesg, SPL_LAP_INDEX_FIELD_NUM, lapIx);
            synced++;
        }

        fitFile.appendTempUpdateLogLn("-- Split sync complete [" + context + "]: synced="
            + synced + ", lapsWithoutSplit=" + noMatch);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Float findLapStartDistanceMeters(int lapIx, Long lapStartTime) {
        int startRecordIx = findRecordIndexAtOrAfterTime(lapStartTime);
        if (startRecordIx >= 0 && startRecordIx < fitFile.getRecordMesg().size()) {
            return fitFile.getRecordMesg().get(startRecordIx).getFieldFloatValue(FitFile.REC_DIST);
        }

        if (lapIx > 0 && lapIx - 1 < fitFile.getLapMesg().size()) {
            Float prevLapDist = fitFile.getLapMesg().get(lapIx - 1).getFieldFloatValue(FitFile.LAP_DIST);
            if (prevLapDist != null) {
                return prevLapDist;
            }
        }
        return null;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Long estimateSplitEndTime(Long splitStartTime, Float totalElapsedSeconds) {
        if (splitStartTime == null || totalElapsedSeconds == null) {
            return null;
        }
        return splitStartTime + Math.round(totalElapsedSeconds);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void removeSplitMesg(Mesg splitMesg) {
        if (splitMesg == null) {
            return;
        }
        fitFile.getSplitMesg().remove(splitMesg);
        fitFile.getAllMesg().remove(splitMesg);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private String formatSec(Float value) {
        if (value == null) {
            return "null";
        }
        return String.format("%.1fs", value);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private List<Integer> findLapsByTimer(Float splitTimer) {
        List<Integer> matches = new ArrayList<>();
        if (splitTimer == null || fitFile.getLapMesg() == null) {
            return matches;
        }
        for (int lapIx = 0; lapIx < fitFile.getLapMesg().size(); lapIx++) {
            Float lapTimer = fitFile.getLapMesg().get(lapIx).getFieldFloatValue(FitFile.LAP_TIMER);
            if (lapTimer == null) {
                continue;
            }
            if (Math.abs(lapTimer - splitTimer) <= SPLIT_TIMER_MATCH_TOLERANCE_SEC) {
                matches.add(lapIx);
            }
        }
        return matches;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Integer findAdjacentLapPairBySummedTimer(Float splitTimer) {
        if (splitTimer == null || fitFile.getLapMesg() == null || fitFile.getLapMesg().size() < 2) {
            return null;
        }
        for (int lapIx = 0; lapIx < fitFile.getLapMesg().size() - 1; lapIx++) {
            Float lap1 = fitFile.getLapMesg().get(lapIx).getFieldFloatValue(FitFile.LAP_TIMER);
            Float lap2 = fitFile.getLapMesg().get(lapIx + 1).getFieldFloatValue(FitFile.LAP_TIMER);
            if (lap1 == null || lap2 == null) {
                continue;
            }
            if (Math.abs((lap1 + lap2) - splitTimer) <= SPLIT_TIMER_MATCH_TOLERANCE_SEC) {
                return lapIx;
            }
        }
        return null;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private String toLapNoList(List<Integer> lapIndexes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lapIndexes.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(lapIndexes.get(i) + 1);
        }
        return sb.toString();
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findFirstRecordIndexAtOrAfterTimer(Long totalTimer) {
        for (int i = 0; i < fitFile.getRecordMesgAddOnRecords().size(); i++) {
            Long timer = fitFile.getRecordMesgAddOnRecords().get(i).getTimer();
            if (timer != null && timer >= totalTimer) {
                return i;
            }
        }
        return fitFile.getRecordMesgAddOnRecords().size() - 1;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findLapIndexForTime(Long timeValue) {
        int lapIx = -1;
        for (int i = 0; i < fitFile.getLapMesg().size(); i++) {
            Long lapStart = fitFile.getLapMesg().get(i).getFieldLongValue(FitFile.LAP_STIME);
            if (lapStart != null && lapStart <= timeValue) {
                lapIx = i;
            } else if (lapStart != null && lapStart > timeValue) {
                break;
            }
        }
        return lapIx;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findRecordIndexAtOrAfterTime(Long timeValue) {
        if (timeValue == null) {
            return -1;
        }
        for (int i = 0; i < fitFile.getRecordMesg().size(); i++) {
            Long recordTime = fitFile.getRecordMesg().get(i).getFieldLongValue(FitFile.REC_TIME);
            if (recordTime != null && recordTime >= timeValue) {
                return i;
            }
        }
        return -1;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findLapRecordEndIndex(int lapIx) {
        if (lapIx < 0 || lapIx >= fitFile.getLapMesg().size()) {
            return -1;
        }

        if (lapIx + 1 >= fitFile.getLapMesg().size()) {
            return fitFile.getRecordMesg().size() - 1;
        }

        Long nextLapStartTime = fitFile.getLapMesg().get(lapIx + 1).getFieldLongValue(FitFile.LAP_STIME);
        if (nextLapStartTime == null) {
            return fitFile.getRecordMesg().size() - 1;
        }

        int nextLapStartRecordIx = findRecordIndexAtOrAfterTime(nextLapStartTime);
        if (nextLapStartRecordIx <= 0) {
            return fitFile.getRecordMesg().size() - 1;
        }

        return nextLapStartRecordIx;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private LapBoundaryValues getBoundaryBeforeLap(int lapIx) {
        if (lapIx <= 0) {
            return new LapBoundaryValues(0f, 0L, fitFile.getTimeFirstRecord(), fitFile.getTimeFirstRecord());
        }

        int prevLapEndRecordIx = findLapRecordEndIndex(lapIx - 1);
        if (prevLapEndRecordIx < 0 || prevLapEndRecordIx >= fitFile.getRecordMesg().size()) {
            return new LapBoundaryValues(0f, 0L, fitFile.getTimeFirstRecord(), fitFile.getTimeFirstRecord());
        }

        Mesg prevLapEndRecord = fitFile.getRecordMesg().get(prevLapEndRecordIx);
        Float previousDistance = prevLapEndRecord.getFieldFloatValue(FitFile.REC_DIST);
        Long previousTimer = fitFile.getRecordMesgAddOnRecords().get(prevLapEndRecordIx).getTimer();
        Long previousRecordTime = prevLapEndRecord.getFieldLongValue(FitFile.REC_TIME);

        return new LapBoundaryValues(
            previousDistance != null ? previousDistance : 0f,
            previousTimer != null ? previousTimer : 0L,
            previousRecordTime,
            previousRecordTime);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private LapBoundaryValues recalculateLapValuesFromRecords(Mesg lapMesg, int recordIxStart, int recordIxEnd) {
        Mesg startRecord = fitFile.getRecordMesg().get(recordIxStart);
        Mesg endRecord = fitFile.getRecordMesg().get(recordIxEnd);

        Long startTime = startRecord.getFieldLongValue(FitFile.REC_TIME);
        Long endTime = endRecord.getFieldLongValue(FitFile.REC_TIME);
        Float startDistance = startRecord.getFieldFloatValue(FitFile.REC_DIST);
        Float endDistance = endRecord.getFieldFloatValue(FitFile.REC_DIST);
        Long startTimer = fitFile.getRecordMesgAddOnRecords().get(recordIxStart).getTimer();
        Long endTimer = fitFile.getRecordMesgAddOnRecords().get(recordIxEnd).getTimer();

        float lapDistance = (startDistance != null && endDistance != null)
            ? Math.max(0f, endDistance - startDistance)
            : 0f;
        float lapTimer = (startTimer != null && endTimer != null)
            ? Math.max(0L, endTimer - startTimer)
            : 0L;
        float lapElapsed = (startTime != null && endTime != null)
            ? Math.max(0L, endTime - startTime)
            : 0L;
        float lapMoving = lapTimer;

        int hrSum = 0;
        int hrCount = 0;
        int hrMax = 0;

        int powerSum = 0;
        int powerCount = 0;
        int powerMax = 0;

        int cadenceSum = 0;
        int cadenceCount = 0;
        int cadenceMax = 0;

        int tempSum = 0;
        int tempCount = 0;
        int tempMax = Integer.MIN_VALUE;
        int tempMin = Integer.MAX_VALUE;

        float altSum = 0f;
        int altCount = 0;
        float altMax = Float.NEGATIVE_INFINITY;
        float altMin = Float.POSITIVE_INFINITY;
        float ascent = 0f;
        float descent = 0f;
        Float previousAlt = null;

        boolean haveEnhancedSpeed = false;
        float maxObservedSpeed = 0f;

        for (int i = recordIxStart; i <= recordIxEnd; i++) {
            Mesg record = fitFile.getRecordMesg().get(i);

            Integer hr = getMesgFieldAsInt(record, FitFile.REC_HR);
            if (hr != null) {
                hrSum += hr;
                hrCount++;
                if (hr > hrMax) {
                    hrMax = hr;
                }
            }

            Integer power = getMesgFieldAsInt(record, FitFile.REC_POW);
            if (power != null) {
                powerSum += power;
                powerCount++;
                if (power > powerMax) {
                    powerMax = power;
                }
            }

            Integer cadence = getMesgFieldAsInt(record, FitFile.REC_CAD);
            if (cadence != null) {
                cadenceSum += cadence;
                cadenceCount++;
                if (cadence > cadenceMax) {
                    cadenceMax = cadence;
                }
            }

            Integer temp = getMesgFieldAsInt(record, RecordMesg.TemperatureFieldNum);
            if (temp != null) {
                tempSum += temp;
                tempCount++;
                if (temp > tempMax) {
                    tempMax = temp;
                }
                if (temp < tempMin) {
                    tempMin = temp;
                }
            }

            Float alt = record.getFieldFloatValue(FitFile.REC_EALT);
            if (alt != null) {
                altSum += alt;
                altCount++;
                if (alt > altMax) {
                    altMax = alt;
                }
                if (alt < altMin) {
                    altMin = alt;
                }
                if (previousAlt != null) {
                    float diff = alt - previousAlt;
                    if (diff > 0f) {
                        ascent += diff;
                    } else if (diff < 0f) {
                        descent += -diff;
                    }
                }
                previousAlt = alt;
            }

            Float enhancedSpeed = record.getFieldFloatValue(FitFile.REC_ESPEED);
            Float standardSpeed = record.getFieldFloatValue(FitFile.REC_SPEED);
            if (enhancedSpeed != null) {
                haveEnhancedSpeed = true;
                if (enhancedSpeed > maxObservedSpeed) {
                    maxObservedSpeed = enhancedSpeed;
                }
            } else if (!haveEnhancedSpeed && standardSpeed != null && standardSpeed > maxObservedSpeed) {
                maxObservedSpeed = standardSpeed;
            }
        }

        float avgSpeed = lapTimer > 0f ? lapDistance / lapTimer : 0f;

        setLongIfPresent(lapMesg, FitFile.LAP_STIME, startTime);
        setLongIfPresent(lapMesg, FitFile.LAP_TIME, fitFile.getActivityDateTimeUTC()); // LAP_TIME is allways the start time of activity

        setIntIfPresent(lapMesg, FitFile.LAP_SLAT, startRecord.getFieldIntegerValue(FitFile.REC_LAT));
        setIntIfPresent(lapMesg, FitFile.LAP_SLON, startRecord.getFieldIntegerValue(FitFile.REC_LON));
        setIntIfPresent(lapMesg, FitFile.LAP_ELAT, endRecord.getFieldIntegerValue(FitFile.REC_LAT));
        setIntIfPresent(lapMesg, FitFile.LAP_ELON, endRecord.getFieldIntegerValue(FitFile.REC_LON));

        setFloatIfPresent(lapMesg, FitFile.LAP_DIST, lapDistance);
        setFloatIfPresent(lapMesg, FitFile.LAP_TIMER, lapTimer);
        setFloatIfPresent(lapMesg, FitFile.LAP_ETIMER, lapElapsed);
        setFloatIfPresent(lapMesg, FitFile.LAP_MTIMER, lapMoving);
        setFloatIfPresent(lapMesg, FitFile.LAP_SPEED, avgSpeed);
        setFloatIfPresent(lapMesg, FitFile.LAP_ESPEED, avgSpeed);
        setFloatIfPresent(lapMesg, FitFile.LAP_MSPEED, maxObservedSpeed);
        setFloatIfPresent(lapMesg, FitFile.LAP_EMSPEED, maxObservedSpeed);
        setFloatIfPresent(lapMesg, FitFile.LAP_ALT, altCount > 0 ? altSum / altCount : 0f);
        setFloatIfPresent(lapMesg, FitFile.LAP_MALT, altCount > 0 ? altMax : 0f);
        setFloatIfPresent(lapMesg, FitFile.LAP_MINALT, altCount > 0 ? altMin : 0f);
        setIntIfPresent(lapMesg, FitFile.LAP_ASC, Math.round((float) ascent));
        setIntIfPresent(lapMesg, FitFile.LAP_DESC, Math.round((float) descent));

        setIntIfPresent(lapMesg, FitFile.LAP_HR, hrCount > 0 ? Math.round((float) hrSum / hrCount) : 0);
        setIntIfPresent(lapMesg, FitFile.LAP_MHR, hrMax);
        setIntIfPresent(lapMesg, FitFile.LAP_POW, powerCount > 0 ? Math.round((float) powerSum / powerCount) : 0);
        setIntIfPresent(lapMesg, FitFile.LAP_MPOW, powerMax);
        setIntIfPresent(lapMesg, FitFile.LAP_CAD, cadenceCount > 0 ? Math.round((float) cadenceSum / cadenceCount) : 0);
        setIntIfPresent(lapMesg, FitFile.LAP_MCAD, cadenceMax);
        setIntIfPresent(lapMesg, FitFile.LAP_TEMP, tempCount > 0 ? Math.round((float) tempSum / tempCount) : 0);
        setIntIfPresent(lapMesg, FitFile.LAP_MTEMP, tempCount > 0 ? tempMax : 0);
        setIntIfPresent(lapMesg, FitFile.LAP_MINTEMP, tempCount > 0 ? tempMin : 0);

        fitFile.appendTempUpdateLogLn("-- Recalculated lap metrics from records ix " + recordIxStart + "-" + recordIxEnd
            + ": timer=" + PehoUtils.sec2minSecLong(lapTimer)
            + ", dist=" + Math.round(lapDistance) + "m"
            + ", hrAvg=" + (hrCount > 0 ? Math.round((float) hrSum / hrCount) : 0)
            + ", speed=" + PehoUtils.mps2minpkm(avgSpeed));

        return new LapBoundaryValues(
            endDistance != null ? endDistance : 0f,
            endTimer != null ? endTimer : 0L,
            endTime,
            startTime,
            lapTimer);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void incrementLapReferencesAfterInsertedLap(int insertedAfterLapIx, Mesg insertedLap, Mesg insertedTimeInZone, Mesg insertedSplit) {
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() == MesgNum.LAP) {
                if (mesg == insertedLap) {
                    continue;
                }
                Integer lapIx = mesg.getFieldIntegerValue(FitFile.LAP_IX);
                if (lapIx != null && lapIx > insertedAfterLapIx) {
                    mesg.setFieldValue(FitFile.LAP_IX, lapIx + 1);
                }
                continue;
            }

            if (mesg.getNum() == MesgNum.TIME_IN_ZONE) {
                if (mesg == insertedTimeInZone) {
                    continue;
                }
                Integer referenceMesg = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_MESG);
                Integer referenceIndex = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_IX);
                if (referenceMesg != null
                    && referenceMesg == MesgNum.LAP
                    && referenceIndex != null
                    && referenceIndex > insertedAfterLapIx) {
                    mesg.setFieldValue(FitFile.TIZ_REF_IX, referenceIndex + 1);
                }
                continue;
            }

            if (mesg.getNum() == MesgNum.SPLIT) {
                if (mesg == insertedSplit) {
                    continue;
                }
                Integer splitLapIx = getMesgFieldAsInt(mesg, SPL_LAP_INDEX_FIELD_NUM);
                if (splitLapIx != null && splitLapIx > insertedAfterLapIx) {
                    mesg.setFieldValue(SPL_LAP_INDEX_FIELD_NUM, splitLapIx + 1);
                }
            }
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void setFloatIfPresent(Mesg mesg, int fieldNum, float value) {
        if (mesg.getFieldFloatValue(fieldNum) != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void setIntIfPresent(Mesg mesg, int fieldNum, Integer value) {
        if (value == null) {
            return;
        }
        Integer intValue = mesg.getFieldIntegerValue(fieldNum);
        if (intValue != null) {
            mesg.setFieldValue(fieldNum, value);
            return;
        }
        Short shortValue = mesg.getFieldShortValue(fieldNum);
        if (shortValue != null) {
            mesg.setFieldValue(fieldNum, value.shortValue());
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void setLongIfPresent(Mesg mesg, int fieldNum, Long value) {
        if (value == null) {
            return;
        }
        if (mesg.getFieldLongValue(fieldNum) != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static class LapBoundaryValues {
        private final float lastDistance;
        private final long lastTimer;
        private final Long lastRecordTime;
        private final Long startRecordTime;
        private final Float totalTimer;

        private LapBoundaryValues(float lastDistance, long lastTimer, Long lastRecordTime, Long startRecordTime) {
            this(lastDistance, lastTimer, lastRecordTime, startRecordTime, null);
        }

        private LapBoundaryValues(float lastDistance, long lastTimer, Long lastRecordTime, Long startRecordTime, Float totalTimer) {
            this.lastDistance = lastDistance;
            this.lastTimer = lastTimer;
            this.lastRecordTime = lastRecordTime;
            this.startRecordTime = startRecordTime;
            this.totalTimer = totalTimer;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static class SplitMatch {
        private final Mesg splitMesg;
        private final int splitListIndex;
        private final int lapIx;
        private final Float splitTimer;
        private final Float lapTimer;
        private final String matchReason;

        private SplitMatch(Mesg splitMesg, int splitListIndex, int lapIx, Float splitTimer, Float lapTimer, String matchReason) {
            this.splitMesg = splitMesg;
            this.splitListIndex = splitListIndex;
            this.lapIx = lapIx;
            this.splitTimer = splitTimer;
            this.lapTimer = lapTimer;
            this.matchReason = matchReason;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findLapMesgIndexInLapMesgByLapIx(int lapIx) {
        return FindMesgIx.findMesgIndexByIntField(fitFile.getLapMesg(), MesgNum.LAP, FitFile.LAP_IX, lapIx);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findLinkedTimeInZoneMesgIndex(int lapAllMesgIx, int lapIx) {
        int nextIx = lapAllMesgIx + 1;
        if (nextIx < fitFile.getAllMesg().size()) {
            Mesg nextMesg = fitFile.getAllMesg().get(nextIx);
            if (isLinkedTimeInZoneMesg(nextMesg, lapIx)) {
                return nextIx;
            }
        }

        for (int i = 0; i < fitFile.getAllMesg().size(); i++) {
            if (i == lapAllMesgIx || i == nextIx) {
                continue;
            }
            if (isLinkedTimeInZoneMesg(fitFile.getAllMesg().get(i), lapIx)) {
                return i;
            }
        }
        return -1;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void decrementLapReferencesAfterDeletedLap(int deletedLapIx) {
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() == MesgNum.LAP) {
                Integer lapIx = mesg.getFieldIntegerValue(FitFile.LAP_IX);
                if (lapIx != null && lapIx > deletedLapIx) {
                    mesg.setFieldValue(FitFile.LAP_IX, lapIx - 1);
                }
                continue;
            }

            if (mesg.getNum() == MesgNum.TIME_IN_ZONE) {
                Integer referenceMesg = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_MESG);
                Integer referenceIndex = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_IX);
                if (referenceMesg != null
                    && referenceMesg == MesgNum.LAP
                    && referenceIndex != null
                    && referenceIndex > deletedLapIx) {
                    mesg.setFieldValue(FitFile.TIZ_REF_IX, referenceIndex - 1);
                }
                continue;
            }

            if (mesg.getNum() == MesgNum.SPLIT) {
                Integer splitLapIx = getMesgFieldAsInt(mesg, SPL_LAP_INDEX_FIELD_NUM);
                if (splitLapIx != null && splitLapIx > deletedLapIx) {
                    mesg.setFieldValue(SPL_LAP_INDEX_FIELD_NUM, splitLapIx - 1);
                }
            }
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private boolean isLinkedTimeInZoneMesg(Mesg mesg, int lapIx) {
        if (mesg.getNum() != MesgNum.TIME_IN_ZONE) {
            return false;
        }
        Integer referenceMesg = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_MESG);
        Integer referenceIndex = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_IX);
        return referenceMesg != null
            && referenceMesg == MesgNum.LAP
            && referenceIndex != null
            && referenceIndex == lapIx;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Integer getMesgFieldAsInt(Mesg mesg, int fieldNum) {
        Integer intValue = mesg.getFieldIntegerValue(fieldNum);
        if (intValue != null) {
            return intValue;
        }
        Short shortValue = mesg.getFieldShortValue(fieldNum);
        if (shortValue != null) {
            return shortValue.intValue();
        }
        Long longValue = mesg.getFieldLongValue(fieldNum);
        if (longValue != null) {
            return longValue.intValue();
        }
        return null;
    }
}