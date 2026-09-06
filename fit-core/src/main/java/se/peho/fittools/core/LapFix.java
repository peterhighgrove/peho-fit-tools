package se.peho.fittools.core;

import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import com.garmin.fit.Intensity;
import com.garmin.fit.SplitType;
import com.garmin.fit.RecordMesg;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.peho.fittools.core.FitFile.LapExtraMesg;
import se.peho.fittools.core.strings.*;

public class LapFix {

    private static final float SPLIT_TIMER_MATCH_TOLERANCE_SEC = 1.1f;

    private final FitFile fitFile;

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public LapFix(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSplitLapMatchReport() {

        fitFile.printAndAppendUpdateLogLn("");
        fitFile.printAndAppendUpdateLogLn("==================================================");
        fitFile.printAndAppendUpdateLogLn("SPLIT vs LAP MATCH ANALYZE");
        fitFile.printAndAppendUpdateLogLn("Rule 1: SPL_LAPIX (field " + FitFile.SPL_LAPIX + ")");
        fitFile.printAndAppendUpdateLogLn("Rule 2: SPL_TIMER fallback (tolerance +/-" + SPLIT_TIMER_MATCH_TOLERANCE_SEC + "s)");
        fitFile.printAndAppendUpdateLogLn("--------------------------------------------------");

        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("No LAP messages found.");
            return;
        }
        if (fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("No SPLIT messages found.");
            return;
        }

        detectAndFixCombinedSplits();

        fitFile.printAndAppendUpdateLogLn("--------------------------------------------------");
        fitFile.printAndAppendUpdateLogLn("LAP -> SPLIT");
        fitFile.printAndAppendUpdateLogLn("--------------------------------------------------");
        Set<Integer> usedSplitIndexes = new HashSet<>();
        for (int lapIx = 0; lapIx < fitFile.getLapMesg().size(); lapIx++) {
            Mesg lap = fitFile.getLapMesg().get(lapIx);
            SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);
            Float lapTimer = lap.getFieldFloatValue(FitFile.LAP_TIMER);
            if (match != null) {
                usedSplitIndexes.add(match.splitListIndex);
                fitFile.printAndAppendUpdateLogLn("LAP " + (lapIx + 1)
                    + " timer=" + formatSec(lapTimer)
                    + " -> SPLIT " + (match.splitListIndex + 1)
                    + " type=" + formatSplitType(match.splitMesg)
                    + " by " + match.matchReason
                    + " splitTimer=" + formatSec(match.splitTimer));
            } else {
                fitFile.printAndAppendUpdateLogLn("LAP " + (lapIx + 1)
                    + " timer=" + formatSec(lapTimer)
                    + " -> no matching split");
            }
        }

        fitFile.printAndAppendUpdateLogLn("--------------------------------------------------");
        fitFile.printAndAppendUpdateLogLn("SPLIT -> LAP (for SPLIT records without usable SPL_LAPIX)");
        fitFile.printAndAppendUpdateLogLn("--------------------------------------------------");

        int missingLapIxCount = 0;
        for (int splitIx = 0; splitIx < fitFile.getSplitMesg().size(); splitIx++) {
            Mesg split = fitFile.getSplitMesg().get(splitIx);
            Integer splitLapIx = split.getFieldIntegerValue(FitFile.SPL_LAPIX);
            Float splitTimer = split.getFieldFloatValue(FitFile.SPL_TIMER);
            String splitType = formatSplitType(split);

            if (splitLapIx != null && splitLapIx >= 0 && splitLapIx < fitFile.getLapMesg().size()) {
                continue;
            }

            missingLapIxCount++;
            List<Integer> singleLapMatches = findLapsByTimer(splitTimer);
            Integer pairStartLapIx = findAdjacentLapPairBySummedTimer(splitTimer);

            if (singleLapMatches.size() == 1) {
                int lapNo = singleLapMatches.get(0) + 1;
                fitFile.printAndAppendUpdateLogLn("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " type=" + splitType
                    + " timer=" + formatSec(splitTimer)
                    + " -> single LAP TIMER match: LAP " + lapNo
                    + " (candidate to set SPL_LAPIX)");
            } else if (singleLapMatches.size() > 1) {
                fitFile.printAndAppendUpdateLogLn("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " type=" + splitType
                    + " timer=" + formatSec(splitTimer)
                    + " -> multiple LAP TIMER matches: " + toLapNoList(singleLapMatches)
                    + " (ambiguous)");
            } else if (pairStartLapIx != null) {
                int lapNo1 = pairStartLapIx + 1;
                int lapNo2 = pairStartLapIx + 2;
                Float lap1Timer = fitFile.getLapMesg().get(pairStartLapIx).getFieldFloatValue(FitFile.LAP_TIMER);
                Float lap2Timer = fitFile.getLapMesg().get(pairStartLapIx + 1).getFieldFloatValue(FitFile.LAP_TIMER);
                fitFile.printAndAppendUpdateLogLn("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " type=" + splitType
                    + " timer=" + formatSec(splitTimer)
                    + " -> matches LAP pair sum: LAP " + lapNo1 + " + LAP " + lapNo2
                    + " (" + formatSec(lap1Timer) + " + " + formatSec(lap2Timer) + ")"
                    + " => candidate for merge/split handling");
            } else {
                fitFile.printAndAppendUpdateLogLn("SPLIT " + (splitIx + 1)
                    + " lapIx=" + splitLapIx
                    + " type=" + splitType
                    + " timer=" + formatSec(splitTimer)
                    + " -> no LAP TIMER match");
            }
        }

        if (missingLapIxCount == 0) {
            fitFile.printAndAppendUpdateLogLn("All SPLIT messages already have valid SPL_LAPIX.");
        }
        fitFile.printAndAppendUpdateLogLn("==================================================");
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void lapMerge(int fromLap, int toLap) {

        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("==XX> No lap messages found.");
            return;
        }
        if (fromLap < 1 || toLap < 1 || fromLap > toLap || toLap > fitFile.getLapMesg().size()) {
            fitFile.printAndAppendUpdateLogLn("==XX> Invalid lap merge range: " + fromLap + "-" + toLap);
            return;
        }
        if (fitFile.getRecordMesg() == null || fitFile.getRecordMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("==XX> No record messages found.");
            return;
        }

        int fromLapIx = fromLap - 1;
        int toLapIx = toLap - 1;

        Mesg firstLap = fitFile.getLapMesg().get(fromLapIx);
        Mesg mergedLap = fitFile.getLapMesg().get(toLapIx);

        Long orgLapStartTime = firstLap.getFieldLongValue(FitFile.LAP_STIME);
        // Calculate the total tTimer and eTimer for the merged lap, based on the sum of the tTimer and eTimer values for the laps being merged.
        // ------------------------------------------------
        Float orgLapTTimer = 0f;
        Float orgLapETimer = 0f;
        for (Mesg lap : fitFile.getLapMesg().subList(fromLapIx, toLapIx + 1)) {
            Float lapTTimer = lap.getFieldFloatValue(FitFile.LAP_TIMER);
            Float lapETimer = lap.getFieldFloatValue(FitFile.LAP_ETIMER);
            orgLapTTimer += lapTTimer != null ? lapTTimer : 0f;
            orgLapETimer += lapETimer != null ? lapETimer : 0f;
        }
        Integer originalMergedStartLat = firstLap.getFieldIntegerValue(FitFile.LAP_SLAT);
        Integer originalMergedStartLon = firstLap.getFieldIntegerValue(FitFile.LAP_SLON);

        if (orgLapStartTime == null) {
            fitFile.printAndAppendUpdateLogLn("==XX> First lap in merge range has no start time.");
            return;
        }

        // Analyze the split matches for the range of laps being merged, to determine which splits are associated with the laps being merged.
        List<SplitMatch> splitMatchesToMerge = analyzeSplitMatchesForLapRange(fromLapIx, toLapIx);
        Set<Short> affectedSplitTypes = new HashSet<>();
        for (SplitMatch match : splitMatchesToMerge) {
                Short splitType = match.splitMesg.getFieldShortValue(FitFile.SPL_TYPE);
                if (splitType != null) {
                    affectedSplitTypes.add(splitType);
                }
        }
 
        // Deleting the merged laps (fromLap to toLap-1)
        //-----------------------------------------------
        int deleteCount = toLap - fromLap;
        int targetLapIx = fromLap - 1;
        for (int deleteCounter = 0; deleteCounter < deleteCount; deleteCounter++) {

            // Find the index of the LAP message in the ALL messages list that corresponds to the target lap index.
            int lapAllMesgIx = findLapMesgIndexInAllMesgByLapIx(targetLapIx);
            if (lapAllMesgIx < 0) {
                fitFile.printAndAppendUpdateLogLn("-- Could not find LAP mesg in allMesg for lap ix:" + targetLapIx);
                continue;
            }

            // Delete the LAP message from the ALL messages list, and also delete any linked TIME_IN_ZONE message for this lap, if it exists.
            Mesg lapMesgToDelete = fitFile.getAllMesg().get(lapAllMesgIx);
            fitFile.printAndAppendUpdateLogLn("-- Deleting lap ix:" + targetLapIx + " time:"
                + FitDateTime.toString(lapMesgToDelete.getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()));

            // Delete any linked TIME_IN_ZONE message for this lap, if it exists.
            int timeInZoneIx = findLinkedTimeInZoneMesgIndex(lapAllMesgIx, targetLapIx);
            if (timeInZoneIx >= 0) {
                fitFile.printAndAppendUpdateLogLn("-- Deleting linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                int firstRemoveIx = Math.max(lapAllMesgIx, timeInZoneIx);
                int secondRemoveIx = Math.min(lapAllMesgIx, timeInZoneIx);
                fitFile.getAllMesg().remove(firstRemoveIx);
                fitFile.getAllMesg().remove(secondRemoveIx);
            } else {
                fitFile.printAndAppendUpdateLogLn("-- Could not find linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                fitFile.getAllMesg().remove(lapAllMesgIx);
            }

            int lapMesgIx = findLapMesgIndexInLapMesgByLapIx(targetLapIx);
            if (lapMesgIx >= 0) {
                fitFile.getLapMesg().remove(lapMesgIx);
            }

            decrementLapReferencesAfterDeletedLap(targetLapIx);
        }

        fitFile.setNumberOfLaps(fitFile.getNumberOfLaps() - (toLap - fromLap));
        if (!fitFile.getSessionMesg().isEmpty()) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_LAPS, fitFile.getNumberOfLaps());
        }

        // Preserve the original start time of the merged lap, since the merged lap is now the first lap in the range.
        setLongIfNotNull(mergedLap, FitFile.LAP_STIME, orgLapStartTime);
        setIntIfNotNull(mergedLap, FitFile.LAP_IX, fromLapIx);

        // Update LapExtra records to reflect that the merged laps have been deleted, and the remaining laps have been renumbered.
        // start time and numberOfLaps need to be set in beforehand
        fitFile.fillLapExtraRecords();

        LapBoundaryValues mergedBoundary = recalculateLapValuesFromRecords(fromLapIx);

        // Preserve the original first-lap start identity fields on merged lap.
        Float lapTTimer = mergedLap.getFieldFloatValue(FitFile.LAP_TIMER);
        Float diffLapTTimer = orgLapTTimer - lapTTimer;
        fitFile.printAndAppendUpdateLogLn("lapMerge: originalLapTTimer=" + orgLapTTimer
            + ", newLapTTimer=" + lapTTimer
            + ", diffLapTTimer=" + diffLapTTimer);
        setFloatIfNotNull(mergedLap, FitFile.LAP_TIMER, lapTTimer + diffLapTTimer);
        fitFile.printAndAppendUpdateLogLn("lapMerge: adjusted tTimer=" + mergedLap.getFieldFloatValue(FitFile.LAP_TIMER));
            
        Float lapETimer = mergedLap.getFieldFloatValue(FitFile.LAP_ETIMER);
        Float diffLapETimer = orgLapETimer - lapETimer;
        fitFile.printAndAppendUpdateLogLn("lapMerge: originalLapETimer=" + orgLapETimer
            + ", newLapETimer=" + lapETimer
            + ", diffLapETimer=" + diffLapETimer);
        setFloatIfNotNull(mergedLap, FitFile.LAP_ETIMER, lapETimer + diffLapETimer);
        fitFile.printAndAppendUpdateLogLn("lapMerge: adjusted eTimer=" + mergedLap.getFieldFloatValue(FitFile.LAP_ETIMER));

        setIntIfNotNull(mergedLap, FitFile.LAP_SLAT, originalMergedStartLat);
        setIntIfNotNull(mergedLap, FitFile.LAP_SLON, originalMergedStartLon);

        mergeMatchedSplitsForLapMerge(splitMatchesToMerge, fromLapIx, toLapIx, mergedLap);
        renumberSplitMesgIndexes();
        updateSplitSummaryFromSplitsForTypes(affectedSplitTypes);

        fitFile.printAndAppendUpdateLogLn("Merged laps: " + fromLap + " to " + toLap);
        Float mergedDist = mergedLap.getFieldFloatValue(FitFile.LAP_DIST);
        fitFile.printAndAppendUpdateLogLn("-- New lap " + fromLap
            + " time: " + new TimeStr(mergedBoundary.totalTimer).get()
            + ", dist: " + Math.round(mergedDist != null ? mergedDist : 0f) + " m");

        // ALREADY DONE IN mergeMatchedSplitsForLapMerge() and updateSplitSummaryFromSplitsForTypes()
        // Only need to sync the merged lap, since the others were deleted.
        // syncSplitsFromLapsAfterLapChange("lapMerge", fromLapIx, fromLapIx); 

    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void applyWorkoutIntervalPattern(int warmupLaps, int cooldownLaps, boolean useRestAfterActive) {
        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("==XX> No LAP messages found. wkti aborted.");
            return;
        }

        int lapCount = fitFile.getLapMesg().size();
        if (warmupLaps < 0 || cooldownLaps < 0) {
            fitFile.printAndAppendUpdateLogLn("==XX> warmup/cooldown must be >= 0. wkti aborted.");
            return;
        }
        if (warmupLaps + cooldownLaps > lapCount) {
            fitFile.printAndAppendUpdateLogLn("==XX> warmup + cooldown exceeds number of laps (" + lapCount + "). wkti aborted.");
            return;
        }

        // Detect and fix any combined splits first, so that SPL_TYPE assignment can be done correctly.
        // ------------------------------------------------
        detectAndFixCombinedSplits();

        // Find the overview split (if any) that represents the entire activity, and skip it for SPL_TYPE assignment.
        // ------------------------------------------------
        int overviewSplitIx = findOverviewSplitIndex();

        // Keep track of which SPLIT messages have already been assigned to a LAP, so that we don't assign the same SPLIT to multiple laps.
        // ------------------------------------------------
        Set<Integer> usedSplitIndexes = new HashSet<>();
        if (overviewSplitIx >= 0) {
            usedSplitIndexes.add(overviewSplitIx);
            fitFile.printAndAppendUpdateLogLn("-- Skipping activity overview split " + (overviewSplitIx + 1) + " for wkti.");
        }

        int updatedLaps = 0;
        int updatedSplits = 0;
        int warmupSplitsUpdated = 0;
        int cooldownSplitsUpdated = 0;
        int activeSplitsUpdated = 0;
        int restSplitsUpdated = 0;
        int recoverySplitsUpdated = 0;
        Set<Short> splitTypesToRefreshSummary = new HashSet<>();
        int intervalStartIx = warmupLaps;
        int cooldownStartIx = lapCount - cooldownLaps;

        // Assign LAP_INTENSITY and SPL_TYPE for each lap, based on the workout interval pattern.
        // ------------------------------------------------
        for (int lapIx = 0; lapIx < lapCount; lapIx++) {
            Mesg lap = fitFile.getLapMesg().get(lapIx);

            // Assign LAP_INTENSITY based on the lap index and the workout interval pattern.
            Intensity intensity;
            if (lapIx < intervalStartIx) {
                intensity = Intensity.WARMUP;
            } else if (lapIx >= cooldownStartIx) {
                intensity = Intensity.COOLDOWN;
            } else {
                int intervalIx = lapIx - intervalStartIx;
                if ((intervalIx % 2) == 0) {
                    intensity = Intensity.ACTIVE;
                } else if (useRestAfterActive) {
                    intensity = Intensity.REST;
                } else {
                    intensity = Intensity.RECOVERY;
                }
            }
            lap.setFieldValue(FitFile.LAP_INTENSITY, intensity.getValue());
            updatedLaps++;
            
            // Find the best matching SPLIT for this lap, if any, and assign SPL_TYPE based on the lap index and the workout interval pattern.
            SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);

            // Assign SPL_TYPE for the matched split, if any, based on the lap index and the workout interval pattern.
            SplitType splitType;
            if (match != null) {
                usedSplitIndexes.add(match.splitListIndex);
                if (lapIx < intervalStartIx) {
                    splitType = SplitType.INTERVAL_WARMUP;
                } else if (lapIx >= cooldownStartIx) {
                    splitType = SplitType.INTERVAL_COOLDOWN;
                } else {
                    int intervalIx = lapIx - intervalStartIx;
                    if ((intervalIx % 2) == 0) {
                        splitType = SplitType.INTERVAL_ACTIVE;
                    } else if (useRestAfterActive) {
                        splitType = SplitType.INTERVAL_REST;
                    } else {
                        splitType = SplitType.INTERVAL_RECOVERY;
                    }
                }

                // Assign SPL_TYPE for the matched split, if it differs from the current value.
                Short currentSplitType = match.splitMesg.getFieldShortValue(FitFile.SPL_TYPE);
                if (currentSplitType == null || currentSplitType.shortValue() != splitType.getValue()) {
                    match.splitMesg.setFieldValue(FitFile.SPL_TYPE, splitType.getValue());
                    updatedSplits++;
                    splitTypesToRefreshSummary.add(splitType.getValue());
                    if (currentSplitType != null) {
                        // Old type lost a split; its SPLIT_SUMMARY totals must shrink too.
                        splitTypesToRefreshSummary.add(currentSplitType);
                    }

                    if (splitType == SplitType.INTERVAL_WARMUP) {
                        warmupSplitsUpdated++;
                    } else if (splitType == SplitType.INTERVAL_COOLDOWN) {
                        cooldownSplitsUpdated++;
                    } else if (splitType == SplitType.INTERVAL_ACTIVE) {
                        activeSplitsUpdated++;
                    } else if (splitType == SplitType.INTERVAL_REST) {
                        restSplitsUpdated++;
                    } else if (splitType == SplitType.INTERVAL_RECOVERY) {
                        recoverySplitsUpdated++;
                    }

                    fitFile.appendTempUpdateLogLn("-- wkti LAP " + (lapIx + 1)
                        + " -> SPLIT " + (match.splitListIndex + 1)
                        + " by " + match.matchReason
                        + " type=" + splitType + " (updated)");
                } else {
                    fitFile.appendTempUpdateLogLn("-- wkti LAP " + (lapIx + 1)
                        + " -> SPLIT " + (match.splitListIndex + 1)
                        + " by " + match.matchReason
                        + " type=" + splitType + " (unchanged)");
                }
            } else {
                fitFile.appendTempUpdateLogLn("-- wkti LAP " + (lapIx + 1)
                    + " -> no matching split found for type assignment.");
            }
        } // end foor loop over laps

        // Update SPLIT_SUMMARY rows for any SPL_TYPEs that were changed, so that the summary totals reflect the new split counts and totals.
        // ------------------------------------------------
        updateSplitSummaryFromSplitsForTypes(splitTypesToRefreshSummary);

        fitFile.printAndAppendUpdateLogLn("wkti applied: warmup=" + warmupLaps
            + ", cooldown=" + cooldownLaps
            + ", after-active=" + (useRestAfterActive ? "rest" : "recover")
            + ", laps=" + lapCount + ".");
        fitFile.printAndAppendUpdateLogLn("Updated LAP_INTENSITY for " + updatedLaps + " lap messages.");
        fitFile.printAndAppendUpdateLogLn("Updated SPL_TYPE for " + updatedSplits + " split messages.");
        fitFile.printAndAppendUpdateLogLn("Updated split types counts:"
            + " warmup=" + warmupSplitsUpdated
            + ", cooldown=" + cooldownSplitsUpdated
            + ", active=" + activeSplitsUpdated
            + ", rest=" + restSplitsUpdated
            + ", recovery=" + recoverySplitsUpdated);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Recalculates SPLIT_SUMMARY rows (incl. SPLSUM_SPLITS, the number of splits of that
    // type) from the current SPLIT list. Returns the log lines instead of writing them
    // directly to fitFile's shared temp log, since callers (spla vs wkti) manage logging differently.
    private void updateSplitSummaryFromSplitsForTypes(Set<Short> splitTypesToRefreshSummary) {

        if (splitTypesToRefreshSummary == null || splitTypesToRefreshSummary.isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("-- No SPL_TYPE changes, SPLIT_SUMMARY unchanged.");
            return;
        }
        if (fitFile.getSplitSummaryMesg() == null || fitFile.getSplitSummaryMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("-- No SPLIT_SUMMARY messages found.");
            return;
        }

        Map<Short, SplitSummaryAgg> aggByType = new HashMap<>();
        for (Short splitType : splitTypesToRefreshSummary) {
            aggByType.put(splitType, new SplitSummaryAgg());
        }

        for (Mesg split : fitFile.getSplitMesg()) {
            Short splitType = split.getFieldShortValue(FitFile.SPL_TYPE);
            if (splitType == null || !aggByType.containsKey(splitType)) {
                continue;
            }
            aggByType.get(splitType).addSplitToSummary(split);
        }

        int updatedSummaryRows = 0;
        int removedSummaryRows = 0;
        Set<Short> typesWithExistingRow = new HashSet<>();
        List<Mesg> summaryRowsToRemove = new ArrayList<>();

        for (Mesg splitSummary : fitFile.getSplitSummaryMesg()) {
            Short splitSummaryType = splitSummary.getFieldShortValue(FitFile.SPLSUM_TYPE);
            if (splitSummaryType == null || !aggByType.containsKey(splitSummaryType)) {
                continue;
            }
            typesWithExistingRow.add(splitSummaryType);

            SplitSummaryAgg agg = aggByType.get(splitSummaryType);
            if (agg.splitCount == 0) {
                // No splits of this type remain; drop the now-stale summary row.
                summaryRowsToRemove.add(splitSummary);
                continue;
            }
            applySplitSummaryAgg(splitSummary, agg);
            updatedSummaryRows++;
        }

        for (Mesg toRemove : summaryRowsToRemove) {
            fitFile.getSplitSummaryMesg().remove(toRemove);
            fitFile.getAllMesg().remove(toRemove);
            removedSummaryRows++;
        }

        int createdSummaryRows = 0;
        for (Map.Entry<Short, SplitSummaryAgg> entry : aggByType.entrySet()) {
            Short splitType = entry.getKey();
            SplitSummaryAgg agg = entry.getValue();
            if (typesWithExistingRow.contains(splitType) || agg.splitCount == 0) {
                continue;
            }
            createSplitSummaryRow(splitType, agg);
            createdSummaryRows++;
        }

        if (createdSummaryRows > 0) {
            renumberSplitSummaryMesgIndexes();
        }

        fitFile.printAndAppendUpdateLogLn("-- Updated SPLIT_SUMMARY rows: " + updatedSummaryRows
            + ", created: " + createdSummaryRows
            + ", removed (no splits left): " + removedSummaryRows
            + " for split types " + splitTypesToRefreshSummary + ".");
        return;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Applies the aggregated totals from SplitSummaryAgg to the given SPLIT_SUMMARY message.
    // This is used when recalculating SPLIT_SUMMARY rows after SPL_TYPE changes or split merges.
    private void applySplitSummaryAgg(Mesg splitSummary, SplitSummaryAgg agg) {
        splitSummary.setFieldValue(FitFile.SPLSUM_TIMER, agg.totalTimer);
        splitSummary.setFieldValue(FitFile.SPLSUM_MTIMER, agg.totalMovingTimer);
        splitSummary.setFieldValue(FitFile.SPLSUM_DIST, agg.totalDist);
        splitSummary.setFieldValue(FitFile.SPLSUM_SPEED, agg.totalTimer > 0f ? agg.totalDist / agg.totalTimer : 0f);
        splitSummary.setFieldValue(FitFile.SPLSUM_MSPEED, agg.maxSpeed);
        splitSummary.setFieldValue(FitFile.SPLSUM_VSPEED, agg.vertWeight > 0f ? agg.weightedVertSpeed / agg.vertWeight : 0f);
        splitSummary.setFieldValue(FitFile.SPLSUM_HR, agg.avgHr);
        splitSummary.setFieldValue(FitFile.SPLSUM_MHR, agg.maxHr);
        splitSummary.setFieldValue(FitFile.SPLSUM_CAD, agg.avgCadence);
        splitSummary.setFieldValue(FitFile.SPLSUM_MCAD, agg.maxCadence);
        splitSummary.setFieldValue(FitFile.SPLSUM_ASC, agg.totalAscent);
        splitSummary.setFieldValue(FitFile.SPLSUM_DESC, agg.totalDescent);
        splitSummary.setFieldValue(FitFile.SPLSUM_CAL, agg.totalCalories);
        setIntIfNotNull(splitSummary, FitFile.SPLSUM_SPLITS, agg.splitCount);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Creates a new SPLIT_SUMMARY message for the given split type, using the aggregated totals from SplitSummaryAgg.
    // The new SPLIT_SUMMARY message is added to both the SPLIT_SUMMARY list and the ALL messages list in the FitFile.
    private void createSplitSummaryRow(Short splitType, SplitSummaryAgg agg) {
        Mesg template = fitFile.getSplitSummaryMesg().get(0);
        Mesg newSummary = new Mesg(template);
        newSummary.setFieldValue(FitFile.SPLSUM_TYPE, splitType);
        applySplitSummaryAgg(newSummary, agg);

        fitFile.getSplitSummaryMesg().add(newSummary);

        int insertAllIx = findLastSplitSummaryAllMesgIndex();
        if (insertAllIx >= 0) {
            fitFile.getAllMesg().add(insertAllIx + 1, newSummary);
        } else {
            fitFile.getAllMesg().add(newSummary);
        }

        fitFile.appendTempUpdateLogLn("-- Created SPLIT_SUMMARY row for split type " + splitType + ".");
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Finds the last index of a SPLIT_SUMMARY message in the ALL messages list, so that new SPLIT_SUMMARY messages can be inserted after it.
    // Returns -1 if no SPLIT_SUMMARY messages are found in the ALL messages list.
    private int findLastSplitSummaryAllMesgIndex() {
        int lastIx = -1;
        for (int i = 0; i < fitFile.getAllMesg().size(); i++) {
            if (fitFile.getAllMesg().get(i).getNum() == MesgNum.SPLIT_SUMMARY) {
                lastIx = i;
            }
        }
        return lastIx;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Renumbers the SPLSUM_MESGIX field in all SPLIT_SUMMARY messages to ensure they are sequential and consistent after any changes to the SPLIT list.
    private void renumberSplitSummaryMesgIndexes() {
        int ix = 0;
        for (Mesg summary : fitFile.getSplitSummaryMesg()) {
            summary.setFieldValue(FitFile.SPLSUM_MESGIX, ix);
            ix++;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Aggregates the totals for a specific SPLIT type, used when creating or updating SPLIT_SUMMARY messages.
    // This class accumulates the total timer, moving timer, distance, max speed, vertical speed, ascent, descent, calories, and split count for a given SPLIT type.
    private static final class SplitSummaryAgg {
        float totalTimer = 0f;
        float totalMovingTimer = 0f;
        float totalDist = 0f;
        float maxSpeed = 0f;
        float weightedVertSpeed = 0f;
        float vertWeight = 0f;
        float weightedAvgHr = 0f;
        float hrWeight = 0f;
        int avgHr = 0;
        int maxHr = Integer.MIN_VALUE;
        float weightedAvgCadence = 0f;
        float cadenceWeight = 0f;
        float avgCadence = 0f;
        float maxCadence = 0f;
        int totalAscent = 0;
        int totalDescent = 0;
        int totalCalories = 0;
        int splitCount = 0;

        void addSplitToSummary(Mesg split) {
            Float timer = split.getFieldFloatValue(FitFile.SPL_TIMER);
            Float movingTimer = split.getFieldFloatValue(FitFile.SPL_MTIMER);
            Float dist = split.getFieldFloatValue(FitFile.SPL_DIST);
            Float maxSpd = split.getFieldFloatValue(FitFile.SPL_MSPEED);
            Float vertSpd = split.getFieldFloatValue(FitFile.SPL_VSPEED);
            Integer splitAvgHr = split.getFieldIntegerValue(FitFile.SPL_HR);
            Integer maxHr = split.getFieldIntegerValue(FitFile.SPL_MHR);
            Float splitAvgCadence = split.getFieldFloatValue(FitFile.SPL_CAD);
            Float splitMaxCadence = split.getFieldFloatValue(FitFile.SPL_MCAD);
            Integer ascent = split.getFieldIntegerValue(FitFile.SPL_ASC);
            Integer descent = split.getFieldIntegerValue(FitFile.SPL_DESC);
            Integer calories = split.getFieldIntegerValue(FitFile.SPL_CAL);

            float timerVal = timer != null ? timer : 0f;
            float movingTimerVal = movingTimer != null ? movingTimer : 0f;
            float distVal = dist != null ? dist : 0f;
            float maxSpdVal = maxSpd != null ? maxSpd : 0f;
            float averageWeight = timerVal > 0f ? timerVal : 1f;

            splitCount++;
            totalTimer += timerVal;
            totalMovingTimer += movingTimerVal;
            totalDist += distVal;
            if (maxSpdVal > maxSpeed) {
                maxSpeed = maxSpdVal;
            }

            if (vertSpd != null && timerVal > 0f) {
                weightedVertSpeed += vertSpd * timerVal;
                vertWeight += timerVal;
            }
            if (maxHr != null && maxHr > this.maxHr) {
                this.maxHr = maxHr;
            }
            if (splitAvgHr != null) {
                weightedAvgHr += splitAvgHr * averageWeight;
                hrWeight += averageWeight;
            }
            if (splitMaxCadence != null && splitMaxCadence > this.maxCadence) {
                this.maxCadence = splitMaxCadence;
            }
            if (splitAvgCadence != null) {
                weightedAvgCadence += splitAvgCadence * averageWeight;
                cadenceWeight += averageWeight;
            }

            totalAscent += ascent != null ? ascent : 0;
            totalDescent += descent != null ? descent : 0;
            totalCalories += calories != null ? calories : 0;

            avgHr = hrWeight > 0f ? Math.round(weightedAvgHr / hrWeight) : 0;
            avgCadence = cadenceWeight > 0f ? weightedAvgCadence / cadenceWeight : 0f;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Finds the index of the SPLIT message that represents the entire activity overview, if any.
    private int findOverviewSplitIndex() {
        if (fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty()) {
            return -1;
        }

        for (int splitIx = 0; splitIx < fitFile.getSplitMesg().size(); splitIx++) {
            Mesg split = fitFile.getSplitMesg().get(splitIx);
            if (split == null) {
                continue;
            }

            Float splitTimer = split.getFieldFloatValue(FitFile.SPL_TIMER);
            Float splitDist = split.getFieldFloatValue(FitFile.SPL_DIST);
            Long splitStartTime = split.getFieldLongValue(FitFile.SPL_STIME);
            Long splitEndTime = split.getFieldLongValue(FitFile.SPL_ETIME);

            boolean matchesTimer = splitTimer != null && fitFile.getTotalTimerTime() != null
                && Math.abs(splitTimer - fitFile.getTotalTimerTime()) <= SPLIT_TIMER_MATCH_TOLERANCE_SEC;
            boolean matchesDist = splitDist != null && fitFile.getTotalDistance() != null
                && Math.abs(splitDist - fitFile.getTotalDistance()) <= 10f;
            boolean matchesStart = splitStartTime != null && fitFile.getTimeFirstRecord() != null
                && Math.abs(splitStartTime - fitFile.getTimeFirstRecord()) <= 2L;
            boolean matchesEnd = splitEndTime != null && fitFile.getTimeLastRecord() != null
                && Math.abs(splitEndTime - fitFile.getTimeLastRecord()) <= 2L;

            if ((matchesTimer && matchesDist) || (matchesStart && matchesEnd)) {
                return splitIx;
            }
        }

        return -1;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Splits a lap into two at the specified total timer value. The first lap retains the original start time, while the second lap starts at the split time.
    // The lap values (timer, distance, etc.) are recalculated for both laps based on the record messages.
    // The split must occur within the bounds of an existing lap, and the total timer value must correspond to a record message.
    public void lapNew(Long totalTimer) {

        if (totalTimer == null) {
            fitFile.printAndAppendUpdateLogLn("==XX> No timer value provided.");
            return;
        }
        if (fitFile.getRecordMesgAddOnRecords() == null || fitFile.getRecordMesgAddOnRecords().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("==XX> Timer list is empty. Run createTimerList() first.");
            return;
        }
        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("==XX> No lap messages found.");
            return;
        }

        // Find the record index corresponding to the provided total timer value.
        // ------------------------------------------------
        int splitRecordIx = findFirstRecordIndexAtOrAfterTimer(totalTimer);
        if (splitRecordIx <= 0 || splitRecordIx >= fitFile.getRecordMesg().size()) {
            fitFile.printAndAppendUpdateLogLn("==XX> Timer cannot be used for lap split (outside record range): "
                + PehoUtils.sec2minSecLong(totalTimer));
            return;
        }

        Mesg splitRecord = fitFile.getRecordMesg().get(splitRecordIx);
        Mesg prevRecord = fitFile.getRecordMesg().get(splitRecordIx - 1);

        Long splitTime = splitRecord.getFieldLongValue(FitFile.REC_TIME);
        Long prevTime = prevRecord.getFieldLongValue(FitFile.REC_TIME);

        // Get the timer value from the add-on record for the split record index.
        Long splitTimer = fitFile.getRecordMesgAddOnRecords().get(splitRecordIx).getTimer();

        if (splitTime == null || prevTime == null || splitTimer == null) {
            fitFile.printAndAppendUpdateLogLn("==XX> Could not resolve split record timing values.");
            return;
        }

        // Find the lap index that contains the split time. The split must occur within an existing lap.
        int lapIx = findLapIndexForTime(splitTime);
        if (lapIx < 0 || lapIx >= fitFile.getLapMesg().size()) {
            fitFile.printAndAppendUpdateLogLn("==XX> Could not find lap for timer " + PehoUtils.sec2minSecLong(totalTimer));
            return;
        }

        // Creating a new lap message based on the first lap message
        Mesg firstLap = fitFile.getLapMesg().get(lapIx);

        // Analyze the split match for the single lap to determine how to split the lap values.
        SplitMatch splitToSplit = analyzeSplitMatchForSingleLap(lapIx, firstLap, "LAP NEW");
        Set<Short> affectedSplitTypes = new HashSet<>();
        Short splitType = splitToSplit.splitMesg.getFieldShortValue(FitFile.SPL_TYPE);
        if (splitType != null) {
            affectedSplitTypes.add(splitType);
        }

        // Create a new lap message for the second lap, copying the first lap's values.
        Mesg secondLap = new Mesg(firstLap);

        Long originalLapStartTime = firstLap.getFieldLongValue(FitFile.LAP_STIME);
        Float originalLapTTimer = firstLap.getFieldFloatValue(FitFile.LAP_TIMER);
        Float originalLapETimer = firstLap.getFieldFloatValue(FitFile.LAP_ETIMER);
        Integer originalLapStartLat = firstLap.getFieldIntegerValue(FitFile.LAP_SLAT);
        Integer originalLapStartLon = firstLap.getFieldIntegerValue(FitFile.LAP_SLON);

        // Get first lap start time to find the record range for the lap
        Long lapStartTime = firstLap.getFieldLongValue(FitFile.LAP_STIME);
        if (lapStartTime == null) {
            fitFile.printAndAppendUpdateLogLn("==XX> Lap has no start time. Cannot split lap " + (lapIx + 1));
            return;
        }

        // Set the start time of the second lap to the split time
        setLongIfNotNull(secondLap, FitFile.LAP_STIME, splitTime);

        int lapAllMesgIx = findLapMesgIndexInAllMesgByLapIx(lapIx);
        if (lapAllMesgIx < 0) {
            fitFile.printAndAppendUpdateLogLn("==XX> Could not find LAP in allMesg for lap ix: " + lapIx);
            return;
        }

        int linkedTizIx = findLinkedTimeInZoneMesgIndex(lapAllMesgIx, lapIx);
        Mesg insertedTimeInZone = null;
        Mesg sourceTimeInZone = linkedTizIx >= 0 ? fitFile.getAllMesg().get(linkedTizIx) : null;
        int insertAllMesgIx = lapAllMesgIx + 1;
        if (linkedTizIx == lapAllMesgIx + 1) {
            insertAllMesgIx = lapAllMesgIx + 2;
        }

        setIntIfNotNull(secondLap, FitFile.LAP_IX, lapIx + 1);

        // Insert the new second lap into the lap list and the all messages list.
        fitFile.getLapMesg().add(lapIx + 1, secondLap);
        fitFile.getAllMesg().add(insertAllMesgIx, secondLap);

        // Update the total number of laps in the fit file and the session message.
        fitFile.setNumberOfLaps(fitFile.getNumberOfLaps() + 1);
        if (!fitFile.getSessionMesg().isEmpty()) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_LAPS, fitFile.getNumberOfLaps());
        }

        // If there was a linked TIME_IN_ZONE message for the original lap, create a new TIME_IN_ZONE message for the second lap and insert it into the all messages list.
        if (sourceTimeInZone != null) {
            insertedTimeInZone = new Mesg(sourceTimeInZone);
            insertedTimeInZone.setFieldValue(FitFile.TIZ_REF_MESG, MesgNum.LAP);
            insertedTimeInZone.setFieldValue(FitFile.TIZ_REF_IX, lapIx + 1);
            fitFile.getAllMesg().add(insertAllMesgIx + 1, insertedTimeInZone);
        }

        fitFile.fillLapExtraRecords();

        // Recalculate the lap values for both the first and second laps based on the record messages in their respective ranges.
        // The first lap will cover the records from the original start to just before the split record.
        // The second lap will cover the records from the split record to the original end.
        LapBoundaryValues firstLapBoundary = recalculateLapValuesFromRecords(lapIx);
        LapBoundaryValues secondLapBoundary = recalculateLapValuesFromRecords(lapIx + 1);

        // Keep the original lap start fields intact for the first split segment.
        setLongIfNotNull(firstLap, FitFile.LAP_STIME, originalLapStartTime);

        Float firstLapTTimer = firstLap.getFieldFloatValue(FitFile.LAP_TIMER);
        Float secondLapTTimer = secondLap.getFieldFloatValue(FitFile.LAP_TIMER);
        Float diffLapTTimer = originalLapTTimer - secondLapTTimer - firstLapTTimer;
        fitFile.printAndAppendUpdateLogLn("lapNew: originalLapTTimer=" + originalLapTTimer
            + ", firstLapTTimer=" + firstLapTTimer
            + ", secondLapTTimer=" + secondLapTTimer
            + ", diffLapTTimer=" + diffLapTTimer);
        setFloatIfNotNull(firstLap, FitFile.LAP_TIMER, firstLapTTimer + diffLapTTimer/2f);
        setFloatIfNotNull(secondLap, FitFile.LAP_TIMER, secondLapTTimer + diffLapTTimer/2f);
        fitFile.printAndAppendUpdateLogLn("lapNew: adjusted firstLapTTimer=" + firstLap.getFieldFloatValue(FitFile.LAP_TIMER)
            + ", adjusted secondLapTTimer=" + secondLap.getFieldFloatValue(FitFile.LAP_TIMER));

        Float firstLapETimer = firstLap.getFieldFloatValue(FitFile.LAP_ETIMER);
        Float secondLapETimer = secondLap.getFieldFloatValue(FitFile.LAP_ETIMER);
        Float diffLapETimer = originalLapETimer - secondLapETimer - firstLapETimer;
        fitFile.printAndAppendUpdateLogLn("lapNew: originalLapETimer=" + originalLapETimer
            + ", firstLapETimer=" + firstLapETimer
            + ", secondLapETimer=" + secondLapETimer
            + ", diffLapETimer=" + diffLapETimer);
        setFloatIfNotNull(firstLap, FitFile.LAP_ETIMER, firstLapETimer + diffLapETimer/2f);
        setFloatIfNotNull(secondLap, FitFile.LAP_ETIMER, secondLapETimer + diffLapETimer/2f);
        fitFile.printAndAppendUpdateLogLn("lapNew: adjusted firstLapETimer=" + firstLap.getFieldFloatValue(FitFile.LAP_ETIMER)
            + ", adjusted secondLapETimer=" + secondLap.getFieldFloatValue(FitFile.LAP_ETIMER));

        setIntIfNotNull(firstLap, FitFile.LAP_SLAT, originalLapStartLat);
        setIntIfNotNull(firstLap, FitFile.LAP_SLON, originalLapStartLon);

        // Split the matched split message for the lap into two, creating a new split message for the second lap.
        Mesg insertedSplit = splitMatchedSplitForLapNew(splitToSplit, lapIx, firstLap, secondLap);
        renumberSplitMesgIndexes();

        // Update the references in the TIME_IN_ZONE messages and other related messages to account for the newly inserted lap.
        incrementLapReferencesAfterInsertedLap(lapIx, secondLap, insertedTimeInZone, insertedSplit);

        // Synchronize the split messages with the lap messages after the lap change.
        syncSplitsFromLapsAfterLapChange("lapNew", lapIx, lapIx + 1);
        updateSplitSummaryFromSplitsForTypes(affectedSplitTypes);

        fitFile.printAndAppendUpdateLogLn("Split lap " + (lapIx + 1)
            + " at totalTimer=" + PehoUtils.sec2minSecLong(totalTimer)
            + " (recordIx=" + splitRecordIx + ")");
        fitFile.printAndAppendUpdateLogLn("-- Lap " + (lapIx + 1)
            + " new timer: " + new TimeStr(firstLapBoundary.totalTimer).get()
            + ", new lap " + (lapIx + 2)
            + " timer: " + new TimeStr(secondLapBoundary.totalTimer).get());
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void renumberSplitMesgIndexes() {
        if (fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty()) {
            return;
        }

        int splitIx = 0;
        for (Mesg split : fitFile.getSplitMesg()) {
            split.setFieldValue(FitFile.SPL_MESGIX, splitIx);
            splitIx++;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private int findLapMesgIndexInAllMesgByLapIx(int lapIx) {
        return FindMesgIx.findMesgIndexByIntField(fitFile.getAllMesg(), MesgNum.LAP, FitFile.LAP_IX, lapIx);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private List<SplitMatch> analyzeSplitMatchesForLapRange(int fromLapIx, int toLapIx) {
        List<SplitMatch> matches = new ArrayList<>();
        Set<Integer> usedSplitIndexes = new HashSet<>();

        // Log the range of laps being analyzed for split matches.
        fitFile.printAndAppendUpdateLogLn("-- Split analyze for lap range " + (fromLapIx + 1) + "-" + (toLapIx + 1));

        // Iterate through the specified range of laps and find the best matching split for each lap.
        for (int lapIx = fromLapIx; lapIx <= toLapIx; lapIx++) {
            Mesg lap = fitFile.getLapMesg().get(lapIx);

            // Find the best matching SPLIT for this lap, if any, and log the result.
            SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);
            if (match != null) {
                usedSplitIndexes.add(match.splitListIndex);
                matches.add(match);
                fitFile.printAndAppendUpdateLogLn("-- Split match LAP " + (lapIx + 1)
                    + " -> SPLIT " + (match.splitListIndex + 1)
                    + " by " + match.matchReason
                    + " (splitTimer=" + formatSec(match.splitTimer)
                    + ", lapTimer=" + formatSec(match.lapTimer) + ")");
            } else {
                Float lapTimer = lap.getFieldFloatValue(FitFile.LAP_TIMER);
                fitFile.printAndAppendUpdateLogLn("-- No split match for LAP " + (lapIx + 1)
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
            fitFile.printAndAppendUpdateLogLn("-- Split analyze [" + context + "] LAP " + (lapIx + 1)
                + " -> SPLIT " + (match.splitListIndex + 1)
                + " by " + match.matchReason
                + " (splitTimer=" + formatSec(match.splitTimer)
                + ", lapTimer=" + formatSec(match.lapTimer) + ")");
        } else {
            fitFile.printAndAppendUpdateLogLn("-- Split analyze [" + context + "] LAP " + (lapIx + 1)
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
            Integer splitLapIx = split.getFieldIntegerValue(FitFile.SPL_LAPIX);
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
            fitFile.printAndAppendUpdateLogLn("-- WARNING: Multiple SPL_TIMER matches for LAP " + (lapIx + 1)
                + "; selecting SPLIT " + (best.splitListIndex + 1) + " with closest timer diff.");
        }
        return best;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void mergeMatchedSplitsForLapMerge(List<SplitMatch> matches, int fromLapIx, int toLapIx, Mesg mergedLap) {
        if (matches == null || matches.isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("-- No matching SPLIT records found for merge range.");
            return;
        }

        matches.sort(Comparator.comparingInt(m -> m.lapIx));

        SplitMatch keeper = null;
        for (SplitMatch match : matches) {
            if (match.lapIx == fromLapIx) {
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

        applyLapMetricsToSplit(fromLapIx, keeper.splitMesg);
        setIntIfNotNull(keeper.splitMesg, FitFile.SPL_LAPIX, fromLapIx);

        fitFile.printAndAppendUpdateLogLn("-- SPLIT merge result: kept SPLIT " + (keeper.splitListIndex + 1)
            + ", removed " + removedSplits + " split(s), tied to LAP " + (fromLapIx + 1));

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

        applyLapMetricsToSplit(lapIx, firstSplit);
        applyLapMetricsToSplit(lapIx + 1, secondSplit);

        setIntIfNotNull(firstSplit, FitFile.SPL_LAPIX, lapIx);
        setIntIfNotNull(secondSplit, FitFile.SPL_LAPIX, lapIx + 1);

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

        fitFile.printAndAppendUpdateLogLn("-- SPLIT update: split SPLIT " + (splitToSplit.splitListIndex + 1)
            + " into two splits for LAP " + (lapIx + 1) + " and LAP " + (lapIx + 2));
        return secondSplit;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void applyLapMetricsToSplit(Integer lapIx, Mesg splitMesg) {
        if (splitMesg == null || lapIx == null) {
            return;
        }
        Mesg lapMesg = fitFile.getLapMesg().get(lapIx);
        LapExtraMesg lapExtra = fitFile.getLapExtraRecords().get(lapIx);

        Long lapStartTime = lapMesg.getFieldLongValue(FitFile.LAP_STIME);
        setLongIfNotNull(splitMesg, FitFile.SPL_STIME, lapStartTime);
        Long splitEndTime = lapExtra.getTimeEnd();
        setLongIfNotNull(splitMesg, FitFile.SPL_ETIME, splitEndTime);

        Float lapTimer = lapMesg.getFieldFloatValue(FitFile.LAP_TIMER);
        setFloatIfNotNull(splitMesg, FitFile.SPL_TIMER, lapTimer);
        Float lapElapsed = lapMesg.getFieldFloatValue(FitFile.LAP_ETIMER);
        setFloatIfNotNull(splitMesg, FitFile.SPL_ETIMER, lapElapsed);
        Float lapMoving = lapMesg.getFieldFloatValue(FitFile.LAP_MTIMER);
        setFloatIfNotNull(splitMesg, FitFile.SPL_MTIMER, lapMoving);

        Float lapDist = lapMesg.getFieldFloatValue(FitFile.LAP_DIST);
        setFloatIfNotNull(splitMesg, FitFile.SPL_DIST, lapDist);
        Float lapStartDist = lapExtra.getDistStartCalc();
        setFloatIfNotNull(splitMesg, FitFile.SPL_SDIST, lapStartDist * 100f);

        Float lapSpeed = lapMesg.getFieldFloatValue(FitFile.LAP_ESPEED);
        Float lapMaxSpeed = lapMesg.getFieldFloatValue(FitFile.LAP_EMSPEED);
        if (lapSpeed == null) {
            lapSpeed = lapMesg.getFieldFloatValue(FitFile.LAP_SPEED);
            lapMaxSpeed = lapMesg.getFieldFloatValue(FitFile.LAP_MSPEED);
            if (lapSpeed == null) {
                lapSpeed = lapExtra.getSpeedAvg();
                lapMaxSpeed = lapExtra.getSpeedMax();
            }
        }
        setFloatIfNotNull(splitMesg, FitFile.SPL_SPEED, lapSpeed);
        setFloatIfNotNull(splitMesg, FitFile.SPL_MSPEED, lapMaxSpeed);

        setIntIfNotNull(splitMesg, FitFile.SPL_HR, lapMesg.getFieldIntegerValue(FitFile.LAP_HR));
        setIntIfNotNull(splitMesg, FitFile.SPL_MHR, lapMesg.getFieldIntegerValue(FitFile.LAP_MHR));
        setFloatIfNotNull(splitMesg, FitFile.SPL_CAD, lapMesg.getFieldShortValue(FitFile.LAP_CAD) * 256/2f);
        setFloatIfNotNull(splitMesg, FitFile.SPL_MCAD, lapMesg.getFieldShortValue(FitFile.LAP_MCAD) * 256/2f);
        setIntIfNotNull(splitMesg, FitFile.SPL_POW, lapMesg.getFieldIntegerValue(FitFile.LAP_POW));
        setIntIfNotNull(splitMesg, FitFile.SPL_MPOW, lapMesg.getFieldIntegerValue(FitFile.LAP_MPOW));
        setFloatIfNotNull(splitMesg, FitFile.SPL_STEP, lapMesg.getFieldFloatValue(FitFile.LAP_STEP));

        setFloatIfNotNull(splitMesg, FitFile.SPL_SELE, fitFile.getLapExtraRecords().get(lapIx).getAltStart());
        setIntIfNotNull(splitMesg, FitFile.SPL_ASC, lapMesg.getFieldIntegerValue(FitFile.LAP_ASC));
        setIntIfNotNull(splitMesg, FitFile.SPL_DESC, lapMesg.getFieldIntegerValue(FitFile.LAP_DESC));
        setIntIfNotNull(splitMesg, FitFile.SPL_TEMP, lapMesg.getFieldIntegerValue(FitFile.LAP_TEMP));
        setIntIfNotNull(splitMesg, FitFile.SPL_MAXTEMP, lapMesg.getFieldIntegerValue(FitFile.LAP_MAXTEMP));
        setIntIfNotNull(splitMesg, FitFile.SPL_MINTEMP, lapMesg.getFieldIntegerValue(FitFile.LAP_MINTEMP));

        setIntIfNotNull(splitMesg, FitFile.SPL_SLAT, lapMesg.getFieldIntegerValue(FitFile.LAP_SLAT));
        setIntIfNotNull(splitMesg, FitFile.SPL_SLON, lapMesg.getFieldIntegerValue(FitFile.LAP_SLON));
        setIntIfNotNull(splitMesg, FitFile.SPL_ELAT, lapMesg.getFieldIntegerValue(FitFile.LAP_ELAT));
        setIntIfNotNull(splitMesg, FitFile.SPL_ELON, lapMesg.getFieldIntegerValue(FitFile.LAP_ELON));

        // This split now represents exactly one lap; undocumented field 68 (num laps combined) must reflect that.
        setIntIfNotNull(splitMesg, FitFile.SPL_LAPS, 1);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Synchronizes the SPLIT messages with the LAP messages after a lap change (e.g., after a lap split or merge).
    private void syncSplitsFromLapsAfterLapChange(String context, int updateFromLapIx, int updateToLapIx) {
        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("-- Split sync skipped (no laps) [" + context + "]");
            return;
        }
        if (fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty()) {
            fitFile.printAndAppendUpdateLogLn("-- Split sync skipped (no splits) [" + context + "]");
            return;
        }

        int synced = 0;
        int noMatch = 0;
        Set<Integer> usedSplitIndexes = new HashSet<>();

        for (int lapIx = 0; lapIx < fitFile.getLapMesg().size(); lapIx++) {
            Mesg lap = fitFile.getLapMesg().get(lapIx);
            System.out.println("syncSplitsFromLapsAfterLapChange: lapIx in loop=" + lapIx
                 + ", lapIx in lap=" + lap.getFieldIntegerValue(FitFile.LAP_IX));
            
            SplitMatch match = findBestSplitMatchForLap(lapIx, lap, usedSplitIndexes);

            System.out.println("syncSplitsFromLapsAfterLapChange: lapIx=" + lapIx
                + ", match=" + (match != null ? "SPLIT " + (match.splitListIndex + 1) : "null"));
            if (match == null) {
                noMatch++;
                continue;
            }

            // Mark this split index as used to avoid matching it with another lap.
            usedSplitIndexes.add(match.splitListIndex);

            // Update the SPLIT message to reflect the metrics of the corresponding LAP message.
            if (lapIx >= updateFromLapIx && lapIx <= updateToLapIx) {
                applyLapMetricsToSplit(lapIx, match.splitMesg);
                fitFile.printAndAppendUpdateLogLn("-- Split sync [" + context + "] LAP " + (lapIx + 1)
                    + " -> SPLIT " + (match.splitListIndex + 1)
                    + " by " + match.matchReason
                    + " (splitTimer=" + formatSec(match.splitTimer)
                    + ", lapTimer=" + formatSec(match.lapTimer) + ")");
            }   

            // Ensure the SPL_LAPIX field in the split message correctly references the lap index.
            setIntIfNotNull(match.splitMesg, FitFile.SPL_LAPIX, lapIx);
            synced++;
        }

        fitFile.printAndAppendUpdateLogLn("-- Split sync complete [" + context + "]: synced="
            + synced + ", lapsWithoutSplit=" + noMatch);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Float findLapStartDistanceMeters(int lapIx, Long lapStartTime) {
        int startRecordIx = findRecordIndexAtOrAfterTime(lapStartTime) - 1;
        System.out.println("findLapStartDistanceMeters: lapIx=" + lapIx + ", lapStartTime=" + new Tstr(lapStartTime).get()
            + ", startRecordIx=" + startRecordIx);
        if (startRecordIx < 0) {
            return 0f;
        }
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
        return splitStartTime + Math.round(totalElapsedSeconds - 0.5f);
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
    private String formatSplitType(Mesg splitMesg) {
        if (splitMesg == null) {
            return "-";
        }

        Short splitType = splitMesg.getFieldShortValue(FitFile.SPL_TYPE);
        if (splitType == null) {
            return "-";
        }

        com.garmin.fit.SplitType splitTypeEnum = com.garmin.fit.SplitType.getByValue(splitType);
        if (splitTypeEnum == null) {
            return "unknown(" + splitType + ")";
        }
        return splitTypeEnum + "(" + splitType + ")";
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
    // Garmin sometimes merges 2+ consecutive laps into a single SPLIT (e.g. when it
    // considers them the same split type). Recognized primarily by the undocumented
    // SPL_NUM_LAPS (field 68): when > 1, SPL_LAPIX is the first of those laps. Falls
    // back to summed LAP_TIMER matching when SPL_NUM_LAPS is absent/unreliable.
    private void detectAndFixCombinedSplits() {

        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()
            || fitFile.getSplitMesg() == null || fitFile.getSplitMesg().isEmpty()) {
            return;
        }

        int totalFixed = 0;
        Set<Short> affectedSplitTypes = new HashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int splitIx = 0; splitIx < fitFile.getSplitMesg().size(); splitIx++) {
                if (totalFixed == 0) {
                    fitFile.printAndAppendUpdateLogLn("--------------------------------------------------");
                    fitFile.printAndAppendUpdateLogLn("COMBINED SPLIT DETECTION (Garmin merged laps into one split)");
                    fitFile.printAndAppendUpdateLogLn("--------------------------------------------------");
                }
                Mesg split = fitFile.getSplitMesg().get(splitIx);
                Integer splitLapIx = split.getFieldIntegerValue(FitFile.SPL_LAPIX);
                if (splitLapIx == null || splitLapIx < 0 || splitLapIx >= fitFile.getLapMesg().size()) {
                    continue;
                }
                Float splitTimer = split.getFieldFloatValue(FitFile.SPL_TIMER);

                Integer declaredNumLaps = split.getFieldIntegerValue(FitFile.SPL_LAPS);
                int combinedLapCount;
                String detectedBy;
                if (declaredNumLaps != null && declaredNumLaps >= 2
                    && splitLapIx + declaredNumLaps <= fitFile.getLapMesg().size()) {
                    combinedLapCount = declaredNumLaps;
                    detectedBy = "SPL_NUM_LAPS";
                } else {
                    combinedLapCount = detectCombinedLapCountForSplit(splitLapIx, splitTimer);
                    detectedBy = "summed LAP_TIMER";
                }
                if (combinedLapCount < 2) {
                    continue;
                }

                fitFile.printAndAppendUpdateLogLn("-- SPLIT " + (splitIx + 1) + " (lapIx=" + (splitLapIx + 1)
                    + ", timer=" + formatSec(splitTimer) + ") combines " + combinedLapCount
                    + " laps (LAP " + (splitLapIx + 1) + "-" + (splitLapIx + combinedLapCount) + ")"
                    + " [detected by " + detectedBy + "]");
                Short splitType = split.getFieldShortValue(FitFile.SPL_TYPE);
                if (splitType != null) {
                    affectedSplitTypes.add(splitType);
                }
                splitCombinedSplitAcrossLaps(split, splitIx, splitLapIx, combinedLapCount);
                renumberSplitMesgIndexes();
                totalFixed++;
                changed = true;
                break; // split list mutated; restart the scan
            }
        }

        if (totalFixed > 0) {
            fitFile.printAndAppendUpdateLogLn("-- Combined-split detection: fixed " + totalFixed + " split(s).");
            // Splitting a combined split increases how many splits share its type, so
            // SPLSUM_SPLITS (and other aggregated SPLIT_SUMMARY fields) must be recalculated.
            updateSplitSummaryFromSplitsForTypes(affectedSplitTypes);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Returns the number of consecutive laps (>=2) starting at firstLapIx whose summed
    // LAP_TIMER matches splitTimer, or 0 if no such combination is found.
    private int detectCombinedLapCountForSplit(int firstLapIx, Float splitTimer) {
        if (splitTimer == null || firstLapIx < 0 || firstLapIx >= fitFile.getLapMesg().size()) {
            return 0;
        }

        float sum = 0f;
        int lapIx = firstLapIx;
        int lapCount = 0;
        while (lapIx < fitFile.getLapMesg().size()) {
            if (lapCount > 0 && hasSplitWithLapIx(lapIx)) {
                // A later lap in the sequence already has its own dedicated split.
                return 0;
            }
            Float lapTimer = fitFile.getLapMesg().get(lapIx).getFieldFloatValue(FitFile.LAP_TIMER);
            if (lapTimer == null) {
                return 0;
            }
            sum += lapTimer;
            lapCount++;

            if (Math.abs(sum - splitTimer) <= SPLIT_TIMER_MATCH_TOLERANCE_SEC) {
                return lapCount >= 2 ? lapCount : 0;
            }
            if (sum > splitTimer + SPLIT_TIMER_MATCH_TOLERANCE_SEC) {
                return 0;
            }
            lapIx++;
        }
        return 0;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private boolean hasSplitWithLapIx(int lapIx) {
        for (Mesg split : fitFile.getSplitMesg()) {
            Integer splitLapIx = split.getFieldIntegerValue(FitFile.SPL_LAPIX);
            if (splitLapIx != null && splitLapIx == lapIx) {
                return true;
            }
        }
        return false;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Splits one combined SPLIT mesg into lapCount SPLIT messages (one per lap), each
    // inheriting its data from the corresponding lap.
    private void splitCombinedSplitAcrossLaps(Mesg originalSplit, int originalSplitListIndex, int firstLapIx, int lapCount) {
        int splitMesgIx = fitFile.getSplitMesg().indexOf(originalSplit);
        int allMesgIx = fitFile.getAllMesg().indexOf(originalSplit);

        Mesg firstLap = fitFile.getLapMesg().get(firstLapIx);
        applyLapMetricsToSplit(firstLapIx, originalSplit);
        setIntIfNotNull(originalSplit, FitFile.SPL_LAPIX, firstLapIx);

        int insertSplitIx = splitMesgIx + 1;
        int insertAllIx = allMesgIx + 1;
        for (int k = 1; k < lapCount; k++) {
            int lapIx = firstLapIx + k;
            Mesg lap = fitFile.getLapMesg().get(lapIx);
            Mesg newSplit = new Mesg(originalSplit);
            applyLapMetricsToSplit(lapIx, newSplit);
            setIntIfNotNull(newSplit, FitFile.SPL_LAPIX, lapIx);

            fitFile.getSplitMesg().add(insertSplitIx, newSplit);
            if (insertAllIx <= fitFile.getAllMesg().size()) {
                fitFile.getAllMesg().add(insertAllIx, newSplit);
            } else {
                fitFile.getAllMesg().add(newSplit);
            }
            renumberSplitMesgIndexes();
            insertSplitIx++;
            insertAllIx++;
        }

        fitFile.printAndAppendUpdateLogLn("-- SPLIT " + (originalSplitListIndex + 1)
            + " split into " + lapCount + " SPLIT messages for LAP " + (firstLapIx + 1)
            + "-" + (firstLapIx + lapCount) + ".");

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
    // Returns the index of the first record whose timer is >= totalTimer, or the last record index if none found.
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
            return new LapBoundaryValues(0f, 0f, fitFile.getTimeFirstRecord(), fitFile.getTimeFirstRecord());
        }

        int prevLapEndRecordIx = findLapRecordEndIndex(lapIx - 1);
        if (prevLapEndRecordIx < 0 || prevLapEndRecordIx >= fitFile.getRecordMesg().size()) {
            return new LapBoundaryValues(0f, 0f, fitFile.getTimeFirstRecord(), fitFile.getTimeFirstRecord());
        }

        Mesg prevLapEndRecord = fitFile.getRecordMesg().get(prevLapEndRecordIx);
        Float previousDistance = prevLapEndRecord.getFieldFloatValue(FitFile.REC_DIST);
        Long previousTimer = fitFile.getRecordMesgAddOnRecords().get(prevLapEndRecordIx).getTimer();
        Long previousRecordTime = prevLapEndRecord.getFieldLongValue(FitFile.REC_TIME);

        return new LapBoundaryValues(
            previousDistance != null ? previousDistance : 0f,
            previousTimer != null ? previousTimer : 0f,
            previousRecordTime,
            previousRecordTime);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private LapBoundaryValues recalculateLapValuesFromRecords(int lapIx) {
        
        Mesg lapMesg = fitFile.getLapMesg().get(lapIx);
        LapExtraMesg lapExtra = fitFile.getLapExtraRecords().get(lapIx);

        // TIME
        lapMesg.setFieldValue(FitFile.LAP_TIME, fitFile.getActivityDateTimeUTC()); // LAP_TIME is allways the start time of activity
        Float lapTTimer = lapExtra.getTTimerLap() != null ? lapExtra.getTTimerLap() : 0f;
        lapMesg.setFieldValue(FitFile.LAP_TIMER, lapTTimer);
        Long startTime = lapExtra.getTimeStart() != null ? lapExtra.getTimeStart() : fitFile.getActivityDateTimeUTC();
        lapMesg.setFieldValue(FitFile.LAP_STIME, startTime);

        // TIMER
        if (lapExtra.getTTimerLap() != null) lapMesg.setFieldValue(FitFile.LAP_TIMER, lapExtra.getTTimerLap());
        if (lapExtra.getETimerLap() != null) lapMesg.setFieldValue(FitFile.LAP_ETIMER, lapExtra.getETimerLap());
        if (lapExtra.getMTimerLap() != null) lapMesg.setFieldValue(FitFile.LAP_MTIMER, lapExtra.getMTimerLap());

        // DISTANCE
        if (lapExtra.getDistLap() != null) lapMesg.setFieldValue(FitFile.LAP_DIST, lapExtra.getDistLap());

        //SPEED
        Float avgSpeed = lapExtra.getSpeedAvg();
        Float maxAvgSpeed = lapExtra.getSpeedMax();
        if (lapExtra.getSpeedEnhancedUsed() != null && lapExtra.getSpeedEnhancedUsed()) {
            if (avgSpeed != null) lapMesg.setFieldValue(FitFile.LAP_ESPEED, avgSpeed);
            if (maxAvgSpeed != null) lapMesg.setFieldValue(FitFile.LAP_EMSPEED, maxAvgSpeed);
        } else {
            if (avgSpeed != null) lapMesg.setFieldValue(FitFile.LAP_SPEED, avgSpeed);
            if (maxAvgSpeed != null) lapMesg.setFieldValue(FitFile.LAP_ESPEED, maxAvgSpeed);
        }

        // HR, POWER, CADENCE
        if (lapExtra.getHrAvg() != null) lapMesg.setFieldValue(FitFile.LAP_HR, lapExtra.getHrAvg());
        if (lapExtra.getHrMax() != null) lapMesg.setFieldValue(FitFile.LAP_MHR, lapExtra.getHrMax());
        if (lapExtra.getPowerAvg() != null) lapMesg.setFieldValue(FitFile.LAP_POW, lapExtra.getPowerAvg());
        if (lapExtra.getPowerMax() != null) lapMesg.setFieldValue(FitFile.LAP_MPOW, lapExtra.getPowerMax());
        if (lapExtra.getCadAvg() != null) lapMesg.setFieldValue(FitFile.LAP_CAD, lapExtra.getCadAvg());
        if (lapExtra.getCadMax() != null) lapMesg.setFieldValue(FitFile.LAP_MCAD, lapExtra.getCadMax());

        // ALT, COORDS
        if (lapExtra.getAltEnhancedUsed() != null && lapExtra.getAltEnhancedUsed()) {
            if (lapExtra.getAltAvg() != null) lapMesg.setFieldValue(FitFile.LAP_EALT, lapExtra.getAltAvg());
            if (lapExtra.getAltMax() != null) lapMesg.setFieldValue(FitFile.LAP_EMALT, lapExtra.getAltMax());
            if (lapExtra.getAltMin() != null) lapMesg.setFieldValue(FitFile.LAP_EMINALT, lapExtra.getAltMin());
        } else {
            if (lapExtra.getAltAvg() != null) lapMesg.setFieldValue(FitFile.LAP_ALT, lapExtra.getAltAvg());
            if (lapExtra.getAltMax() != null) lapMesg.setFieldValue(FitFile.LAP_MALT, lapExtra.getAltMax());
            if (lapExtra.getAltMin() != null) lapMesg.setFieldValue(FitFile.LAP_MINALT, lapExtra.getAltMin());
        }
        if (lapExtra.getAscent() != null) lapMesg.setFieldValue(FitFile.LAP_ASC, lapExtra.getAscent());
        if (lapExtra.getDescent() != null) lapMesg.setFieldValue(FitFile.LAP_DESC, lapExtra.getDescent());
        if (lapExtra.getLatStart() != null) lapMesg.setFieldValue(FitFile.LAP_SLAT, lapExtra.getLatStart());
        if (lapExtra.getLonStart() != null) lapMesg.setFieldValue(FitFile.LAP_SLON, lapExtra.getLonStart());
        if (lapExtra.getLatEnd() != null) lapMesg.setFieldValue(FitFile.LAP_ELAT, lapExtra.getLatEnd());
        if (lapExtra.getLonEnd() != null) lapMesg.setFieldValue(FitFile.LAP_ELON, lapExtra.getLonEnd());

        if (lapExtra.getTempAvg() != null) lapMesg.setFieldValue(FitFile.LAP_TEMP, lapExtra.getTempAvg());
        if (lapExtra.getTempMax() != null) lapMesg.setFieldValue(FitFile.LAP_MAXTEMP, lapExtra.getTempMax());
        if (lapExtra.getTempMin() != null) lapMesg.setFieldValue(FitFile.LAP_MINTEMP, lapExtra.getTempMin());

        fitFile.appendTempUpdateLogLn("-- Recalculated lap metrics from records ix "
            + lapExtra.getRecordIxStart() + "-" + lapExtra.getRecordIxEnd()
            + ": timer=" + PehoUtils.sec2minSecLong(lapTTimer)
            + ", dist=" + lapExtra.getDistLap() + "m"
            + ", hrAvg=" + lapExtra.getHrAvg() + ", hrMax=" + lapExtra.getHrMax()
            + ", powAvg=" + lapExtra.getPowerAvg() + ", powMax=" + lapExtra.getPowerMax()
            + ", cadAvg=" + lapExtra.getCadAvg() + ", cadMax=" + lapExtra.getCadMax()
            + ", speed=" + PehoUtils.mps2minpkm(avgSpeed));

        Long endTime = lapExtra.getTimeEnd();
        Float endDistance = lapExtra.getDistEnd();
        Float endTimer = lapExtra.getTTimerEnd();

        return new LapBoundaryValues(
                endDistance != null ? endDistance : 0f,
                endTimer != null ? endTimer : 0f,
                endTime,
                startTime,
                lapTTimer);
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
                Integer referenceMesg = mesg.getFieldIntegerValue(FitFile.TIZ_REF_MESG);
                Integer referenceIndex = mesg.getFieldIntegerValue(FitFile.TIZ_REF_IX);
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
                Integer splitLapIx = mesg.getFieldIntegerValue(FitFile.SPL_LAPIX);
                if (splitLapIx != null && splitLapIx > insertedAfterLapIx) {
                    mesg.setFieldValue(FitFile.SPL_LAPIX, splitLapIx + 1);
                }
            }
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void setFloatIfNotNull(Mesg mesg, int fieldNum, Float value) {
        if (value != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void setIntIfNotNull(Mesg mesg, int fieldNum, Integer value) {
        if (value != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void setShortIfNotNull(Mesg mesg, int fieldNum, Short value) {
        if (value != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void setLongIfNotNull(Mesg mesg, int fieldNum, Long value) {
        if (value != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static class LapBoundaryValues {
        private final Float lastDistance;
        private final Float lastTimer;
        private final Long lastRecordTime;
        private final Long startRecordTime;
        private final Float totalTimer;

        private LapBoundaryValues(Float lastDistance, Float lastTimer, Long lastRecordTime, Long startRecordTime) {
            this(lastDistance, lastTimer, lastRecordTime, startRecordTime, null);
        }

        private LapBoundaryValues(Float lastDistance, Float lastTimer, Long lastRecordTime, Long startRecordTime, Float totalTimer) {
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
                Integer referenceMesg = mesg.getFieldIntegerValue(FitFile.TIZ_REF_MESG);
                Integer referenceIndex = mesg.getFieldIntegerValue(FitFile.TIZ_REF_IX);
                if (referenceMesg != null
                    && referenceMesg == MesgNum.LAP
                    && referenceIndex != null
                    && referenceIndex > deletedLapIx) {
                    mesg.setFieldValue(FitFile.TIZ_REF_IX, referenceIndex - 1);
                }
                continue;
            }

            if (mesg.getNum() == MesgNum.SPLIT) {
                Integer splitLapIx = mesg.getFieldIntegerValue(FitFile.SPL_LAPIX);
                if (splitLapIx != null && splitLapIx > deletedLapIx) {
                    mesg.setFieldValue(FitFile.SPL_LAPIX, splitLapIx - 1);
                }
            }
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private boolean isLinkedTimeInZoneMesg(Mesg mesg, int lapIx) {
        if (mesg.getNum() != MesgNum.TIME_IN_ZONE) {
            return false;
        }
        Integer referenceMesg = mesg.getFieldIntegerValue(FitFile.TIZ_REF_MESG);
        Integer referenceIndex = mesg.getFieldIntegerValue(FitFile.TIZ_REF_IX);
        return referenceMesg != null
            && referenceMesg == MesgNum.LAP
            && referenceIndex != null
            && referenceIndex == lapIx;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // private Integer getMesgFieldAsInt(Mesg mesg, int fieldNum) {
    //     Integer intValue = mesg.getFieldIntegerValue(fieldNum);
    //     if (intValue != null) {
    //         return intValue;
    //     }
    //     Short shortValue = mesg.getFieldShortValue(fieldNum);
    //     if (shortValue != null) {
    //         return shortValue.intValue();
    //     }
    //     Long longValue = mesg.getFieldLongValue(fieldNum);
    //     if (longValue != null) {
    //         return longValue.intValue();
    //     }
    //     return null;
    // }
}