package se.peho.fittools.core;

import com.garmin.fit.DateTime;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;
import se.peho.fittools.core.strings.*;

public class LapFix {

    private final FitFile fitFile;

    public LapFix(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    public void lapMerge(int fromLap, int toLap) {

        Long timeStart = 0L;
        Long timeEnd = 0L;
        Float timerSumOfLaps = 0f;
        Float elapsedTimerSumOfLaps = 0f;
        Float movingTimerSumOfLaps = 0f;
        Float distSumOfLaps = 0f;
        Float hrAvgFaktorsumOfLaps = 0f;
        Integer hrMaxOfLaps = 0;
        Float speedAvgFaktorsumOfLaps = 0f;
        Float speedMaxOfLaps = 0f;
        Float enhSpeedAvgFaktorsumOfLaps = 0f;
        Float enhSpeedMaxOfLaps = 0f;
        Float powerAvgFaktorsumOfLaps = 0f;
        Integer powerMaxOfLaps = 0;
        Float cadenceAvgFaktorsumOfLaps = 0f;
        Integer cadenceMaxOfLaps = 0;
        Float ascentSumOfLaps = 0f;
        Float descentSumOfLaps = 0f;
        Float altAvgFaktorsumOfLaps = 0f;
        Float altMaxOfLaps = 0f;
        Float altMinOfLaps = 0f;
        Integer latStart = 0;
        Integer lonStart = 0;
        Integer latEnd = 0;
        Integer lonEnd = 0;
        Float tempAvgFaktorsumOfLaps = 0f;
        Byte tempMaxOfLaps = 0;
        Byte tempMinOfLaps = 0;

        fitFile.clearTempUpdateLog();

        // int tempLapIx = 0;
        // for (Mesg mesg : fitFile.getAllMesg()) {
        //     if (mesg.getNum() == MesgNum.LAP) {
        //         Mesg lap = mesg;
        //         System.out.println("1LapIx: " + tempLapIx + 
        //             ", LapStartTime: " + new Tstr(lap.getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()).get() + 
        //             ", LapTotalTimer: " + new TimeStr(lap.getFieldFloatValue(FitFile.LAP_TIMER)).get() + 
        //             ", LapIntensity: " + lap.getFieldShortValue(FitFile.LAP_INTENSITY));
        //         tempLapIx++;
        //     }
        // }
        

        for (int lapIxCounter = (fromLap-1); lapIxCounter <= (toLap-1); lapIxCounter++) {

            // Summing up values from laps to be merged
            //-----------------------------------------------
            if (lapIxCounter == fromLap-1) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_STIME) != null) {
                    timeStart = fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_STIME);
                }
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLAT) != null) {
                    latStart = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLAT);
                }
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLON) != null) {
                    lonStart = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLON);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_TIMER) != null) {
                timerSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ETIMER) != null) {
                elapsedTimerSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ETIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MTIMER) != null) {
                movingTimerSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MTIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DIST) != null) {
                distSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DIST);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_HR) != null) {
                hrAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_HR)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIME);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR) > hrMaxOfLaps) {
                    hrMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_SPEED) != null) {
                speedAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_SPEED)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED) > speedMaxOfLaps) {
                    speedMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ESPEED) != null) {
                enhSpeedAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ESPEED)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED) > enhSpeedMaxOfLaps) {
                    enhSpeedMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_POW) != null) {
                powerAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_POW)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW) > powerMaxOfLaps) {
                    powerMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_CAD) != null) {
                cadenceAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_CAD)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD) > cadenceMaxOfLaps) {
                    cadenceMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ASC) != null) {
                ascentSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ASC);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DESC) != null) {
                descentSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DESC);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ALT) != null) {
                altAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ALT)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT) > altMaxOfLaps) {
                    altMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT) != null) {
                if ((fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT) < altMinOfLaps) || (altMinOfLaps == 0f)) {
                    altMinOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_TEMP) != null) {
                tempAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_TEMP)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP) > tempMaxOfLaps) {
                    tempMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP) != null) {
                if ((fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP) < tempMinOfLaps) || (tempMinOfLaps == 0)) {
                    tempMinOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP);
                }
            }
        }

        // Getting values from the "toLap - 1" lap, but will not be used
        //-----------------------------------------------
        if (fitFile.getLapMesg().get(toLap-1).getFieldLongValue(FitFile.LAP_TIME) != null) {
            timeEnd = fitFile.getLapMesg().get(toLap-1).getFieldLongValue(FitFile.LAP_TIME);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELAT) != null) {
            latEnd = fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELAT);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELON) != null) {
            lonEnd = fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELON);
        }

        // Setting values in the "toLap - 1" lap
        //-----------------------------------------------
        if (fitFile.getLapMesg().get(toLap-1).getFieldLongValue(FitFile.LAP_STIME) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_STIME, timeStart);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_TIMER) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_TIMER, timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ETIMER) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ETIMER, elapsedTimerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MTIMER) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MTIMER, movingTimerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_DIST) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_DIST, distSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_HR) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_HR, Math.round(hrAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_MHR) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MHR, hrMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_SPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_SPEED, speedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MSPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MSPEED, speedMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ESPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ESPEED, enhSpeedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_EMSPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_EMSPEED, enhSpeedMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_POW) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_POW, Math.round(powerAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_MPOW) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MPOW, powerMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_CAD) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_CAD, Math.round(cadenceAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_MCAD) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MCAD, cadenceMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ASC) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ASC, ascentSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_DESC) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_DESC, descentSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ALT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ALT, altAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MALT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MALT, altMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MINALT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MINALT, altMinOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_TEMP) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_TEMP, Math.round(tempAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldByteValue(FitFile.LAP_MTEMP) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MTEMP, tempMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldByteValue(FitFile.LAP_MINTEMP) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MINTEMP, tempMinOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_SLAT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_SLAT, latStart);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_SLON) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_SLON, lonStart);
        }

        fitFile.appendTempUpdateLogLn("Merged laps: " + fromLap + " to " + toLap);
        fitFile.appendTempUpdateLogLn("-- New lap " + (toLap-1) + " time: " + PehoUtils.sec2minSecLong(timerSumOfLaps) + " min, dist: " + Math.round(distSumOfLaps) + " m");

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

        // Print and save logs
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }

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

        Mesg firstLap = fitFile.getLapMesg().get(lapIx);
        Mesg secondLap = new Mesg(firstLap);

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

        setLongIfPresent(secondLap, FitFile.LAP_STIME, splitTime);

        LapBoundaryValues boundaryBeforeFirstLap = getBoundaryBeforeLap(lapIx);
        LapBoundaryValues firstLapBoundary = recalculateLapValuesFromRecords(
            firstLap,
            originalLapStartRecordIx,
            splitRecordIx - 1,
            boundaryBeforeFirstLap);
        LapBoundaryValues secondLapBoundary = recalculateLapValuesFromRecords(
            secondLap,
            splitRecordIx,
            originalLapEndRecordIx,
            firstLapBoundary);

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

        incrementLapReferencesAfterInsertedLap(lapIx, secondLap, insertedTimeInZone);

        fitFile.setNumberOfLaps(fitFile.getNumberOfLaps() + 1);
        if (!fitFile.getSessionMesg().isEmpty()) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_LAPS, fitFile.getNumberOfLaps());
        }

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

    private int findLapMesgIndexInAllMesgByLapIx(int lapIx) {
        return FindMesgIx.findMesgIndexByIntField(fitFile.getAllMesg(), MesgNum.LAP, FitFile.LAP_IX, lapIx);
    }

    private int findFirstRecordIndexAtOrAfterTimer(Long totalTimer) {
        for (int i = 0; i < fitFile.getRecordMesgAddOnRecords().size(); i++) {
            Long timer = fitFile.getRecordMesgAddOnRecords().get(i).getTimer();
            if (timer != null && timer >= totalTimer) {
                return i;
            }
        }
        return fitFile.getRecordMesgAddOnRecords().size() - 1;
    }

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

        return nextLapStartRecordIx - 1;
    }

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

    private LapBoundaryValues recalculateLapValuesFromRecords(Mesg lapMesg, int recordIxStart, int recordIxEnd, LapBoundaryValues previousBoundary) {
        Mesg startRecord = fitFile.getRecordMesg().get(recordIxStart);
        Mesg endRecord = fitFile.getRecordMesg().get(recordIxEnd);

        Long startTime = startRecord.getFieldLongValue(FitFile.REC_TIME);
        Long endTime = endRecord.getFieldLongValue(FitFile.REC_TIME);
        Float endDistance = endRecord.getFieldFloatValue(FitFile.REC_DIST);
        Long endTimer = fitFile.getRecordMesgAddOnRecords().get(recordIxEnd).getTimer();

        float lapDistance = endDistance != null ? Math.max(0f, endDistance - previousBoundary.lastDistance) : 0f;
        float lapTimer = endTimer != null ? Math.max(0L, endTimer - previousBoundary.lastTimer) : 0L;
        float lapElapsed = (endTime != null && previousBoundary.lastRecordTime != null)
            ? Math.max(0L, endTime - previousBoundary.lastRecordTime)
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
        setLongIfPresent(lapMesg, FitFile.LAP_TIME, endTime);

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
        setFloatOrWholeIfPresent(lapMesg, FitFile.LAP_ASC, ascent);
        setFloatOrWholeIfPresent(lapMesg, FitFile.LAP_DESC, descent);

        setWholeNumberIfPresent(lapMesg, FitFile.LAP_HR, hrCount > 0 ? Math.round((float) hrSum / hrCount) : 0);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_MHR, hrMax);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_POW, powerCount > 0 ? Math.round((float) powerSum / powerCount) : 0);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_MPOW, powerMax);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_CAD, cadenceCount > 0 ? Math.round((float) cadenceSum / cadenceCount) : 0);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_MCAD, cadenceMax);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_TEMP, tempCount > 0 ? Math.round((float) tempSum / tempCount) : 0);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_MTEMP, tempCount > 0 ? tempMax : 0);
        setWholeNumberIfPresent(lapMesg, FitFile.LAP_MINTEMP, tempCount > 0 ? tempMin : 0);

        fitFile.appendTempUpdateLogLn("-- Recalculated lap metrics from records ix " + recordIxStart + "-" + recordIxEnd
            + ": timer=" + PehoUtils.sec2minSecLong(lapTimer)
            + ", dist=" + Math.round(lapDistance) + "m"
            + ", hrAvg=" + (hrCount > 0 ? Math.round((float) hrSum / hrCount) : 0)
            + ", speed=" + PehoUtils.mps2minpkm(avgSpeed));

        return new LapBoundaryValues(lapDistance + previousBoundary.lastDistance, endTimer != null ? endTimer : previousBoundary.lastTimer, endTime, startTime, lapTimer);
    }

    private void incrementLapReferencesAfterInsertedLap(int insertedAfterLapIx, Mesg insertedLap, Mesg insertedTimeInZone) {
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
            }
        }
    }

    private void setFloatIfPresent(Mesg mesg, int fieldNum, float value) {
        if (mesg.getFieldFloatValue(fieldNum) != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    private void setFloatOrWholeIfPresent(Mesg mesg, int fieldNum, float value) {
        if (mesg.getFieldFloatValue(fieldNum) != null) {
            mesg.setFieldValue(fieldNum, value);
            return;
        }
        setWholeNumberIfPresent(mesg, fieldNum, Math.round(value));
    }

    private void setWholeNumberIfPresent(Mesg mesg, int fieldNum, int value) {
        Byte byteValue = mesg.getFieldByteValue(fieldNum);
        if (byteValue != null) {
            mesg.setFieldValue(fieldNum, (byte) value);
            return;
        }

        Integer intValue = mesg.getFieldIntegerValue(fieldNum);
        if (intValue != null) {
            mesg.setFieldValue(fieldNum, value);
            return;
        }

        Short shortValue = mesg.getFieldShortValue(fieldNum);
        if (shortValue != null) {
            mesg.setFieldValue(fieldNum, (short) value);
            return;
        }

        Long longValue = mesg.getFieldLongValue(fieldNum);
        if (longValue != null) {
            mesg.setFieldValue(fieldNum, (long) value);
        }
    }

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

    private void setLongIfPresent(Mesg mesg, int fieldNum, Long value) {
        if (value == null) {
            return;
        }
        if (mesg.getFieldLongValue(fieldNum) != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

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

    private int findLapMesgIndexInLapMesgByLapIx(int lapIx) {
        return FindMesgIx.findMesgIndexByIntField(fitFile.getLapMesg(), MesgNum.LAP, FitFile.LAP_IX, lapIx);
    }

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
            }
        }
    }

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